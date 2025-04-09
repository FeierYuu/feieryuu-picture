package com.feieryuu.picturebackend.crawlStrategy.impl;

import com.feieryuu.picturebackend.crawlStrategy.ImageCrawlStrategy;
import com.feieryuu.picturebackend.model.dto.picture.PictureUploadByBatchRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: wallhereCrawlStrategy
 * Description:
 * date: 2025/4/9 8:35
 *
 * @author 飞飞鱼
 * @since JDK 1.8
 */
@Service("wallhereStrategy")
public class wallhereCrawlStrategy implements ImageCrawlStrategy {

    private static final String BASE_URL = "https://wallhere.com";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    private static final int DELAY_MS = 2000; // 2秒请求间隔

    private static final int PAGE_SIZE = 24; // 每页固定数量

    @Override
    public List<String> crawlImages(PictureUploadByBatchRequest request) {
        List<String> results = new ArrayList<>();
        int targetCount = request.getCount();
        String query = request.getSearchText();

        if (targetCount <= 0) return results;

        try {
            // 计算需要抓取的页数
            int totalPages = calculateTotalPages(targetCount);
            int remaining = targetCount;

            for (int page = 1; page <= totalPages; page++) {
                // 计算当前页需要抓取的数量
                int currentPageSize = (page == totalPages)
                        ? (targetCount % PAGE_SIZE == 0 ? PAGE_SIZE : targetCount % PAGE_SIZE)
                        : PAGE_SIZE;

                String listUrl = String.format("%s/zh/wallpapers?q=%s&page=%d",
                        BASE_URL, query, page);

                System.out.printf("正在处理第 %d/%d 页，需要获取 %d 张 [%s]%n",
                        page, totalPages, currentPageSize, listUrl);

                Document listDoc = Jsoup.connect(listUrl)
                        .userAgent(USER_AGENT)
                        .timeout(10000)
                        .get();

                Elements items = listDoc.select(".hub-mediagrid .item-container");
                System.out.println("找到 " + items.size() + " 个图片项");

                // 实际可抓取数量取最小值
                int realSize = Math.min(items.size(), currentPageSize);
                System.out.println("实际将处理 " + realSize + " 项");

                for (int i = 0; i < realSize; i++) {
                    Element item = items.get(i);
                    Element link = item.selectFirst("a.current-item-photo");
                    if (link == null) continue;

                    String detailUrl = BASE_URL + link.attr("href");
                    System.out.printf("进度：%d/%d | 处理：%s%n",
                            results.size() + 1, targetCount, detailUrl);

                    String imageUrl = parseDetailPage(detailUrl);
                    if (imageUrl != null) {
                        results.add(imageUrl);
                        remaining--;

                        // 达到目标数量提前退出
                        if (remaining <= 0) break;
                    }

                    Thread.sleep(DELAY_MS);
                }

                // 提前完成条件
                if (remaining <= 0) break;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("抓取完成，成功获取 %d/%d 张图片%n",
                results.size(), targetCount);
        return results;
    }


    private String parseDetailPage(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .get();

            // 通过CSS选择器定位目标元素
            Element photoModal = doc.selectFirst(".hub-photo .hub-photomodal");
            if (photoModal == null) {
                System.out.println("未找到hub-photomodal元素");
                return null;
            }

            Element aTag = photoModal.selectFirst("a");
            if (aTag == null) {
                System.out.println("未找到a标签");
                return null;
            }

            // 获取绝对地址并清理参数
            String imageUrl = aTag.attr("abs:href");
            return imageUrl.replaceAll("\\?.*", ""); // 移除URL参数

        } catch (IOException e) {
            System.err.println("请求详情页失败: " + url);
            return null;
        }
    }

    // 计算需要抓取的总页数
    private int calculateTotalPages(int targetCount) {
        return (targetCount + PAGE_SIZE - 1) / PAGE_SIZE;
    }

}
