package holt.picture.manager.websocket;

import cn.hutool.core.util.ObjUtil;
import holt.picture.manager.auth.SpaceUserAuthManager;
import holt.picture.manager.auth.model.SpaceUserPermissionConstant;
import holt.picture.model.Picture;
import holt.picture.model.Space;
import holt.picture.model.User;
import holt.picture.model.enums.SpaceTypeEnum;
import holt.picture.service.PictureService;
import holt.picture.service.SpaceService;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Write validation before establishing a session in Websocket interceptor
 * @author Weiyang Wu
 * @date 2025/6/2 14:08
 */
@Component
@Slf4j
public class WsHandshakeInterceptor implements HandshakeInterceptor {
    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    /**
     * Validates parameters and authorisation level before connection
     * Puts records once the connection is established
     */
    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response,
                       @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            String pictureId = servletRequest.getParameter("pictureId");
            // Validate parameter
            if (pictureId == null) {
                log.error("Picture ID does not exist, refuse to handshake");
                return false;
            }
            User loginUser = userService.getLoginUser(servletRequest);
            if (ObjUtil.isEmpty(loginUser)) {
                log.error("User is not logged in, refuse to handshake");
                return false;
            }

            // Validate authorisation level
            Picture picture = pictureService.getById(pictureId);
            if (picture == null) {
                log.error("Picture does not exist, refuse to handshake");
                return false;
            }
            Long spaceId = picture.getSpaceId();
            Space space = null;
            if (spaceId != null) {
                space = spaceService.getById(spaceId);
                if (space == null) {
                    log.error("Space does not exist, refuse to handshake");
                    return false;
                }
                if (!Objects.equals(space.getSpaceType(), SpaceTypeEnum.TEAM.getValue())) {
                    log.error("Space type is not team, refuse to handshake");
                    return false;
                }
            }
            List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
            if (!permissionList.contains(SpaceUserPermissionConstant.PICTURE_EDIT)) {
                log.error("No edit authorisation, refuse to handshake");
                return false;
            }
            attributes.put("user", loginUser);
            attributes.put("userId", loginUser.getId());
            attributes.put("pictureId", Long.valueOf(pictureId));
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {

    }
}
