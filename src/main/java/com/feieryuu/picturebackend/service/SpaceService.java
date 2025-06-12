package com.feieryuu.picturebackend.service;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feieryuu.picturebackend.model.dto.space.SpaceAddRequest;
import com.feieryuu.picturebackend.model.dto.space.SpaceQueryRequest;
import com.feieryuu.picturebackend.model.entity.Space;
import com.feieryuu.picturebackend.model.entity.User;
import com.feieryuu.picturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface SpaceService extends IService<Space> {
    /**
     * 获取查询条件
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);


    /**
     * 获取空间包装类
     * @param space
     * @param request
     * @return
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 分页获取空间包装类
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 空间校验
     * @param space
     * @param add 是否为创建时校验
     */
    void validSpace(Space space, boolean add);

    /**
     *  根据空间级别填充空间对象
     * @param space
     */
     void fillSpaceBySpaceLevel(Space space);

    /**
     * 添加空间
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
     long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    /**
     * 校验空间权限
     * @param loginUser
     * @param space
     */
    void checkSpaceAuth(User loginUser, Space space);
}
