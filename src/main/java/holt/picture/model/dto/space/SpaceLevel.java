package holt.picture.model.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Outlines storage space level information
 * @author Weiyang Wu
 * @date 2025/4/28 21:28
 */
@Data
@AllArgsConstructor
public class SpaceLevel {
    private int value;

    private String text;

    /**
     * Maximal storage size
     */
    private Long maxSize;

    /**
     * Maximal picture number
     */
    private Long maxCount;
}
