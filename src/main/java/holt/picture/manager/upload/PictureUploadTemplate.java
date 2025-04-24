package holt.picture.manager.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import holt.picture.config.AwsS3ClientConfig;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import holt.picture.manager.AwsS3Manager;
import holt.picture.model.Picture;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * A template to upload a picture through file uploading or url
 * @author Weiyang Wu
 * @date 2025/4/24 9:20
 */
@Slf4j
public abstract class PictureUploadTemplate {
    @Resource
    protected AwsS3Manager awsS3Manager;

    @Resource
    protected AwsS3ClientConfig awsS3ClientConfig;

    public final Picture uploadPicture(Object inputSource, String uploadPathPrefix) {
        validatePicture(inputSource);

        // Formating upload path
        String uuid = RandomUtil.randomNumbers(16);
        String originalFilename = getOriginalFilename(inputSource);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFilename);
        File file = null;
        try {
            BufferedImage image = getImage(inputSource);
            file = File.createTempFile(uploadPath, null);
            processFile(inputSource, file);
            awsS3Manager.putObject(uploadPath, file);
            return constructPicture(inputSource, image, originalFilename,uploadPath, awsS3ClientConfig);
        } catch (Exception e) {
            log.error("Failed to upload picture to AWS S3", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to upload");
        } finally {
            deleteTempFile(file);
        }

    }

    /**
     * Validate input source(either a file or url)
     */
    protected abstract void validatePicture(Object inputSource);

    /**
     * Get original file name from input source
     */
    protected abstract String getOriginalFilename(Object inputSource);

    /**
     * Handle inputs and create temporary file
     */
    protected abstract void processFile(Object inputSource, File file) throws Exception;

    /**
     * Construct a picture object with all the information obtained
     */
    protected abstract Picture constructPicture(Object inputSource, BufferedImage image, String originalFilename,
                                                String uploadPath, AwsS3ClientConfig awsS3ClientConfig) throws IOException;

    /**
     * Obtain a buffered image before processing the file
     */
    protected abstract BufferedImage getImage(Object inputSource);

    /**
     * Delete the temporary file stored in local machine
     */
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
