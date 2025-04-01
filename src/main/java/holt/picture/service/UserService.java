package holt.picture.service;

import holt.picture.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import holt.picture.model.vo.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author Weiyang Wu
* @date 2025-04-01 17:36:37
*/
public interface UserService extends IService<User> {
    /**
     * Register user with credentials
     */
    long userRegister(String account, String password, String checkPassword);

    /**
     * Encrypt password with MD5
     */
    String getEncryptPassword(String userPassword);

    /**
     * Log in user with credentials and set session cookie
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * Convert User object to user view object
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * Get insensitive user information based on session cookie
     */
    LoginUserVO getLoginUserVO(HttpServletRequest request);
}
