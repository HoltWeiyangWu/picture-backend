package holt.picture.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * @author Weiyang Wu
 * @date 2025/4/9 20:59
 */
@Configuration
@ConfigurationProperties(prefix = "aws.s3.client") // Maps .yaml properties to this bean
@Data
public class AwsS3ClientConfig {
    /**
     * Bucket's region
     */
    private String region;

    /**
     * Bucket name
     */
    private String bucket;

    /**
     * Access key id
     */
    private String accessKey;

    /**
     * Secret access key
     */
    private String secretKey;

    @Bean
    public S3Client getS3Client() {
        Region bucketRegion = Region.of(region);
        return S3Client.builder()
                .region(bucketRegion)
                .credentialsProvider(()-> AwsBasicCredentials.create(accessKey, secretKey))
                .build();
    }
}
