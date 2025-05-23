package holt.picture.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import holt.picture.manager.auth.model.SpaceUserAuthConfig;
import holt.picture.manager.auth.model.SpaceUserRole;
import holt.picture.service.SpaceUserService;
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


    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    static {
        String json = ResourceUtil.readUtf8Str("business/spaceUserAuth.json");
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
}
