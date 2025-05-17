package holt.picture.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author Weiyang Wu
 * @date 2025/5/14 8:47
 */
@Getter
public enum SpaceRoleEnum {
    VIEWER("Viewer", "viewer"),
    EDITOR("Editor", "editor"),
    ADMIN("Admin", "admin");


    private final String text;
    private final String value;

    SpaceRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static SpaceRoleEnum getEnum(String value) {
        if (value == null) {
            return null;
        }
        for (SpaceRoleEnum e : values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }


    /**
     * Get all available enum texts
     */
    public static List<String> getAllTexts() {
        return Arrays.stream(SpaceRoleEnum.values())
                .map(SpaceRoleEnum::getText)
                .toList();
    }

    /**
     * Get all available enum values
     */
    public static List<String> getAllValues() {
        return Arrays.stream(SpaceRoleEnum.values())
                .map(SpaceRoleEnum::getValue)
                .toList();
    }
}
