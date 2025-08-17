package com.feieryuu.picturebackend.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import com.feieryuu.picturebackend.manager.auth.model.SpaceUserAuthConfig;
import com.feieryuu.picturebackend.manager.auth.model.SpaceUserRole;
import com.feieryuu.picturebackend.model.entity.Space;
import com.feieryuu.picturebackend.model.entity.SpaceUser;
import com.feieryuu.picturebackend.model.entity.User;
import com.feieryuu.picturebackend.model.enums.SpaceRoleEnum;
import com.feieryuu.picturebackend.model.enums.SpaceTypeEnum;
import com.feieryuu.picturebackend.service.SpaceUserService;
import com.feieryuu.picturebackend.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: SpaceUserAuthManager
 * Description: 空间成员权限管理器
 * date: 2025/8/3 3:12
 *
 * @author 飞飞鱼
 * @since JDK 1.8
 */

@Component
public class SpaceUserAuthManager {
    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    @Resource
    private UserService userService;

    @Resource
    private SpaceUserService spaceUserService;

    /**
     * 类加载时初始化权限配置
     */
    static {
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
        SpaceUserAuthConfig spaceUserAuthConfig = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
        SPACE_USER_AUTH_CONFIG = spaceUserAuthConfig;
    }

    /**
     * 根据角色获取权限类别
     */
    public List< String>getPermissionsByRole(String SpaceUserRole) {
        if (StrUtil.isBlank(SpaceUserRole)){
            return new ArrayList<>();
        }
       SpaceUserRole role =SPACE_USER_AUTH_CONFIG.getRoles()
                .stream()
                .filter(r-> r.getKey().equals(SpaceUserRole))
                .findFirst()
                .orElse(null);
        if (role == null){
            new ArrayList<>();
        }
        return role.getPermissions();
    }


    public List<String> getPermissionList(Space space, User loginUser) {
        if (loginUser == null) {
            return new ArrayList<>();
        }
        // 管理员权限
        List<String> ADMIN_PERMISSIONS = getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // 公共图库
        if (space == null) {
            if (userService.isAdmin(loginUser)) {
                return ADMIN_PERMISSIONS;
            }
            return new ArrayList<>();
        }
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(space.getSpaceType());
        if (spaceTypeEnum == null) {
            return new ArrayList<>();
        }
        // 根据空间获取对应的权限
        switch (spaceTypeEnum) {
            case PRIVATE:
                // 私有空间，仅本人或管理员有所有权限
                if (space.getUserId().equals(loginUser.getId()) || userService.isAdmin(loginUser)) {
                    return ADMIN_PERMISSIONS;
                } else {
                    return new ArrayList<>();
                }
            case TEAM:
                // 团队空间，查询 SpaceUser 并获取角色和权限
                SpaceUser spaceUser = spaceUserService.lambdaQuery()
                        .eq(SpaceUser::getSpaceId, space.getId())
                        .eq(SpaceUser::getUserId, loginUser.getId())
                        .one();
                if (spaceUser == null) {
                    return new ArrayList<>();
                } else {
                    return getPermissionsByRole(spaceUser.getSpaceRole());
                }
        }
        return new ArrayList<>();
    }

}
