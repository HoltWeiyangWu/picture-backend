package holt.picture.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * Request object for adding user (admin only)
 * @author Weiyang Wu
 * @date 2025/4/2 15:46
 */
@Data
public class UserAddRequest implements Serializable {
    /**
     * account
     */
    private String userAccount;

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
