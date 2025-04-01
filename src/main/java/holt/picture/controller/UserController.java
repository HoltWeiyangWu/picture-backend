package holt.picture.controller;

import holt.picture.common.BaseResponse;
import holt.picture.common.ResultUtils;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.model.dto.UserLoginRequest;
import holt.picture.model.dto.UserRegisterRequest;
import holt.picture.model.vo.LoginUserVO;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest httpRequest) {
        LoginUserVO loginUserVO = userService.getLoginUserVO(httpRequest);
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
}
