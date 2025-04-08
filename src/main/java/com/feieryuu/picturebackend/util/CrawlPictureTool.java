package com.feieryuu.picturebackend.util;

import cn.hutool.core.util.StrUtil;
import com.feieryuu.picturebackend.model.dto.picture.PictureUploadByBatchRequest;
import com.feieryuu.picturebackend.model.dto.picture.PictureUploadRequest;
import com.feieryuu.picturebackend.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * ClassName: CrawlPictureTool
 * Description:
 * date: 2025/4/9 5:52
 *
 * @author 飞飞鱼
 * @since JDK 1.8
 */
@Slf4j
public class CrawlPictureTool {


    /**
     * 高质量图片爬取核心方法
     */
    public  List<String> fetchHighQualityImages(String searchText, int targetCount) {
        List<String> resultUrls = new ArrayList<>(targetCount);
        int currentPage = 1;
        boolean hasNextPage = true;
        final int maxRetry = 3;
        final int requestDelay = 1500;

        while (hasNextPage && resultUrls.size() < targetCount) {
            log.info("正在爬取第 {} 页，目标数量 {}", currentPage, targetCount);

            // 带重试机制的页面请求
            List<String> pageUrls = fetchPageWithRetry(searchText, currentPage, maxRetry);
            resultUrls.addAll(pageUrls);

            // 数量控制
            if (resultUrls.size() > targetCount) {
                resultUrls = resultUrls.subList(0, targetCount);
                break;
            }

            // 检查下一页
            hasNextPage = checkNextPage(searchText, currentPage);
            currentPage++;

            try {
                Thread.sleep(requestDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return resultUrls;
    }

    /**
     * 带重试的页面抓取
     */
    private List<String> fetchPageWithRetry(String query, int page, int maxRetry) {
        for (int retry = 0; retry < maxRetry; retry++) {
            try {
                String url = buildSearchUrl(query, page);
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .timeout(10000)
                        .header("Accept-Language", "zh-CN,zh;q=0.9")
                        .get();

                return parseImageUrls(doc);
            } catch (IOException e) {
                log.warn("第 {} 页第 {} 次重试失败 - {}", page, retry+1, e.getMessage());
            }
        }
        return Collections.emptyList();
    }

    /**
     * 解析图片地址
     */
    private List<String> parseImageUrls(Document doc) {
        return doc.select("a.item-img > img[src]").stream()
                .map(element -> {
                    String src = element.absUrl("src");
                    // 去除缩略图后缀
                    int index = src.lastIndexOf("-pcthumbs");
                    return index != -1 ? src.substring(0, index) : src;
                })
                .filter(this::isValidImageUrl)
                .collect(Collectors.toList());
    }

    /**
     * 图片质量校验
     */
    private boolean isValidImageUrl(String url) {
        // 示例校验规则（根据实际需求调整）
        return !url.contains("thumbnail")
                && url.matches(".*\\.(jpg|jpeg|png)$")
                && url.length() > 30;
    }

    /**
     * 构造搜索URL
     */
    private String buildSearchUrl(String query, int page) {
        return String.format("https://www.bizhihui.com/search.php?q=%s&page=%d",
                URLEncoder.encode(query, StandardCharsets.UTF_8), page);
    }

    /**
     * 检查是否存在下一页
     */
    private boolean checkNextPage(String query, int currentPage) {
        try {
            Document doc = Jsoup.connect(buildSearchUrl(query, currentPage))
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(5000)
                    .get();
            return doc.selectFirst("#npage a") != null;
        } catch (IOException e) {
            log.warn("下一页检查失败", e);
            return false;
        }
    }


}
