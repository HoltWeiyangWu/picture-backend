package holt.picture.manager.auth.model;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.manager.auth.SpaceUserAuthManager;
import holt.picture.manager.auth.StpKit;
import holt.picture.model.Picture;
import holt.picture.model.Space;
import holt.picture.model.SpaceUser;
import holt.picture.model.User;
import holt.picture.model.enums.SpaceRoleEnum;
import holt.picture.model.enums.SpaceTypeEnum;
import holt.picture.service.PictureService;
import holt.picture.service.SpaceService;
import holt.picture.service.SpaceUserService;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

import static holt.picture.constant.UserConstant.USER_LOGIN_STATE;

/**
 * Define how to load user roles and permission using Sa-Token authentication framework
 * @author Weiyang Wu
 * @date 2025/5/24 8:06
 */
@Component
public class StpInterfaceImpl implements StpInterface {


    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private UserService userService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // Determine the type of login, only give permission for requests related to storage space at this stage
        if (!StpKit.SPACE_TYPE.equals(loginType)) {
            return new ArrayList<>();
        }

        List<String> ADMIN_PERMISSIONS = spaceUserAuthManager.getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        SpaceUserAuthContext authContext = getAuthContextByRequest();

        // User querying for the public space, return admin permission
        if (isAllFieldNull(authContext)) {
            return ADMIN_PERMISSIONS;
        }

        // User has not logged in, throw error
        User loginUser = (User) StpKit.SPACE.getSessionByLoginId(loginId).get(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // Obtain permission through an existing space-user relation object (if exists)
        SpaceUser spaceUser = authContext.getSpaceUser();
        if (spaceUser != null) {
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getRole());
        }

        // Get space-user relation object and obtain permission
        Long userId = loginUser.getId();
        Long spaceUserId = authContext.getSpaceUserId();
        if (spaceUserId != null) {
            spaceUser = spaceUserService.getById(spaceUserId);
            ThrowUtils.throwIf(spaceUser==null, ErrorCode.NOT_FOUND_ERROR,
                    "Can not find space-user relation object");
            SpaceUser loginSpaceUser = spaceUserService.lambdaQuery()
                    .eq(SpaceUser::getSpaceId, spaceUser.getUserId())
                    .eq(SpaceUser::getUserId, userId)
                    .one();
            if (loginSpaceUser == null) {
                return new ArrayList<>();
            }
            return spaceUserAuthManager.getPermissionsByRole(loginSpaceUser.getRole());
        }

        // Get Space object through spaceId or pictureId
        Long spaceId = authContext.getSpaceId();
        if (spaceId == null) { // Get SpaceId through pictureId
            Long pictureId = authContext.getPictureId();
            if (pictureId == null) {
                return ADMIN_PERMISSIONS;
            }
            Picture picture = pictureService.lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .select(Picture::getId, Picture::getSpaceId, Picture::getCreatorId)
                    .one();
            ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR, "No picture found");
            spaceId = picture.getSpaceId();
            // Public space authorisation
            if (spaceId == null) {
                if (picture.getCreatorId().equals(userId) || userService.isAdmin(loginUser)) {
                    return ADMIN_PERMISSIONS;
                } else {
                    return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
                }
            }
        }

        // Get Space through spaceId
        Space space = spaceService.getById(spaceId);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "No space found");
        if(Objects.equals(space.getSpaceType(), SpaceTypeEnum.PRIVATE.getValue())) {
            // Private space, only accessible to creator or admin
            if (space.getCreatorId().equals(userId) || userService.isAdmin(loginUser)) {
                return ADMIN_PERMISSIONS;
            } else {
                return new ArrayList<>();
            }
        } else {
            // Team space, get space user
            spaceUser = spaceUserService.lambdaQuery()
                    .eq(SpaceUser::getSpaceId, spaceId)
                    .eq(SpaceUser::getUserId, userId)
                    .one();
            if (spaceUser == null) {
                return new ArrayList<>();
            }
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getRole());
        }
    }

    @Override
    public List<String> getRoleList(Object o, String s) {
        return List.of();
    }


    /**
     * Get authorisation request parameters via request object
     */
    private SpaceUserAuthContext getAuthContextByRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String contentType = request.getHeader(Header.CONTENT_TYPE.getValue());
        SpaceUserAuthContext authRequest;

        // Read GET/POST request parameters
        if (contentType.equals(ContentType.JSON.getValue())) {
            String body = JakartaServletUtil.getBody(request);
            authRequest = JSONUtil.toBean(body, SpaceUserAuthContext.class);
        } else {
            Map<String, String> paramMap = JakartaServletUtil.getParamMap(request);
            authRequest = BeanUtil.toBean(paramMap, SpaceUserAuthContext.class);
        }

        // Categorise ID according to request context path
        Long id = authRequest.getId();
        if (ObjUtil.isNotNull(id)) {
            String requestUri = request.getRequestURI();
            String partUri = requestUri.replace(contextPath + "/", "");
            String moduleName = StrUtil.subBefore(partUri, "/", false);
            switch (moduleName) {
                case "picture":
                    authRequest.setPictureId(id);
                    break;
                case "spaceUser":
                    authRequest.setSpaceUserId(id);
                    break;
                case "space":
                    authRequest.setSpaceId(id);
                    break;
                default:
                    break;
            }
        }
        return authRequest;
    }

    private boolean isAllFieldNull(Object object) {
        if (object == null) {
            return true;
        }
        return Arrays.stream(ReflectUtil.getFields(object.getClass()))
                .map(field -> ReflectUtil.getFieldValue(object, field))
                .allMatch(ObjectUtil::isEmpty);

    }
}
