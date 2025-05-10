package holt.picture.model.vo.space.analyse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Weiyang Wu
 * @date 2025/5/10 10:06
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceCategoryAnalyseResponse implements Serializable {
    /**
     * Picture category
     */
    private String category;

    /**
     * Picture number/count
     */
    private Long count;

    /**
     * Total size of the current selected category
     */
    private Long totalSize;

    @Serial
    private static final long serialVersionUID = 1L;
}
