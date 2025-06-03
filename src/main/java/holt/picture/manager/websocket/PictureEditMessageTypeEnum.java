package holt.picture.manager.websocket;

import lombok.Getter;

/**
 * Specifies the editing state of action for a particular user
 * @author Weiyang Wu
 * @date 2025/6/2 14:00
 */
@Getter
public enum PictureEditMessageTypeEnum {

    INFO("Send information", "INFO"),
    ERROR("Send error", "ERROR"),
    ENTER_EDIT("Entering edit", "ENTER_EDIT"),
    EXIT_EDIT("Exiting edit", "EXIT_EDIT"),
    EDIT_ACTION("Editing action", "EDIT_ACTION");

    private final String text;

    private final String value;

    PictureEditMessageTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static PictureEditMessageTypeEnum getEnumByValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (PictureEditMessageTypeEnum e : PictureEditMessageTypeEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }
}
