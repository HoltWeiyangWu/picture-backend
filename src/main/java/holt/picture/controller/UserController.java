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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
