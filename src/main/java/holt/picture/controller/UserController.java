package holt.picture.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import holt.picture.annotation.AuthCheck;
import holt.picture.common.BaseResponse;
import holt.picture.common.DeleteRequest;
import holt.picture.common.ResultUtils;
import holt.picture.constant.UserConstant;
import holt.picture.dto.*;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.model.User;
import holt.picture.model.vo.LoginUserVO;
import holt.picture.model.vo.UserVO;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller layer for user operations
 * @author Weiyang Wu
 * @date 2025/4/1 19:24
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * User registration
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest request) {
        ThrowUtils.throwIf(request==null, ErrorCode.PARAMS_ERROR);
        String account = request.getUserAccount();
        String password = request.getUserPassword();
        String checkPassword = request.getCheckPassword();
        long result = userService.userRegister(account, password, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * User login
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest request, HttpServletRequest httpRequest) {
        ThrowUtils.throwIf(request==null, ErrorCode.PARAMS_ERROR);
        String account = request.getUserAccount();
        String password = request.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(account, password, httpRequest);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * Get insensitive user information based on session cookie
     */
//    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    @GetMapping("get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        LoginUserVO loginUserVO = userService.getLoginUserVO(loginUser);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * Log out user and remove session
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest httpRequest) {
        ThrowUtils.throwIf(httpRequest==null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(httpRequest);
        return ResultUtils.success(result);
    }

    /**
     * Add user to database (only for admin)
     */
    @PostMapping("/add")
    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest==null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        final String DEFAULT_PASSWORD = "password";
        String encryptedPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptedPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result,
                new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to save user to database"));
        return ResultUtils.success(user.getId());
    }

    /**
     * Get user form user ID (only to admin)
     */
    @GetMapping("/get")
    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * Delete user by its user ID (only to admin)
     */
    @PostMapping("/delete")
    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null,
                ErrorCode.PARAMS_ERROR);
        boolean result = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * Get user insensitive information
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }


    /**
     * Update user with new user information (only to admin)
     */
    @PostMapping("/update")
    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(userUpdateRequest==null || userUpdateRequest.getId()==null,
                ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * Obtain a list of user objects (only to admin)
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest){
        ThrowUtils.throwIf(userQueryRequest== null, ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        // Query a page of user
        Page<User> userPage = userService.page(new Page<>(current, pageSize),
                userService.getUserQueryWrapper(userQueryRequest));
        // Create a page with the desired format
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        // Obtain a list of user vo
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        // Convert this page of user to a page of user vo
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);

    }

}
