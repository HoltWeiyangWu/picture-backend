package holt.picture.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.model.User;
import holt.picture.model.UserRoleEnum;
import holt.picture.service.UserService;
import holt.picture.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
* @author Weiyang Wu
* @date 2025-04-01 17:36:37
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

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

    @Override
    public String getEncryptPassword(String userPassword) {
        // Add salt
        final String SALT = "holt";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

}




