package holt.picture.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Request object for user registration
 * @author Weiyang Wu
 * @date 2025/4/1 19:02
 */
@Data
public class UserRegisterRequest implements Serializable {
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

    /**
     * Confirm password to avoid wrong input
     */
    private String checkPassword;
}
