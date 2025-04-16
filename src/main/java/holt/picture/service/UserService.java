package holt.picture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import holt.picture.model.dto.user.UserQueryRequest;
import holt.picture.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import holt.picture.model.vo.LoginUserVO;
import holt.picture.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

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
     * Get user information based on session cookie
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * Log out user and remove session
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * Get user view object from user object
     */
    UserVO getUserVO(User user);

    /**
     * Get a list of user view objects from user objects
     */
    List<UserVO> getUserVOList(List<User> users);

    /**
     * Customised query wrapper to handle SQL queries
     */
    QueryWrapper<User> getUserQueryWrapper(UserQueryRequest queryRequest);

    Boolean isAdmin(User user);
}
