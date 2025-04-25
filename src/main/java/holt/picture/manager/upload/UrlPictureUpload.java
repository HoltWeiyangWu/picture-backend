package holt.picture.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import holt.picture.config.AwsS3ClientConfig;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.model.Picture;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * A concrete class that enables picture uploading through url
 * @author Weiyang Wu
 * @date 2025/4/24 10:22
 */
@Service
public class UrlPictureUpload extends PictureUploadTemplate{
    @Override
    protected void validatePicture(Object inputSource) {
        String fileUrl = (String)inputSource;
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "Url cannot be empty");

        // 1. Validate URL format
        try {
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Url format is invalid");
        }

        // 2. Validate URL protocols
        ThrowUtils.throwIf(!(fileUrl.startsWith("http://") || fileUrl.startsWith("https://")),
                ErrorCode.PARAMS_ERROR, "Only support HTTP or HTTPS protocol");

        // 3. Send HEAD request to check if the file exists
        try (HttpResponse response = HttpUtil.createRequest(Method.HEAD, fileUrl).execute()) {
            // Format validation
            String contentType = response.header("Content-Type");
            if (StrUtil.isNotBlank(contentType)) {
                final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/png", "image/webp");
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()),
                        ErrorCode.PARAMS_ERROR, "Do not support current image format");
            }

            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                long contentLength = Long.parseLong(contentLengthStr);
                final long TWO_MB = 2 * 1024 * 1024L;
                ThrowUtils.throwIf(contentLength > TWO_MB, ErrorCode.PARAMS_ERROR,
                        "File cannot be larger than 2MB");
            }

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to check if URL file exists");
        }
    }

    @Override
    protected String getOriginalFilename(Object inputSource) {
        String fileUrl = (String)inputSource;
        return FileUtil.mainName(fileUrl);
    }

    @Override
    protected void processFile(Object inputSource, File file) {
        String fileUrl = (String)inputSource;
        HttpUtil.downloadFile(fileUrl, file);
    }

    @Override
    protected Picture constructPicture(Object inputSource, BufferedImage image, String originalFilename, String uploadPath,
                                       AwsS3ClientConfig awsS3ClientConfig) {
        Picture picture = new Picture();

        String fileUrl = (String)inputSource;
        try (HttpResponse response = HttpUtil.createRequest(Method.HEAD, fileUrl).execute()) {
            String contentType = response.header("Content-Type");
            Long contentLength = Long.parseLong(response.header("Content-Length"));
            String url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                    awsS3ClientConfig.getBucket(),
                    awsS3ClientConfig.getRegion(),
                    uploadPath);
            picture.setUrl(url);
            picture.setName(originalFilename);
            picture.setPicSize(contentLength);
            picture.setPicWidth(image.getWidth());
            picture.setPicHeight(image.getHeight());
            double picScale = image.getWidth() / (double) image.getHeight();
            picture.setPicScale(picScale);
            picture.setPicFormat(contentType);
        }
        return picture;
    }

    @Override
    protected BufferedImage getImage(Object inputSource) {
        try {
            String fileUrl = (String)inputSource;
            return ImageIO.read(new URL(fileUrl));
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to get image from url");
        }
    }
}
