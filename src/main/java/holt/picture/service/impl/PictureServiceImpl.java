package holt.picture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.manager.upload.FilePictureUpload;
import holt.picture.manager.upload.PictureUploadTemplate;
import holt.picture.manager.upload.UrlPictureUpload;
import holt.picture.model.Picture;
import holt.picture.model.Space;
import holt.picture.model.dto.file.*;
import holt.picture.model.enums.PictureReviewStatusEnum;
import holt.picture.model.User;
import holt.picture.model.vo.PictureVO;
import holt.picture.model.vo.UserVO;
import holt.picture.service.PictureService;
import holt.picture.mapper.PictureMapper;
import holt.picture.service.SpaceService;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation details of operations to picture objects
* @author Weiyang Wu
* @date 2025-04-10 22:03:58
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    @Resource
    private FilePictureUpload filePictureUpload;

    @Resource
    private UrlPictureUpload urlPictureUpload;

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private TransactionTemplate transactionTemplate;


    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        ThrowUtils.throwIf(pictureUploadRequest == null, ErrorCode.NO_AUTH_ERROR);
        // Check if space exists
        Long spaceId = pictureUploadRequest.getSpaceId();
        if (spaceId != null) {
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "No storage space found");
            // Check is user is authorised for this space
            boolean isCreator = loginUser.getId().equals(space.getCreatorId());
            ThrowUtils.throwIf(!isCreator, ErrorCode.NO_AUTH_ERROR,
                    "You are not authorized to upload picture to this storage space");
            // Check if there remains any space left
            if (space.getTotalCount() >= space.getMaxCount()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "Too many pictures in the storage space");
            }
            if (space.getTotalSize() >= space.getMaxSize()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "Storage space is not enough");
            }
        }

        // Check whether the user is adding a picture or updating a picture
        Long pictureId = pictureUploadRequest.getId();

        // Validate picture's existence if updating
        if (pictureId != null) {
            Picture existingPicture = this.getById(pictureId);
            ThrowUtils.throwIf(existingPicture == null, ErrorCode.NOT_FOUND_ERROR,
                    "Picture does not exist");
            // Only its creator or admin can edit it
            if (!existingPicture.getCreatorId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "You are not authorized to edit this file");
            }
            // Check if the space remains the same
            if (spaceId == null) {
                if (existingPicture.getSpaceId() != null) {
                    spaceId = existingPicture.getSpaceId();
                }
            } else {
                if (ObjUtil.notEqual(spaceId, existingPicture.getSpaceId())) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "Space ID does not match");
                }
            }
        }
        String uploadPathPrefix;
        if (spaceId == null) {
            uploadPathPrefix = String.format("public/%s", loginUser.getId());
        } else {
            uploadPathPrefix = String.format("space/%s", spaceId);
        }

        PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
        if (inputSource instanceof String) {
            pictureUploadTemplate = urlPictureUpload;
        }
        Picture picture = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
        picture.setCreatorId(loginUser.getId());
        picture.setSpaceId(spaceId);
        // Set a picture name if it is provided through batch uploading
        if (StrUtil.isNotBlank(pictureUploadRequest.getPicName())) {
            picture.setName(pictureUploadRequest.getPicName());
        }
        fillReviewParams(picture, loginUser);
        // Re-new properties in case of update
        if (pictureId != null) {
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        Long finalSpaceId = spaceId;
        transactionTemplate.execute(status -> {

            boolean result = this.saveOrUpdate(picture);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "Failed to save it to database");
            if (finalSpaceId != null) {
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, finalSpaceId)
                        .setSql("totalSize = totalSize + " + picture.getPicSize())
                        .setSql("totalCount = totalCount + 1")
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "Failed to update space status information");
            }
            return PictureVO.objectToVo(picture);
        });

        return PictureVO.objectToVo(picture);
    }

    @Override
    public QueryWrapper<Picture> getPictureQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long creatorId = pictureQueryRequest.getCreatorId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        Long spaceId = pictureQueryRequest.getSpaceId();
        boolean nullSpaceId = pictureQueryRequest.isNullSpaceId();

        // Find text from name or introduction
        if (StrUtil.isNotBlank(searchText)) {
            queryWrapper.and(qw ->
                    qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(creatorId), "creatorId", creatorId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.isNull(nullSpaceId, "nullSpaceId");
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);
        // Query to tags (JSON array)
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag: tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }

        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }


    /**
     * Show only insensitive information for both picture and its creator
     */
    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        PictureVO pictureVO = PictureVO.objectToVo(picture);
        Long creatorId = pictureVO.getCreatorId();
        if (creatorId != null && creatorId > 0) {
            User user = userService.getById(creatorId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setCreator(userVO);
        }
        return pictureVO;
    }

    /**
     * Show only insensitive information for both picture and its creator in a page
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(),
                picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }

        List<PictureVO> pictureVOList = pictureList.stream()
                .map(PictureVO::objectToVo)
                .toList();
        Set<Long> userIdSet = pictureVOList.stream()
                .map(PictureVO::getCreatorId)
                .collect(Collectors.toSet());

        // Find a unique set of users instead of finding a user according to a picture
        Map<Long, List<User>> idToUserlistMap = userService.listByIds(userIdSet)
                .stream()
                .collect(Collectors.groupingBy(User::getId));

        // Append user information into the vo object
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getCreatorId();
            User user = null;
            if (idToUserlistMap.containsKey(userId)) {
                user = idToUserlistMap.get(userId).get(0);
            }
            pictureVO.setCreator(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    /**
     * Restrict picture image in terms of URL size and introduction length
     */
    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();

        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "ID can't be null");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR,
                    "URL is too long");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR,
                    "Introduction is too long");
        }
    }

    /**
     * Set review status for a particular picture (only for admin)
     */
    @Override
    public void reviewPicture(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // Validate inputs
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        if (id == null || reviewStatusEnum == null || reviewStatusEnum.equals(PictureReviewStatusEnum.REVIEWING)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // Check if the picture exists
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "Picture not found");
        // Check if the previous review status is the same
        if (oldPicture.getReviewStatus().equals(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Picture is already reviewed as such");
        }
        // Update review status
        Picture pictureToUpdate = new Picture();
        BeanUtils.copyProperties(pictureReviewRequest, pictureToUpdate);
        pictureToUpdate.setReviewerId(loginUser.getId());
        pictureToUpdate.setReviewTime(new Date());
        boolean result = this.saveOrUpdate(pictureToUpdate);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "Failed to save the picture review");
    }

    /**
     * Helper function to fill review parameters based on the requester's role
     */
    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        if (userService.isAdmin(loginUser)) {
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewMessage("Created/Edited by admin");
            picture.setReviewTime(new Date());
        } else {
            // Non-admin user should wait for review before uploading/editing
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }
    }


    /**
     * Upload a batch of pictures through Bing. Available only to admin.
     */
    @Override
    public Integer uploadPictureByBatch(UploadPictureByBatchRequest batchRequest, User loginUser) {
        // 1. Validate parameters
        String searchText = batchRequest.getSearchText();
        Integer count = batchRequest.getCount();
        ThrowUtils.throwIf(count > 30, ErrorCode.PARAMS_ERROR, "Can't fetch more than 30 pictures");
        // 2. Obtain contents
        String fetchUrl = String.format("https://bing.com/images/async?q=%s&mmasync=1",searchText);
        String namePrefix = batchRequest.getNamePrefix();
        if (StrUtil.isBlank(namePrefix)) {
            namePrefix = searchText;
        }
        Document document;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            log.error("Failed to fetch pictures", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Failed to fetch pictures");
        }

        // 3. Parse contents
        // Fetch the whole page of result
        Element div = document.getElementsByClass("dgControl").first();
        if (ObjUtil.isNull(div)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Failed to fetch elements");
        }
        // Select images from the page
        Elements imgElements = div.select("img.mimg");
        int uploadCount = 0;
        for (Element imgElement : imgElements) {
            String fileUrl = imgElement.attr("src");
            if (StrUtil.isBlank(fileUrl)) {
                log.warn("Current URL link is empty: " + fileUrl);
                continue;
            }
            int questionMarkIndex = fileUrl.indexOf("?");
            // Handle url string issue
            if (questionMarkIndex > -1) {
                fileUrl = fileUrl.substring(0, questionMarkIndex);
            }
            PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
            if (StrUtil.isNotBlank(namePrefix)) {
                pictureUploadRequest.setPicName(namePrefix + (uploadCount + 1));
            }
            try {
                this.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
                uploadCount++;
            } catch (Exception e) {
                log.error("Failed to upload picture", e);
                continue;
            }
            if (uploadCount >= count) {
                break;
            }
        }
        // 4. Upload pictures
        return uploadCount;
    }

    /**
     * Check authorisation of the current picture storage space
     */
    @Override
    public void checkPictureAuth(User loginUser, Picture picture) {
        Long spaceId = picture.getSpaceId();
        Long loginUserId = loginUser.getId();
        boolean isCreator = picture.getCreatorId().equals(loginUserId);
        if (spaceId == null) {
            // Public space, only creator or admin can do operations on it
            ThrowUtils.throwIf(!isCreator&&!userService.isAdmin(loginUser),
                    ErrorCode.NO_AUTH_ERROR, "You are not authorized to change this picture");
        } else {
            // Private space, only creator can operate on it
            ThrowUtils.throwIf(!isCreator,
                    ErrorCode.NO_AUTH_ERROR, "You are not authorized to change this picture");
        }
    }

    @Override
    public void deletePicture(long pictureId, User loginUser) {

        // Check if the picture exists
        Picture picture = this.getById(pictureId);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        checkPictureAuth(loginUser, picture);

        transactionTemplate.execute(status -> {
            boolean result = this.removeById(pictureId);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            Long spaceId = picture.getSpaceId();
            if (spaceId != null) {
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, spaceId)
                        .setSql("totalSize = totalSize - " + picture.getPicSize())
                        .setSql("totalCount = totalCount - 1")
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "Failed to update space status information");
            }
            return PictureVO.objectToVo(picture);
        });
    }

    @Override
    public void editPicture(PictureEditRequest pictureEditRequest, User loginUser) {
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // Convert Json array to string
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        picture.setEditTime(new Date());
        this.validPicture(picture);
        long id = pictureEditRequest.getId();
        // Check if the picture already exists
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        this.checkPictureAuth(loginUser, picture);
        // Add picture review feature
        this.fillReviewParams(picture, loginUser);
        boolean result = this.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }
}




