package holt.picture.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.manager.FileManager;
import holt.picture.model.Picture;
import holt.picture.model.User;
import holt.picture.model.dto.file.PictureUploadRequest;
import holt.picture.model.vo.PictureVO;
import holt.picture.service.PictureService;
import holt.picture.mapper.PictureMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;

/**
* @author Weiyang Wu
* @date 2025-04-10 22:03:58
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    @Resource
    private FileManager fileManager;

    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        Long pictureId = null;
        // Check whether the user is adding a picture or updating a picture
        if (pictureUploadRequest != null) {
            pictureId = pictureUploadRequest.getId();
        }
        // Validate picture's existence if updating
        if (pictureId != null) {
            boolean isExisted = this.lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .exists();
            ThrowUtils.throwIf(!isExisted, ErrorCode.NOT_FOUND_ERROR,"Picture does not exist");
        }

        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
        Picture picture = fileManager.uploadPicture(multipartFile, uploadPathPrefix);
        picture.setCreatorId(loginUser.getId());
        // Re-new properties in case of update
        if (pictureId != null) {
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "Failed to save it to database");
        return PictureVO.objectToVo(picture);
    }



}




