package holt.picture.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Springboot MVC configuration to allow CORS
 * @author Weiyang Wu
 * @date 2025/3/31 14:30
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Cover all requests
                .allowCredentials(true) // Allows for sending cookies
                // allows for all origins, have to use patterns due to credentials
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*") // For request
                .exposedHeaders("*"); // For response
    }
}
