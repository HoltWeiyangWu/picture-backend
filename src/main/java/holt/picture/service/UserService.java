package holt.picture.service;

import holt.picture.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Weiyang Wu
* @date 2025-04-01 17:36:37
*/
public interface UserService extends IService<User> {
    long userRegister(String account, String password, String checkPassword);
    String getEncryptPassword(String userPassword);
}
