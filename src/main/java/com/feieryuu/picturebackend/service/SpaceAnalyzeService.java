package com.feieryuu.picturebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.feieryuu.picturebackend.model.dto.space.analyze.*;
import com.feieryuu.picturebackend.model.entity.Picture;
import com.feieryuu.picturebackend.model.entity.Space;
import com.feieryuu.picturebackend.model.entity.User;
import com.feieryuu.picturebackend.model.vo.space.analyze.*;

import java.util.List;

/**
 * ClassName: SpaceAnalyzeService
 * Description:
 * date: 2025/6/13 1:38
 *
 * @author 飞飞鱼
 * @since JDK 1.8
 */
public interface SpaceAnalyzeService extends IService<Space> {
    /**
    * 获取空间使用分析
    * @param spaceUsageAnalyzeRequest
    * @param loginUser
    * @return
    */
    SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);

    /**
    * 获取空间分类分析
    * @param spaceCategoryAnalyzeRequest
    * @param loginUser
    * @return
    */
    List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser);
    /**
    * 获取空间标签分析
    * @param spaceTagAnalyzeRequest
    * @param loginUser
    * @return
    */
    List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser);
    /**
    * 获取空间大小分析
    * @param spaceSizeAnalyzeRequest
    * @param loginUser
    * @return
    */
    List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser);

    /**
    * 获取空间用户分析
    * @param spaceUserAnalyzeRequest
    * @param loginUser
    * @return
    */
    List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser);
    /**
    * 获取空间排行分析
    * @param spaceRankAnalyzeRequest
    * @param loginUser
    * @return
    */
    List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);
}
