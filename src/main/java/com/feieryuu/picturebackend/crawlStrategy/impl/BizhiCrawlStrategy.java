package com.feieryuu.picturebackend.crawlStrategy.impl;

import com.feieryuu.picturebackend.crawlStrategy.ImageCrawlStrategy;
import com.feieryuu.picturebackend.exception.BusinessException;
import com.feieryuu.picturebackend.exception.ErrorCode;
import com.feieryuu.picturebackend.model.dto.picture.PictureUploadByBatchRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("bizhiStrategy")
public class BizhiCrawlStrategy implements ImageCrawlStrategy {
    private static final String BIZHI_SEARCH_URL = "https://www.bizhihui.com/search.php?q=%s&page=%d";

    @Override
    public List<String> crawlImages(PictureUploadByBatchRequest pictureUploadByBatchRequest) {
        List<String> imageUrls = new ArrayList<>();
        int page = 1;
        
        while (imageUrls.size() < pictureUploadByBatchRequest.getCount()) {
            try {
                Document doc = Jsoup.connect(String.format(BIZHI_SEARCH_URL, pictureUploadByBatchRequest.getSearchText(), page))
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .get();

                Elements imgElements = doc.select("a.item-img > img");
                
                for (Element img : imgElements) {
                    if (imageUrls.size() >= pictureUploadByBatchRequest.getCount()) break;
                    
                    String src = img.absUrl("src");
                    int index = src.lastIndexOf("-pcthumbs");
                    if (index != -1) {
                        src = src.substring(0, index);
                    }
                    imageUrls.add(src);
                }
                
                page++;
            } catch (IOException e) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"壁纸汇图片爬取失败");
            }
        }
        
        return imageUrls.subList(0, Math.min(pictureUploadByBatchRequest.getCount(), imageUrls.size()));
    }
}