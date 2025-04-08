package com.feieryuu.picturebackend.crawlStrategy;

import com.feieryuu.picturebackend.model.dto.picture.PictureUploadByBatchRequest;

import java.util.List;

public interface ImageCrawlStrategy {

    /**
     * 爬取图片策略接口
     * @param pictureUploadByBatchRequest
     * @return
     */
    List<String> crawlImages(PictureUploadByBatchRequest pictureUploadByBatchRequest);
}