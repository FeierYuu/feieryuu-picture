package com.feieryuu.picturebackend.crawlStrategy.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.feieryuu.picturebackend.crawlStrategy.ImageCrawlStrategy;
import com.feieryuu.picturebackend.exception.BusinessException;
import com.feieryuu.picturebackend.exception.ErrorCode;
import com.feieryuu.picturebackend.model.dto.picture.PictureUploadByBatchRequest;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// src/main/java/com/yourpackage/strategy/impl/BingCrawlStrategy.java
@Service("bingStrategy")
@Slf4j
public class BingCrawlStrategy implements ImageCrawlStrategy {
    private static final String BING_SEARCH_URL = "https://cn.bing.com/images/async?q=%s&mmasync=1";

    @Override
    public List<String> crawlImages(PictureUploadByBatchRequest pictureUploadByBatchRequest) {
        List<String> imageUrls = new ArrayList<>();
        
        try {
            Document doc = Jsoup.connect(String.format(BING_SEARCH_URL, pictureUploadByBatchRequest.getSearchText()))
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get();

            Elements imgElements = doc.select(".iuscp.isv");
            
            for (Element imgElement : imgElements) {
                if (imageUrls.size() >= pictureUploadByBatchRequest.getCount()) break;
                
                String mAttr = imgElement.select(".iusc").attr("m");
                if (StringUtils.isBlank(mAttr)) continue;

                try {
                    Map<String, Object> map = JSONUtil.toBean(mAttr, Map.class);
                    String highResUrl = (String) map.get("murl");
                    if (StringUtils.isNotBlank(highResUrl)) {
                        int queryIndex = highResUrl.indexOf("?");
                        if (queryIndex > -1) {
                            highResUrl = highResUrl.substring(0, queryIndex);
                        }
                        imageUrls.add(highResUrl);
                    }
                } catch (Exception e) {
                    log.error("解析必应图片失败: {}", mAttr, e);
                }
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"必应图片爬取失败");
        }
        
        return imageUrls;
    }
}