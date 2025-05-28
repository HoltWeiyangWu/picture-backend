package holt.picture.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import holt.picture.manager.auth.model.SpaceUserAuthConfig;
import holt.picture.manager.auth.model.SpaceUserRole;
import holt.picture.model.Space;
import holt.picture.model.SpaceUser;
import holt.picture.model.User;
import holt.picture.model.enums.SpaceRoleEnum;
import holt.picture.model.enums.SpaceTypeEnum;
import holt.picture.service.SpaceUserService;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes a JSON configuration file and provides permission list according to user role
 * @author Weiyang Wu
 * @date 2025/5/23 21:36
 */
@Component
public class SpaceUserAuthManager {

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private UserService userService;

    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    static {
        String json = ResourceUtil.readUtf8Str("business/spaceUserAuthConfig.json");
        SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    /**
     * Get a list of permissions according to a user role
     */
    public List<String> getPermissionsByRole(String role) {
        if (StrUtil.isBlank(role)) {
            return new ArrayList<>();
        }
        SpaceUserRole userRole = SPACE_USER_AUTH_CONFIG.getRoles().stream()
                .filter(r-> role.equals(r.getKey()))
                .findFirst().
                orElse(null);
        if (userRole == null) {
            return new ArrayList<>();
        }
        return userRole.getPermissions();
    }

    /**
     * Return a list of permission for frontend display
     */
    public List<String> getPermissionList(Space space, User loginUser) {
        if(loginUser == null) {
            return new ArrayList<>();
        }

        List<String> ADMIN_PERMISSIONS = getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // Public storage space
        if (space == null) {
            if (userService.isAdmin(loginUser)) {
                return ADMIN_PERMISSIONS;
            } else {
                return new ArrayList<>();
            }
        }

        // Get permission list according to the type of space
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(space.getSpaceType());
        if (spaceTypeEnum == null) {
            return new ArrayList<>();
        }
        switch (spaceTypeEnum) {
            case PRIVATE -> {
                boolean isCreator = space.getCreatorId().equals(loginUser.getId());
                boolean isAdmin = userService.isAdmin(loginUser);
                if (isCreator || isAdmin) {
                    return ADMIN_PERMISSIONS;
                } else {
                    return new ArrayList<>();
                }
            }
            case TEAM -> {
                SpaceUser spaceUser = spaceUserService.lambdaQuery()
                        .eq(SpaceUser::getUserId, loginUser.getId())
                        .eq(SpaceUser::getSpaceId, space.getId())
                        .one();
                if (spaceUser == null) {
                    return new ArrayList<>();
                } else {
                    return getPermissionsByRole(spaceUser.getRole());
                }
            }
        }
        return new ArrayList<>();
    }
}
