package holt.picture.service;

import holt.picture.model.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import holt.picture.model.User;
import holt.picture.model.dto.file.PictureUploadRequest;
import holt.picture.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

/**
* @author Weiyang Wu
* @date 2025-04-10 22:03:58
*/
public interface PictureService extends IService<Picture> {
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest,
                            User logiinUser);
}
