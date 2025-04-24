package holt.picture.manager.upload;

import cn.hutool.core.io.FileUtil;
import holt.picture.config.AwsS3ClientConfig;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.model.Picture;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * A concrete class that enables picture uploading through file
 * @author Weiyang Wu
 * @date 2025/4/24 9:44
 */
@Service
public class FilePictureUpload extends PictureUploadTemplate{

    @Override
    protected void validatePicture(Object inputSource) {
        // 1. Validate file size
        MultipartFile multipartFile = (MultipartFile) inputSource;
        long fileSize = multipartFile.getSize();
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(fileSize > 2 * ONE_MB, ErrorCode.PARAMS_ERROR,
                "File cannot be larger than 2MB");
        // 2. Validate file suffix
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpg", "jpeg", "png", "webp", "gif");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR,
                "File format not allowed");
    }

    @Override
    protected String getOriginalFilename(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        return multipartFile.getOriginalFilename();
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        multipartFile.transferTo(file);
    }


    @Override
    protected Picture constructPicture(Object inputSource, BufferedImage image, String originalFilename,
                                       String uploadPath, AwsS3ClientConfig awsS3ClientConfig) throws IOException {
        Picture picture = new Picture();
        MultipartFile multipartFile = (MultipartFile) inputSource;
        String url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                awsS3ClientConfig.getBucket(),
                awsS3ClientConfig.getRegion(),
                uploadPath);
        picture.setUrl(url);
        picture.setName(originalFilename);
        picture.setPicSize(multipartFile.getSize());
        picture.setPicWidth(image.getWidth());
        picture.setPicHeight(image.getHeight());
        double picScale = image.getWidth() / (double) image.getHeight();
        picture.setPicScale(picScale);
        picture.setPicFormat(multipartFile.getContentType());
        return picture;
    }

    @Override
    protected BufferedImage getImage(Object inputSource) {
        try {
            MultipartFile multipartFile = (MultipartFile) inputSource;
            InputStream inputStream = multipartFile.getInputStream();
            return ImageIO.read(inputStream);
        }  catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to get image from file");
        }
    }
}
