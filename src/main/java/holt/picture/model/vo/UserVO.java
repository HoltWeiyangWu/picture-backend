package holt.picture.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Weiyang Wu
 * @date 2025/4/2 15:55
 */
@Data
public class UserVO implements Serializable {
    /**
     * id
     */
    private Long id;

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

    /**
     * Create time
     */
    private Date createTime;
}
