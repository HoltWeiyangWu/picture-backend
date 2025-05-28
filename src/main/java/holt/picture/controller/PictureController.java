package holt.picture.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import holt.picture.annotation.AuthCheck;
import holt.picture.common.BaseResponse;
import holt.picture.common.DeleteRequest;
import holt.picture.common.ResultUtils;
import holt.picture.constant.UserConstant;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.manager.auth.SpaceUserAuthManager;
import holt.picture.manager.auth.StpKit;
import holt.picture.manager.auth.annotation.SaSpaceCheckPermission;
import holt.picture.manager.auth.model.SpaceUserPermissionConstant;
import holt.picture.model.Picture;
import holt.picture.model.Space;
import holt.picture.model.enums.PictureReviewStatusEnum;
import holt.picture.model.User;
import holt.picture.model.dto.file.*;
import holt.picture.model.vo.PictureTagCategory;
import holt.picture.model.vo.PictureVO;
import holt.picture.service.PictureService;
import holt.picture.service.SpaceService;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SpaceService spaceService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;
    /**
     * Local cache
     */
    private final Cache<String, String> LOCAL_CACHE = Caffeine.newBuilder()
            .initialCapacity(1024)
            .maximumSize(10_000L)
            // Remove cache after five minutes
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    /**
     * Upload a picture file to AWS S3 and record its information to database
     */
    @PostMapping("/upload")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_UPLOAD)
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
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_UPLOAD)
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
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_DELETE)
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        pictureService.deletePicture(id, loginUser);
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
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_EDIT)
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureEditRequest == null || pictureEditRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureService.editPicture(pictureEditRequest, loginUser);
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
        Long spaceId = picture.getSpaceId();
        Space space = null;
        if (spaceId != null) {
            boolean hasPermission = StpKit.SPACE.hasPermission(SpaceUserPermissionConstant.PICTURE_VIEW);
            ThrowUtils.throwIf(!hasPermission, ErrorCode.NO_AUTH_ERROR);
            space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "Space does not exist");
        }
        User loginUser = userService.getLoginUser(request);
        List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
        PictureVO pictureVO = pictureService.getPictureVO(picture, request);
        pictureVO.setPermissionList(permissionList);
        return ResultUtils.success(pictureVO);
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
        // Space authorisation check
        Long spaceId = pictureQueryRequest.getSpaceId();
        if (spaceId == null) {
            // Public space: users can see reviewed pictures
            pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            pictureQueryRequest.setNullSpaceId(true);
        } else {
            // Private space
            boolean hasPermission = StpKit.SPACE.hasPermission(SpaceUserPermissionConstant.PICTURE_VIEW);
            ThrowUtils.throwIf(!hasPermission, ErrorCode.NO_AUTH_ERROR);
        }
        QueryWrapper<Picture> pictureQueryWrapper = pictureService.getPictureQueryWrapper(pictureQueryRequest);
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),pictureQueryWrapper
                );
        return ResultUtils.success(pictureService.getPictureVOPage(picturePage, request));
    }

    /**
     * Get filtered information of a list of picture with cache (a combination of local cache and remote cache)
     */
    @Deprecated
    @PostMapping("/list/page/vo/cache")
    public BaseResponse<Page<PictureVO>> listPictureVOByPageWithCache(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                             HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // Limit what the user can query about in terms of size
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        // Check cache, set key as formatted hashed parameters and value as request object
        String queryCondition = JSONUtil.toJsonStr(pictureQueryRequest);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        String cacheKey = String.format("cloudPicture:listPictureVOByPage:%s", hashKey);

        // 1. Get value from Caffeine local cache first
        String cachedValue = LOCAL_CACHE.getIfPresent(cacheKey);
        if (cachedValue != null) {
            Page<PictureVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            return ResultUtils.success(cachedPage);
        }
        // 2. In case where a local cache is available, use remote redis cache
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        cachedValue = opsForValue.get(cacheKey);
        if (cachedValue != null) {
            Page<PictureVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            LOCAL_CACHE.put(cacheKey, cachedValue);
            return ResultUtils.success(cachedPage);
        }

        // 3. No cache found locally or remotely, make a query and save in both caches
        // Make a query to the database
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getPictureQueryWrapper(pictureQueryRequest));
        Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(picturePage, request);
        String cacheValue = JSONUtil.toJsonStr(pictureVOPage);
        // Set an expiry time form 5 to 10 minutes to avoid cache
        int cacheExpire = 300 + RandomUtil.randomInt(0, 300);
        // Set Redis remote cache
        opsForValue.set(cacheKey, cacheValue, cacheExpire, TimeUnit.SECONDS);
        // Set local cache
        LOCAL_CACHE.put(cacheKey, cacheValue);

        return ResultUtils.success(pictureVOPage);
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

    @PostMapping("/edit/batch")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_EDIT)
    public BaseResponse<Boolean> editPictureByBatch(@RequestBody PictureEditByBatchRequest pictureEditByBatchRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureEditByBatchRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureService.editPictureByBatch(pictureEditByBatchRequest, loginUser);
        return ResultUtils.success(true);
    }
}
