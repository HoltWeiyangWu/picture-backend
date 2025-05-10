package holt.picture.model.dto.space.analyse;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Weiyang Wu
 * @date 2025/5/10 12:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUserAnalyseRequest extends SpaceAnalyseRequest {
    /**
     * User ID
     */
    private Long userId;

    /**
     * Time dimension: day/ week/ month
     */
    private String timeDimension;
}
