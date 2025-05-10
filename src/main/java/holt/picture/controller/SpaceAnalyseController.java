package holt.picture.controller;

import holt.picture.common.BaseResponse;
import holt.picture.common.ResultUtils;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.model.Space;
import holt.picture.model.User;
import holt.picture.model.dto.space.analyse.*;
import holt.picture.model.vo.space.analyse.*;
import holt.picture.service.SpaceAnalyseService;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public BaseResponse<SpaceUsageAnalyseResponse> getSpaceUsageAnalyse(
            @RequestBody SpaceUsageAnalyseRequest request, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(spaceAnalyseService== null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        SpaceUsageAnalyseResponse response = spaceAnalyseService.getSpaceUsageAnalyse(request, loginUser);
        return ResultUtils.success(response);
    }

    @PostMapping("/category")
    public BaseResponse<List<SpaceCategoryAnalyseResponse>> getSpaceCategoryAnalyse(
            @RequestBody SpaceCategoryAnalyseRequest request, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(spaceAnalyseService== null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        List<SpaceCategoryAnalyseResponse> response = spaceAnalyseService.getSpaceCategoryAnalyse(request, loginUser);
        return ResultUtils.success(response);
    }

    @PostMapping("/tag")
    public BaseResponse<List<SpaceTagAnalyseResponse>> getSpaceTagAnalyse(
            @RequestBody SpaceTagAnalyseRequest request, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        List<SpaceTagAnalyseResponse> resultList = spaceAnalyseService.getSpaceTagAnalyse(request, loginUser);
        return ResultUtils.success(resultList);
    }

    @PostMapping("/size")
    public BaseResponse<List<SpaceSizeAnalyseResponse>> getSpaceSizeAnalyse(
            @RequestBody SpaceSizeAnalyseRequest request, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        List<SpaceSizeAnalyseResponse> resultList = spaceAnalyseService.getSpaceSizeAnalyse(request, loginUser);
        return ResultUtils.success(resultList);
    }

    @PostMapping("/user")
    public BaseResponse<List<SpaceUserAnalyseResponse>> getSpaceUserAnalyse(
            @RequestBody SpaceUserAnalyseRequest request, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        List<SpaceUserAnalyseResponse> resultList = spaceAnalyseService.getSpaceUserAnalyse(request, loginUser);
        return ResultUtils.success(resultList);
    }

    @PostMapping("/rank")
    public BaseResponse<List<Space>> getSpaceRankAnalyze(
            @RequestBody SpaceRankAnalyseRequest request, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        List<Space> resultList = spaceAnalyseService.getSpaceRankAnalyse(request, loginUser);
        return ResultUtils.success(resultList);
    }



}
