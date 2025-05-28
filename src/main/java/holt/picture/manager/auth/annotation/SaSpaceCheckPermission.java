package holt.picture.manager.auth.annotation;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import holt.picture.manager.auth.StpKit;
import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Weiyang Wu
 * @date 2025/5/26 19:22
 */

@SaCheckPermission(type = StpKit.SPACE_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SaSpaceCheckPermission {
    /**
     * Value of authentication
     */
    @AliasFor(annotation = SaCheckPermission.class)
    String[] value() default {};

    /**
     * Authentication mode: AND | OR. Default AND
     */
    @AliasFor(annotation = SaCheckPermission.class)
    SaMode mode() default SaMode.AND;

    /**
     * Auxiliary authentication
     *
     * <p>
     * E.g.1：@SaCheckPermission(value="user-add", orRole="admin")，
     * Means that requests with "user-add" or with admin roles are allowed
     * </p>
     *
     * <p>
     * E.g.2： orRole = {"admin", "manager", "staff"}. Either one of them should should work <br>
     * E.g.3： orRole = {"admin, manager, staff"}. All are required
     * </p>
     */
    @AliasFor(annotation = SaCheckPermission.class)
    String[] orRole() default {};
}
