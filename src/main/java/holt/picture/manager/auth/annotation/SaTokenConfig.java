package holt.picture.manager.auth.annotation;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for Sa-Token, added when the application starts
 * @author Weiyang Wu
 * @date 2025/5/26 19:19
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }

    @PostConstruct
    public void rewriteSaStrategy() {
        SaAnnotationStrategy.instance.getAnnotation = (AnnotatedElementUtils::getMergedAnnotation);
    }
}
