package holt.picture.controller;

import holt.picture.common.BaseResponse;
import holt.picture.common.ResultUtils;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.model.User;
import holt.picture.model.dto.space.analyse.SpaceUsageAnalyseRequest;
import holt.picture.model.vo.space.analyse.SpaceUsageAnalyseResponse;
import holt.picture.service.SpaceAnalyseService;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * @author Weiyang Wu
 * @date 2025/5/9 22:33
 */
@RestController
@RequestMapping("/space/analyse")
public class SpaceAnalyseController {
    @Resource
    private UserService userService;

    @Resource
    private SpaceAnalyseService spaceAnalyseService;

    @PostMapping("/usage")
    public BaseResponse<SpaceUsageAnalyseResponse> getSpaceUsageAnalyse(@RequestBody SpaceUsageAnalyseRequest request,
                                                                        HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(spaceAnalyseService== null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        SpaceUsageAnalyseResponse response = spaceAnalyseService.getSpaceUsageAnalyse(request, loginUser);
        return ResultUtils.success(response);
    }
}
