package holt.picture.controller;

import holt.picture.annotation.AuthCheck;
import holt.picture.common.BaseResponse;
import holt.picture.common.ResultUtils;
import holt.picture.constant.UserConstant;
import holt.picture.model.User;
import holt.picture.model.dto.file.PictureUploadRequest;
import holt.picture.model.vo.PictureVO;
import holt.picture.service.PictureService;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
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

    @PostMapping("upload")
    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PictureVO> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request
    ) {
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }
}
