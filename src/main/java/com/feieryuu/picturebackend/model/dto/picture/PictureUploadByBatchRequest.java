package com.feieryuu.picturebackend.model.dto.picture;

import lombok.Data;

import java.util.List;

/**
 * 批量抓取图片请求
 */
@Data
public class PictureUploadByBatchRequest {  
  
    /**  
     * 搜索词  
     */  
    private String searchText;


    /**
     * 名称前缀
     */
    private String namePrefix;


    /**  
     * 抓取数量  
     */  
    private Integer count = 10;



    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 下载源
     */
    private String source;
}
