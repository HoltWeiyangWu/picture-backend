package holt.picture.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Weiyang Wu
 * @date 2025/6/3 14:35
 */
@Data
public class UserEditRequest implements Serializable {

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
}
