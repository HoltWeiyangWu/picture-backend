package holt.picture.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.model.User;
import holt.picture.model.UserRoleEnum;
import holt.picture.model.vo.LoginUserVO;
import holt.picture.service.UserService;
import holt.picture.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import static holt.picture.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author Weiyang Wu
* @date 2025-04-01 17:36:37
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {
    /**
     * Register user with credentials
     */
    @Override
    public long userRegister(String account, String password, String checkPassword) {
        // 1. Parameter validation
        ThrowUtils.throwIf(StrUtil.hasBlank(account, password, checkPassword),
                new BusinessException(ErrorCode.PARAMS_ERROR, "Empty input"));
        ThrowUtils.throwIf(account.length() < 4,
                new BusinessException(ErrorCode.PARAMS_ERROR, "Account length less than 4"));
        ThrowUtils.throwIf(password.length() < 8 || checkPassword.length() < 8,
                new BusinessException(ErrorCode.PARAMS_ERROR, "Password length less than 8"));
        ThrowUtils.throwIf(!password.equals(checkPassword),
                new BusinessException(ErrorCode.PARAMS_ERROR, "Password does not match"));

        // 2. Check if account already exists
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", account);
        long count = this.baseMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(count > 0,
                new BusinessException(ErrorCode.PARAMS_ERROR, "Account already exists"));

        // 3. Encrypt password
        String encryptedPassword = getEncryptPassword(password);
        // 4. Add data to database
        User user = new User();
        user.setUserAccount(account);
        user.setUserPassword(encryptedPassword);
        user.setUserName("Anonymous");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        ThrowUtils.throwIf(!saveResult,
                new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to register user into database"));
        return user.getId();
    }

    /**
     * Encrypt password with MD5
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        // Add salt
        final String SALT = "holt";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    /**
     * Log in user with credentials and set session cookie
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. Parameter validation
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword),
                new BusinessException(ErrorCode.PARAMS_ERROR, "Empty input"));
        ThrowUtils.throwIf(userAccount.length() < 4,
                new BusinessException(ErrorCode.PARAMS_ERROR, "Account length less than 4"));
        ThrowUtils.throwIf(userPassword.length() < 8,
                new BusinessException(ErrorCode.PARAMS_ERROR, "Password length less than 8"));

        // 2. Check if the user exists
        String encryptedPassword = getEncryptPassword(userPassword);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptedPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // User does not exist
        ThrowUtils.throwIf(user==null,
                new BusinessException(ErrorCode.PARAMS_ERROR, "User does not exist or incorrect password"));
        // 3. Record user login state
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * Convert User object to user view object
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * Get insensitive user information based on session cookie
     */
    @Override
    public LoginUserVO getLoginUserVO(HttpServletRequest request) {
        // Check if the current user has logged in
        Object userObject = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObject;
        ThrowUtils.throwIf(user == null || user.getId() == null,
                new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "User has not yet logged in"));
        long userId = user.getId();
        user = getById(userId);
        // It may be the case where the user is deleted while the session is valid
        ThrowUtils.throwIf(user == null,
                new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "User has not yet logged in"));
        return getLoginUserVO(user);
    }
}




