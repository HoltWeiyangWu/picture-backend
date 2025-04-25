package holt.picture.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import holt.picture.annotation.AuthCheck;
import holt.picture.common.BaseResponse;
import holt.picture.common.DeleteRequest;
import holt.picture.common.ResultUtils;
import holt.picture.constant.UserConstant;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.model.Picture;
import holt.picture.model.enums.PictureReviewStatusEnum;
import holt.picture.model.User;
import holt.picture.model.dto.file.*;
import holt.picture.model.vo.PictureTagCategory;
import holt.picture.model.vo.PictureVO;
import holt.picture.service.PictureService;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Controller layer for picture operations
 * @author Weiyang Wu
 * @date 2025/4/14 13:12
 */
@RestController
@RequestMapping("/picture")
public class PictureController {

    @Resource
    private PictureService pictureService;

    @Resource
    private UserService userService;

    /**
     * Upload a picture file to AWS S3 and record its information to database
     */
    @PostMapping("/upload")
    public BaseResponse<PictureVO> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request
    ) {
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }
    /**
     * Upload a picture url to AWS S3 and record its information to database
     */
    @PostMapping("/upload/url")
    public BaseResponse<PictureVO> uploadPictureByUrl(
            @RequestBody PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String fileUrl = pictureUploadRequest.getFileUrl();
        PictureVO pictureVO = pictureService.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * Upload a picture url to AWS S3 and record its information to database
     */
    @PostMapping("/upload/batch")
    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> uploadPictureByBatch(
            @RequestBody UploadPictureByBatchRequest uploadPictureByBatchRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(uploadPictureByBatchRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        int uploadCount = pictureService.uploadPictureByBatch(uploadPictureByBatchRequest, loginUser);
        return ResultUtils.success(uploadCount);
    }

    /**
     * Delete a picture according to picture id
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();

        // Check if the picture exists
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        checkIfOwnerOrAdmin(picture, loginUser);
        boolean result = pictureService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Update a picture (Only for admin)
     */
    @PostMapping("/update")
    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest,
                                               HttpServletRequest request) {
        ThrowUtils.throwIf(pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        // Convert Json array to string
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        picture.setUpdateTime(new Date());
        pictureService.validPicture(picture);
        long id = pictureUpdateRequest.getId();
        // Check if the picture already exists
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // Add picture review feature
        User loginUser = userService.getLoginUser(request);
        pictureService.fillReviewParams(picture, loginUser);
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Edit a picture (For creator or admin)
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureEditRequest == null || pictureEditRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // Convert Json array to string
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        picture.setEditTime(new Date());
        pictureService.validPicture(picture);
        long id = pictureEditRequest.getId();
        // Check if the picture already exists
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        checkIfOwnerOrAdmin(oldPicture, loginUser);
        // Add picture review feature
        pictureService.fillReviewParams(picture, loginUser);
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Get all information of a picture (Only for admin)
     */
    @PostMapping("/get")
    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(picture);
    }

    /**
     * Get filtered information of a picture
     */
    @PostMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(pictureService.getPictureVO(picture, request));
    }

    /**
     * Get all information of a list of picture (Only for admin)
     */
    @PostMapping("/list/page")
    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // Query from the database
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getPictureQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(picturePage);
    }

    /**
     * Get filtered information of a list of picture
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                             HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // Limit what the user can query about in terms of size
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getPictureQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(pictureService.getPictureVOPage(picturePage, request));
    }

    /**
     * Get pre-defined picture tag or category
     */
    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("Fantasy", "Nature", "Animal", "Car", "Anime", "Space");
        List<String> categoryList = Arrays.asList("Wallpapers", "PNG", "Transparent");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }

    /**
     * Review a picture, setting the picture's status to pass/rejected/reviewing (only for admin)
     */
    @PostMapping("/review")
    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> reviewPicture(@RequestBody PictureReviewRequest pictureReviewRequest,
                                               HttpServletRequest request) {
        ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureService.reviewPicture(pictureReviewRequest, loginUser);
        return ResultUtils.success(true);
    }

    /**
     * Helper function to check if the user is authorised to access the current picture object
     */
    private void checkIfOwnerOrAdmin(Picture picture, User loginUser) {
        boolean isCreator = picture.getCreatorId().equals(loginUser.getId());
        boolean isAdmin = userService.isAdmin(loginUser);
        ThrowUtils.throwIf(!isCreator || !isAdmin, ErrorCode.NO_AUTH_ERROR);
    }

}
