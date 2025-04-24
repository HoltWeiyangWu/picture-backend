package holt.picture.model.enums;

import holt.picture.constant.UserConstant;
import lombok.Getter;

/**
 * Define user role
 * @author Weiyang Wu
 * @date 2025/4/1 18:58
 */
@Getter
public enum UserRoleEnum {
    USER("User", UserConstant.USER_ROLE),
    ADMIN("Administrator", UserConstant.ADMIN_ROLE);

    private final String text;
    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * Get Enum object by value
     */
    public static UserRoleEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (UserRoleEnum e : UserRoleEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }
}
