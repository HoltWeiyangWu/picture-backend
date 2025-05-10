package holt.picture.model.dto.space.analyse;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Weiyang Wu
 * @date 2025/5/10 13:37
 */
@Data
public class SpaceRankAnalyseRequest implements Serializable {
    /**
     * The first N-th from spaces
     */
    private Integer topN = 10;

    @Serial
    private static final long serialVersionUID = 1L;
}
