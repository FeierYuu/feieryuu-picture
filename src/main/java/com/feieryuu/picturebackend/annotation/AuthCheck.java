package com.feieryuu.picturebackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: AuthCheck
 * Description: 权限校验
 * date: 2025/2/23 18:45
 *
 * @author 飞飞鱼
 * @since JDK 1.8
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    /**
     * 必须具有某个角色
     * @return
     */
    String mustRole() default "";
}
