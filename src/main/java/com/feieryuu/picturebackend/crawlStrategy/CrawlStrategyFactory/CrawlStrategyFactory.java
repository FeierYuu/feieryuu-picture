package com.feieryuu.picturebackend.crawlStrategy.CrawlStrategyFactory;

import com.feieryuu.picturebackend.crawlStrategy.ImageCrawlStrategy;
import com.feieryuu.picturebackend.exception.BusinessException;
import com.feieryuu.picturebackend.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class CrawlStrategyFactory {
    private final Map<String, ImageCrawlStrategy> strategyMap;

    @Autowired
    public CrawlStrategyFactory(
            @Qualifier("bingStrategy") ImageCrawlStrategy bingStrategy,
            @Qualifier("bizhiStrategy") ImageCrawlStrategy bizhiStrategy) {
        strategyMap = new HashMap<>();
        strategyMap.put("bing", bingStrategy);
        strategyMap.put("bizhi", bizhiStrategy);
    }

    public ImageCrawlStrategy getStrategy(String source) {
        ImageCrawlStrategy strategy = strategyMap.get(source.toLowerCase());
        if (strategy == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的下载源类型");
        }
        return strategy;
    }
}