package holt.picture.model.vo.space.analyse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Weiyang Wu
 * @date 2025/5/10 10:20
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceTagAnalyseResponse implements Serializable {
    /**
     * Picture tag
     */
    private String tag;

    /**
     * Counting the number of appearance of the selected tag (frequency)
     */
    private Long count;

    @Serial
    private static final long serialVersionUID = 1L;
}
