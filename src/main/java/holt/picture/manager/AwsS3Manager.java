package holt.picture.manager;

import holt.picture.config.AwsS3ClientConfig;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;

/**
 * An AWS S3 manager to handle specific AWS SDK calls to data operations
 * @author Weiyang Wu
 * @date 2025/4/9 22:30
 */
@Component
public class AwsS3Manager {
    private static final Logger log = LoggerFactory.getLogger(AwsS3Manager.class);
    @Resource
    private S3Client s3Client;

    @Resource
    private AwsS3ClientConfig awsS3ClientConfig;

    /**
     * Upload an object to AWS S3
     */
    public PutObjectResponse putObject(File file){
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsS3ClientConfig.getBucket())
                .key(file.getName())
                .build();
        return s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
    }

    /**
     * Put an object to AWS S3
     */
    public ResponseInputStream<GetObjectResponse> getObject(String key){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsS3ClientConfig.getBucket())
                .key(key)
                .build();
        return s3Client.getObject(getObjectRequest);
    }
    /**
     * Upload an object to AWS S3
     */
    public void putObject(String key, File file) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(awsS3ClientConfig.getBucket())
                    .key(key)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
        } catch (AwsServiceException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to put file to AWS S3");
        }
    }

    public DeleteObjectResponse deleteObject(String key){
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(awsS3ClientConfig.getBucket())
                    .key(key)
                    .build();
            return s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            System.out.println("Failed to delete file from AWS S3 " + e.getMessage());
        }
        return null;
    }
}
