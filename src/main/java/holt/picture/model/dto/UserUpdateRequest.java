package holt.picture.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Request object for changing user information
 * @author Weiyang Wu
 * @date 2025/4/2 15:48
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * user name
     */
    private String userName;

    /**
     * avatar
     */
    private String userAvatar;

    /**
     * profile
     */
    private String userProfile;

    /**
     * Roles：user/admin
     */
    private String userRole;
}
