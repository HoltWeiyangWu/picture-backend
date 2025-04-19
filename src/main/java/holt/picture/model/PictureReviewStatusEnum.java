package holt.picture.model;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * An enumerator class that outlines the status codes for picture review process
 * @author Weiyang Wu
 * @date 2025/4/19 10:03
 */
@Getter
public enum PictureReviewStatusEnum {
    REVIEWING("Reviewing", 0),
    PASS("Pass", 1),
    REJECTED("Rejected", 2);


    private final String text;
    private final int value;

    PictureReviewStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public static PictureReviewStatusEnum getEnumByValue(int value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (PictureReviewStatusEnum e : PictureReviewStatusEnum.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null;
    }
}
