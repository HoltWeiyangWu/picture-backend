package holt.picture.dto;

import holt.picture.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Request object for requesting a page of users
 * Each field of this class represents a query field
 * @author Weiyang Wu
 * @date 2025/4/2 15:50
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
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
     * profile
     */
    private String userProfile;

    /**
     * Roles：user/admin
     */
    private String userRole;
}
