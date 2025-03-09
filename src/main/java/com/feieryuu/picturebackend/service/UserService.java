package com.feieryuu.picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.feieryuu.picturebackend.model.dto.user.UserQueryRequest;
import com.feieryuu.picturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feieryuu.picturebackend.model.vo.UserLoginVo;
import com.feieryuu.picturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);


    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    UserLoginVo userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 获取登入后的用户信息
     * @param httpServletRequest
     * @return
     */
    User getLoginUser(HttpServletRequest httpServletRequest);
    /**
     * 获取加密后的密码
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获得脱敏后的登入信息
     * @param user
     * @return
     */
    UserLoginVo getUserLoginVo(User user);


    /**
     * 获取脱敏后的用户信息
     * @param user
     * @return
     */
    UserVO getUserVo(User user);


    /**
     * 获取脱敏后的用户信息列表
     * @param userList
     * @return
     */
    List<UserVO> getUserVoList(List<User> userList);
    /**
     * 用户注销
     *
     */
    boolean userLogout(HttpServletRequest request);


    /**
     * 获取查询条件
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest queryWrapper);


    /**
     * 是否为管理员
     * @param user
     * @return
     */
    boolean isAdmin(User user);
}
