package com.feieryuu.picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feieryuu.picturebackend.model.dto.space.SpaceAddRequest;
import com.feieryuu.picturebackend.model.dto.space.SpaceQueryRequest;
import com.feieryuu.picturebackend.model.dto.spaceUser.SpaceUserAddRequest;
import com.feieryuu.picturebackend.model.dto.spaceUser.SpaceUserQueryRequest;
import com.feieryuu.picturebackend.model.entity.Space;
import com.feieryuu.picturebackend.model.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feieryuu.picturebackend.model.entity.User;
import com.feieryuu.picturebackend.model.vo.SpaceUserVO;
import com.feieryuu.picturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
public interface SpaceUserService extends IService<SpaceUser> {
    /**
     * 获取查询条件
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);


    /**
     * 获取空间成员包装类
     * @param spaceUser
     * @param request
     * @return
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 获取空间成员包装类
     * @param spaceUsers
     * @param request
     * @return
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUsers, HttpServletRequest request);
    /**
     * 空间校验
     * @param spaceUser
     * @param add 是否为创建时校验
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);


    /**
     * 添加空间
     * @param spaceUserAddRequest
     * @return
     */
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

}
