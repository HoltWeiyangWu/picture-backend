package holt.picture.controller;

import holt.picture.annotation.AuthCheck;
import holt.picture.common.BaseResponse;
import holt.picture.common.ResultUtils;
import holt.picture.constant.UserConstant;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import holt.picture.manager.AwsS3Manager;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.utils.IoUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Weiyang Wu
 * @date 2025/4/9 23:08
 */
@RestController
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private final AwsS3Manager awsS3Manager;

    public FileController(AwsS3Manager awsS3Manager) {
        this.awsS3Manager = awsS3Manager;
    }

    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUpload(@RequestParam("file") MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        String filePath = String.format("/test/%s", fileName);

        File file = null;
        try {
            file = File.createTempFile(filePath, null);
            multipartFile.transferTo(file);
            PutObjectResponse response = awsS3Manager.putObject(file);
            return ResultUtils.success(response.toString());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());

        } finally {
            if (file != null) {
                boolean deleted = file.delete();
                if (!deleted) {
                    log.error("file delete error, filePath:{}", filePath);
                }
            }
        }
    }

    @AuthCheck(requiredRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download")
    public void testDownload(String filePath, HttpServletResponse response) throws IOException {
        ResponseInputStream<GetObjectResponse> responseInputStream = null;
        try {
            // Handle response stream
            responseInputStream = awsS3Manager.getObject(filePath);
            byte[] bytes = IoUtils.toByteArray(responseInputStream);
            // Set header for the response
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filePath + "\"");
            // Write contents into response
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("get object error, filePath:{}", filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to download file: " + filePath);
        } finally {
            if (responseInputStream != null) {
                responseInputStream.close();
            }
        }
    }
}
