package com.feieryuu.picturebackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.feieryuu.picturebackend.model.entity.Picture;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class PictureTagCategory implements Serializable {
    /**
     *
     * 标签列表
     */
    private List<String> tagList;


    /**
     * 分类列表
     */
    private List<String> categoryList;
}
