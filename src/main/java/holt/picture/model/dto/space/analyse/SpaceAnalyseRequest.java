package holt.picture.model.dto.space.analyse;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Customisable request for space analysis
 * @author Weiyang Wu
 * @date 2025/5/9 21:28
 */
@Data
public class SpaceAnalyseRequest implements Serializable {
    /**
     * Space ID
     */
    private Long spaceId;
    /**
     * Analyse public picture space
     */
    private boolean queryPublic;

    /**
     * Analyse all the picture space
     */
    private boolean queryAll;

    @Serial
    private static final long serialVersionUID= 1L;
}
