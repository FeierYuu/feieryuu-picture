package com.feieryuu;

import com.feieryuu.picturebackend.api.imageSearch.sub.GetImageFirstUrlApi;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: fetchPicture
 * Description:
 * date: 2025/4/9 5:05
 *
 * @author 飞飞鱼
 * @since JDK 1.8
 */
public class fetchPicture {
    @Test
    public void test1() throws IOException {
        String query = "动漫";
        String fetchUrl = String.format("https://www.bizhihui.com/search.php?q=%s&page=1", query);
        Document document = Jsoup.connect(fetchUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .get();

        // 使用组合选择器定位元素
        Elements imgElements = document.select("a.item-img > img");

        // 提取绝对路径的src
        List<String> imgUrls = imgElements.stream()
                .map(element -> element.absUrl("src"))
                .filter(src -> !src.isEmpty())
                .collect(Collectors.toList());

        List<String> originalUrls = imgUrls.stream()
                .map(url -> {
                    int index = url.lastIndexOf("-pcthumbs");
                    return index != -1 ? url.substring(0, index) : url;
                })
                .collect(Collectors.toList());

        // 输出结果
        originalUrls.forEach(System.out::println);
        System.out.println(originalUrls.size());
    }


    @Test
    public void test2() throws IOException {
        String query = "原神";
        String fetchUrl = String.format("https://wallhere.com/zh/wallpapers?q=%s&page=1", query);
        Document document = Jsoup.connect(fetchUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .get();
        System.out.println(document);
    }



}
