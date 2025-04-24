package holt.picture.aop;

import holt.picture.annotation.AuthCheck;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.model.User;
import holt.picture.model.enums.UserRoleEnum;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Weiyang Wu
 * @date 2025/4/2 12:08
 */
@Aspect
@Component
public class AuthInterceptor {
    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doIntercept(ProceedingJoinPoint pjp, AuthCheck authCheck) throws Throwable {
        String requiredRole = authCheck.requiredRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        // Get user state
        User user = userService.getLoginUser(request);
        UserRoleEnum requiredRoleEnum = UserRoleEnum.getByValue(requiredRole);
        // Proceed if authentication is not required
        if (requiredRoleEnum == null) {
            return pjp.proceed();
        }
        UserRoleEnum userRoleEnum = UserRoleEnum.getByValue(user.getUserRole());
        // Unregistered user cannot access authorised methods
        ThrowUtils.throwIf(userRoleEnum==null, ErrorCode.NO_AUTH_ERROR);
        // Unauthorised access by user
        boolean requireAdmin = requiredRoleEnum.equals(UserRoleEnum.ADMIN);
        boolean isAdmin = userRoleEnum.equals(UserRoleEnum.ADMIN);
        ThrowUtils.throwIf(requireAdmin && !isAdmin, ErrorCode.NO_AUTH_ERROR);
        return pjp.proceed();
    }
}
