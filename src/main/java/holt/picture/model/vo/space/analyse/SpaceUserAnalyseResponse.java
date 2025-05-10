package holt.picture.model.vo.space.analyse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Weiyang Wu
 * @date 2025/5/10 12:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceUserAnalyseResponse implements Serializable {
    /**
     * Time period
     */
    private String period;

    /**
     * Upload number
     */
    private Long count;

    @Serial
    private static final long serialVersionUID = 1L;
}
