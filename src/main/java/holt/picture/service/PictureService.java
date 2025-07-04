package holt.picture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import holt.picture.model.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import holt.picture.model.User;
import holt.picture.model.dto.file.*;
import holt.picture.model.vo.PictureVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author Weiyang Wu
* @date 2025-04-10 22:03:58
*/
public interface PictureService extends IService<Picture> {
    PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest,
                            User logiinUser);
    QueryWrapper<Picture> getPictureQueryWrapper(PictureQueryRequest pictureQueryRequest);
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);
    void validPicture(Picture picture);
    void reviewPicture(PictureReviewRequest pictureReviewRequest, User loginUser);
    void fillReviewParams(Picture picture, User loginUser);
    Integer uploadPictureByBatch(UploadPictureByBatchRequest batchRequest, User loginUser);
    void checkPictureAuth(User loginUser, Picture picture);

    void deletePicture(long pictureId, User loginUser);

    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);
}
