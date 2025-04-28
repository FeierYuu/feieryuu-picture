package com.feieryuu.picturebackend.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.feieryuu.picturebackend.exception.ErrorCode;
import com.feieryuu.picturebackend.exception.ThrowUtils;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * url 图片上传
 */
@Service
public class UrlPictureUpload extends PictureUploadTemplate {  
    @Override  
    protected void validPicture(Object inputSource) {  
        String fileUrl = (String) inputSource;  
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "文件地址不能为空");
        // ... 跟之前的校验逻辑保持一致  
    }  
  
    @Override  
    protected String getOriginFilename(Object inputSource) {  
        String fileUrl = (String) inputSource;  
        // 从 URL 中提取文件名
        // FileUtil.mainName(fileUrl); 会导致获取不到文件名的后缀 采用getName 获取完整的文件名
//        return FileUtil.mainName(fileUrl);
        return  getValidFileName(fileUrl);
    }  
  
    @Override  
    protected void processFile(Object inputSource, File file) throws Exception {
        String fileUrl = (String) inputSource;  
        // 下载文件到临时目录  
        HttpUtil.downloadFile(fileUrl, file);
    }
    private String getValidFileName(String orginFileName){
        int queryIndex = orginFileName.indexOf("?");
        if(queryIndex != -1){
           orginFileName = orginFileName.substring(0, queryIndex);
        }
        return orginFileName;
    }
}
