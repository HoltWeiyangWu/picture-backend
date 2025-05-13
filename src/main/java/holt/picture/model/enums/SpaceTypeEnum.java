package holt.picture.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * @author Weiyang Wu
 * @date 2025/5/13 22:50
 */
@Getter
public enum SpaceTypeEnum {

    PRIVATE("Private Space", 0),
    PUBLIC("Team Space", 1);

    private final String text;

    private final Integer value;

    SpaceTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public static SpaceTypeEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceTypeEnum e : SpaceTypeEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }
}
