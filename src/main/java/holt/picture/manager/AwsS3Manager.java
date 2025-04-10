package holt.picture.manager;

import holt.picture.config.AwsS3ClientConfig;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;

/**
 * @author Weiyang Wu
 * @date 2025/4/9 22:30
 */
@Component
public class AwsS3Manager {
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

    public ResponseInputStream<GetObjectResponse> getObject(String key){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsS3ClientConfig.getBucket())
                .key(key)
                .build();
        return s3Client.getObject(getObjectRequest);
    }
}
