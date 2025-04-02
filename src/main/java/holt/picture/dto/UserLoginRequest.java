package holt.picture.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Request object for user login
 * @author Weiyang Wu
 * @date 2025/4/1 19:50
 */
@Data
public class UserLoginRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Account
     */
    private String userAccount;

    /**
     * Password to register
     */
    private String userPassword;
}
