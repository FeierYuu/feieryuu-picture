package com.feieryuu.picturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feieryuu.picturebackend.constant.UserConstant;
import com.feieryuu.picturebackend.exception.BusinessException;
import com.feieryuu.picturebackend.exception.ErrorCode;
import com.feieryuu.picturebackend.exception.ThrowUtils;
import com.feieryuu.picturebackend.manager.auth.StpKit;
import com.feieryuu.picturebackend.model.dto.user.UserQueryRequest;
import com.feieryuu.picturebackend.model.entity.User;
import com.feieryuu.picturebackend.model.enums.UserRoleEnum;
import com.feieryuu.picturebackend.model.vo.UserLoginVo;
import com.feieryuu.picturebackend.model.vo.UserVO;
import com.feieryuu.picturebackend.service.UserService;
import com.feieryuu.picturebackend.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //参数校验
        if (StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }

        if (userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }

        if (userPassword.length()<8 || checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if (!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
        }

        //检查是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = this.baseMapper.selectCount(queryWrapper);
        if (count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }

        //密码加密
        String password = getEncryptPassword(userPassword);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(password);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());

        boolean saveResult = this.save(user);
        if (!saveResult){
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"注册失败,数据库错误");
        }
        return user.getId();
    }

    /**
     * 用户登入
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return
     */
    @Override
    public UserLoginVo userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        //参数校验
        if (StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }

        if (userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号错误");
        }
        if (userPassword.length()<8 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码错误");
        }

        //密码加密
        String password = getEncryptPassword(userPassword);



        //检查账号是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", password);
        User user = this.baseMapper.selectOne(queryWrapper);
        if (user==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号或密码错误");
        }

        //记录用户的登入状态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,user);
        //给sa-token 写入登入账号信息 便于空间鉴权时使用
        StpKit.SPACE.login(user.getId());
        StpKit.SPACE.getSession().set(UserConstant.USER_LOGIN_STATE,user);
        return this.getUserLoginVo(user);
    }


    /**
     * 获取登入后的用户信息
     * @param httpServletRequest
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest httpServletRequest) {
        //判断是否登入
        Object userObj = httpServletRequest.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null  || currentUser.getId()==null){
            throw  new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        //从数据库查询
        currentUser = this.getById(currentUser.getId());
        if (currentUser == null){
            throw  new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }


    /**
     * 密码加密
     * @param userPassword
     * @return
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "feieryuu";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }



    /**
     * 获得脱敏后的登入信息
     * @param user
     * @return
     */
    @Override
    public UserLoginVo getUserLoginVo(User user) {
        UserLoginVo userLoginVo = new UserLoginVo();
        BeanUtils.copyProperties(user, userLoginVo);
        return userLoginVo;
    }
    /**
     * 获取脱敏后的用户信息
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVo(User user) {
        if (user==null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取脱敏后的用户信息列表
     * @param userList
     * @return
     */
    @Override
    public List<UserVO> getUserVoList(List<User> userList) {
        if (CollUtil.isEmpty(userList)){
            return new ArrayList<>();
        }
        return userList.stream().map(this:: getUserVo).collect(Collectors.toList());
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @Override
        public boolean userLogout(HttpServletRequest request) {
        //判断是否登入
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null ){
            throw  new BusinessException(ErrorCode.OPERATION_ERROR,"用户未登录");
        }
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest queryWrapper) {
        if (queryWrapper==null){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }

        Long id = queryWrapper.getId();
        String userName = queryWrapper.getUserName();
        String userAccount = queryWrapper.getUserAccount();
        String userProfile = queryWrapper.getUserProfile();
        String userRole = queryWrapper.getUserRole();

        String sortField = queryWrapper.getSortField();
        String sortOrder = queryWrapper.getSortOrder();

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq(ObjectUtil.isNotNull(id), "id",id);
        userQueryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        userQueryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        userQueryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        userQueryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        userQueryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);

        return userQueryWrapper;
    }

    /**
     * 判断是否是管理员
     * @param user
     * @return
     */
    @Override
    public boolean isAdmin(User user) {
        return user!=null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }


}




