package holt.picture.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * @author Weiyang Wu
 * @date 2025/4/28 13:08
 */
@Getter
public enum SpaceLevelEnum {

    PERSONAL("Personal", 0, 100, 100L*1024*1024),
    PROFESSIONAL("Professional", 1, 1000, 1000L*1024*1024),
    FLAGSHIP("Flagship", 2, 10000, 10000L*1024*1024);

    private final String text;

    private final int value;

    private final long maxCount;

    private final long maxSize;


    SpaceLevelEnum(String text, int value, long maxCount, long maxSize) {
        this.text = text;
        this.value = value;
        this.maxCount = maxCount;
        this.maxSize = maxSize;
    }

    public static SpaceLevelEnum getSpaceLevelEnum(Integer value) {
        if(ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceLevelEnum spaceLevelEnum : SpaceLevelEnum.values()) {
            if(spaceLevelEnum.getValue() == value) {
                return spaceLevelEnum;
            }
        }
        return null;

    }
}
