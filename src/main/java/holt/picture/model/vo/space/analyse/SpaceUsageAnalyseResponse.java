package holt.picture.model.vo.space.analyse;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Weiyang Wu
 * @date 2025/5/9 21:56
 */
@Data
public class SpaceUsageAnalyseResponse implements Serializable {
    /**
     * Used storage space size
     */
    private Long usedSize;

    /**
     * Maximum storage space size
     */
    private Long maxSize;

    /**
     * Ratio of used storage space to total storage space
     */
    private Double sizeUsageRatio;

    /**
     * Current number of pictures
     */
    private Long usedCount;

    /**
     * Maximum number of pictures
     */
    private Long maxCount;

    /**
     * Ratio of current number of pictures to maximum number of pictures
     */
    private Double countUsageRatio;
}
