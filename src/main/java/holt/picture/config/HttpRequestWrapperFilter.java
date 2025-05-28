package holt.picture.config;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Request wrapper filter
 */
@Order(1)
@Component
public class HttpRequestWrapperFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (request instanceof HttpServletRequest servletRequest) {
            String contentType = servletRequest.getHeader(Header.CONTENT_TYPE.getValue());
            if (ContentType.JSON.getValue().equals(contentType)) {
                chain.doFilter(new RequestWrapper(servletRequest), response);
            } else {
                chain.doFilter(request, response);
            }
        }
    }

}
