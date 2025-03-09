package com.feieryuu.picturebackend.aop;

import com.feieryuu.picturebackend.annotation.AuthCheck;
import com.feieryuu.picturebackend.exception.BusinessException;
import com.feieryuu.picturebackend.exception.ErrorCode;
import com.feieryuu.picturebackend.model.entity.User;
import com.feieryuu.picturebackend.model.enums.UserRoleEnum;
import com.feieryuu.picturebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * ClassName: AuthInterceptor
 * Description: 用户权限校验拦截器
 * date: 2025/2/23 18:49
 *
 * @author 飞飞鱼
 * @since JDK 1.8
 */
@Aspect
@Component
public class AuthInterceptor {
    @Resource
    private UserService userService;

    /**
     * 执行校验权限
     * @param proceedingJoinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptorD(ProceedingJoinPoint proceedingJoinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request= ((ServletRequestAttributes) requestAttributes).getRequest();

        //当前用户登入信息
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);

        //不需要权限放行
        if (mustRoleEnum == null){
            proceedingJoinPoint.proceed();
        }
        //必须有权限才能放行
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        //没有权限拒绝
        if (userRoleEnum == null){
            throw  new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        //要求必须是管理员权限 但用户不是管理员权限 拒绝放行
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)){
            throw  new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //通过校验 放行
        return proceedingJoinPoint.proceed();
    }



}
