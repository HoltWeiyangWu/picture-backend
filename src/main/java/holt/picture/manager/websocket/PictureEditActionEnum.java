package holt.picture.manager.websocket;

import lombok.Getter;

/**
 * Specifies the four types of edit action
 * @author Weiyang Wu
 * @date 2025/6/2 14:05
 */
@Getter
public enum PictureEditActionEnum {
    ZOOM_IN("zoom in", "zoom in"),
    ZOOM_OUT("zoom out", "zoom out"),
    ROTATE_LEFT("rotate left", "rotate left"),
    ROTATE_RIGHT("rotate right", "rotate right"),;

    private final String text;

    private final String value;

    PictureEditActionEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static PictureEditActionEnum getEnumByValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (PictureEditActionEnum e : PictureEditActionEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }
}
