package holt.picture.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import holt.picture.annotation.AuthCheck;
import holt.picture.common.BaseResponse;
import holt.picture.common.DeleteRequest;
import holt.picture.common.ResultUtils;
import holt.picture.constant.UserConstant;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.manager.auth.SpaceUserAuthManager;
import holt.picture.model.Space;
import holt.picture.model.User;
import holt.picture.model.dto.space.*;
import holt.picture.model.enums.SpaceLevelEnum;
import holt.picture.model.vo.SpaceVO;
import holt.picture.service.SpaceService;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Weiyang Wu
 * @date 2025/4/28 14:51
 */
@RestController
@RequestMapping("/space")
public class SpaceController {

    @Resource
    private SpaceService spaceService;

    @Resource
    private UserService userService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    @PostMapping("/add")
    public BaseResponse<Long> addSpace(@RequestBody SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceAddRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        long newId = spaceService.addSpace(spaceAddRequest, loginUser);
        return ResultUtils.success(newId);
    }

    /**
     * Delete a space according to space id
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();

        // Check if the space exists
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        spaceService.checkIfOwnerOrAdmin(loginUser, space);
        boolean result = spaceService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Update a space (Only for admin)
     */
    @PostMapping("/update")
    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest) {
        ThrowUtils.throwIf(spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        Space space = new Space();
        BeanUtils.copyProperties(spaceUpdateRequest, space);
        space.setUpdateTime(new Date());
        spaceService.fillSpaceBySpaceLevel(space);
        spaceService.validSpace(space, false);
        long id = spaceUpdateRequest.getId();
        // Check if the space already exists
        Space oldSpace = spaceService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // Add space review feature
        boolean result = spaceService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Edit a space (For creator or admin)
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceEditRequest == null || spaceEditRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        Space space = new Space();
        BeanUtils.copyProperties(spaceEditRequest, space);
        spaceService.fillSpaceBySpaceLevel(space);
        space.setEditTime(new Date());
        spaceService.validSpace(space, false);
        long id = spaceEditRequest.getId();
        // Check if the space already exists
        Space oldSpace = spaceService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        spaceService.checkIfOwnerOrAdmin(loginUser, oldSpace);
        boolean result = spaceService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Get all information of a space (Only for admin)
     */
    @PostMapping("/get")
    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Space> getSpaceById(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(space);
    }

    /**
     * Get filtered information of a space
     */
    @PostMapping("/get/vo")
    public BaseResponse<SpaceVO> getSpaceVOById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        SpaceVO spaceVO = spaceService.getSpaceVO(space, request);
        User loginUser = userService.getLoginUser(request);
        List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
        spaceVO.setPermissionList(permissionList);
        return ResultUtils.success(spaceVO);
    }

    /**
     * Get all information of a list of space (Only for admin)
     */
    @PostMapping("/list/page")
    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        // Query from the database
        Page<Space> spacePage = spaceService.page(new Page<>(current, size),
                spaceService.getSpaceQueryWrapper(spaceQueryRequest));
        return ResultUtils.success(spacePage);
    }

    /**
     * Get filtered information of a list of space
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryRequest spaceQueryRequest,
                                                             HttpServletRequest request) {
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        // Limit what the user can query about in terms of size
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Space> spacePage = spaceService.page(new Page<>(current, size),
                spaceService.getSpaceQueryWrapper(spaceQueryRequest));
        return ResultUtils.success(spaceService.getSpaceVOPage(spacePage, request));
    }

    @GetMapping("/list/level")
    public BaseResponse<List<SpaceLevel>> listSpaceLevels() {
        List<SpaceLevel> spaceLevelList = Arrays.stream(SpaceLevelEnum.values())
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxSize(),
                        spaceLevelEnum.getMaxCount()
                ))
                .collect(Collectors.toList());
        return ResultUtils.success(spaceLevelList);
    }
}
