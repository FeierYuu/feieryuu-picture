package com.feieryuu.picturebackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feieryuu.picturebackend.annotation.AuthCheck;
import com.feieryuu.picturebackend.common.BaseResponse;
import com.feieryuu.picturebackend.common.DeleteRequest;
import com.feieryuu.picturebackend.common.ResultUtils;
import com.feieryuu.picturebackend.constant.UserConstant;
import com.feieryuu.picturebackend.exception.BusinessException;
import com.feieryuu.picturebackend.exception.ErrorCode;
import com.feieryuu.picturebackend.exception.ThrowUtils;
import com.feieryuu.picturebackend.model.dto.user.*;
import com.feieryuu.picturebackend.model.entity.User;
import com.feieryuu.picturebackend.model.vo.UserLoginVo;
import com.feieryuu.picturebackend.model.vo.UserVO;
import com.feieryuu.picturebackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

   @Resource
    private UserService userService;


   /**
    * 用户注册
    * @param userRegisterRequest
    * @return
    */
   @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
       ThrowUtils.throwIf(userRegisterRequest==null, ErrorCode.PARAMS_ERROR);
       String userAccount = userRegisterRequest.getUserAccount();
       String userPassword = userRegisterRequest.getUserPassword();
       String checkPassword = userRegisterRequest.getCheckPassword();
       long userId = userService.userRegister(userAccount, userPassword, checkPassword);
       return ResultUtils.success(userId);
   }


   /**
    * 用户登入
    * @param userLoginRequest
    * @param httpServletRequest
    * @return
    */
   @PostMapping("/login")
   public BaseResponse<UserLoginVo> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest){
      ThrowUtils.throwIf(userLoginRequest==null, ErrorCode.PARAMS_ERROR);
      String userAccount = userLoginRequest.getUserAccount();
      String userPassword = userLoginRequest.getUserPassword();
      UserLoginVo userLoginVo = userService.userLogin(userAccount, userPassword, httpServletRequest);
      return ResultUtils.success(userLoginVo);
   }



   /**
    * 获取当前登入用户信息
    */
   @GetMapping("/get/login")
   public BaseResponse<UserLoginVo> getLoginUser(HttpServletRequest httpServletRequest) {
      User loginUser = userService.getLoginUser(httpServletRequest);
      return ResultUtils.success(userService.getUserLoginVo(loginUser));
   }


   /**
    * 用户注销
    */

   @PostMapping("/logout")
   public BaseResponse<Boolean> userLogout(HttpServletRequest httpServletRequest) {
      ThrowUtils.throwIf(httpServletRequest==null, ErrorCode.PARAMS_ERROR);
      boolean userLogout = userService.userLogout(httpServletRequest);
      return ResultUtils.success(userLogout);
   }



   /**
    * 创建用户
    */
   @PostMapping("/add")
   @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
   public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
      ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
      User user = new User();
      BeanUtils.copyProperties(userAddRequest, user);
      // 默认密码 12345678
      final String DEFAULT_PASSWORD = "12345678";
      String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
      user.setUserPassword(encryptPassword);
      boolean result = userService.save(user);
      ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
      return ResultUtils.success(user.getId());
   }

   /**
    * 根据 id 获取用户（仅管理员）
    */
   @GetMapping("/get")
   @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
   public BaseResponse<User> getUserById(long id) {
      ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
      User user = userService.getById(id);
      ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
      return ResultUtils.success(user);
   }

   /**
    * 根据 id 获取包装类
    */
   @GetMapping("/get/vo")
   public BaseResponse<UserVO> getUserVOById(long id) {
      BaseResponse<User> response = getUserById(id);
      User user = response.getData();
      return ResultUtils.success(userService.getUserVo(user));
   }

   /**
    * 删除用户
    */
   @PostMapping("/delete")
   @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
   public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
      if (deleteRequest == null || deleteRequest.getId() <= 0) {
         throw new BusinessException(ErrorCode.PARAMS_ERROR);
      }
      boolean b = userService.removeById(deleteRequest.getId());
      return ResultUtils.success(b);
   }

   /**
    * 更新用户
    */
   @PostMapping("/update")
   @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
   public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
      if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
         throw new BusinessException(ErrorCode.PARAMS_ERROR);
      }
      User user = new User();
      BeanUtils.copyProperties(userUpdateRequest, user);
      boolean result = userService.updateById(user);
      ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
      return ResultUtils.success(true);
   }




   /**
    * 分页获取用户封装列表（仅管理员）
    *
    * @param userQueryRequest 查询请求参数
    */
   @PostMapping("/list/page/vo")
   @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
   public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
      ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
      long current = userQueryRequest.getCurrent();
      long pageSize = userQueryRequest.getPageSize();
      Page<User> userPage = userService.page(new Page<>(current, pageSize),
              userService.getQueryWrapper(userQueryRequest));
      Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
      List<UserVO> userVOList = userService.getUserVoList(userPage.getRecords());
      userVOPage.setRecords(userVOList);
      return ResultUtils.success(userVOPage);
   }

}
