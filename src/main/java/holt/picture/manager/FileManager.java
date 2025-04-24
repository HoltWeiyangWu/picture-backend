package holt.picture.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import holt.picture.config.AwsS3ClientConfig;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.model.Picture;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * A file service/manager to how we want our images to be uploaded
 * when uploading images to AWS S3
 * @author Weiyang Wu
 * @date 2025/4/12 20:43
 */
@Service
@Slf4j
@Deprecated
public class FileManager {
    @Resource
    private AwsS3ClientConfig awsS3ClientConfig;

    @Resource
    private AwsS3Manager awsS3Manager;


    /**
     * Upload an image with unique name and set its properties
     */
    public Picture uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        validatePicture(multipartFile);
        // Formating upload path
        String uuid = RandomUtil.randomNumbers(16);
        String originalFilename = multipartFile.getOriginalFilename();
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFilename);
        File file = null;
        try {

            BufferedImage image = ImageIO.read(multipartFile.getInputStream());
            // Create temporary file
            file = File.createTempFile(uploadPath, null);
            multipartFile.transferTo(file);
            // Upload picture
            awsS3Manager.putObject(uploadPath, file);
            Picture picture = new Picture();
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
        }catch (Exception e){
            log.error("Failed to upload picture", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to upload picture");
        } finally {
            this.deleteTempFile(file);
        }
    }

    /**
     * Validate an image file in terms of its size and format
     */
    public void validatePicture(MultipartFile multipartFile) {
        ThrowUtils.throwIf(multipartFile== null, ErrorCode.PARAMS_ERROR, "File cannot be null");
        // 1. Validate file size
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

    public void deleteTempFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        boolean isDeletedSuccessfully = file.delete();
        if (!isDeletedSuccessfully) {
            log.error("Failed to delete temp file: {}", file.getAbsolutePath());
        }
    }

}
