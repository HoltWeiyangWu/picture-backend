package holt.picture.model.vo.space.analyse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Weiyang Wu
 * @date 2025/5/10 12:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceSizeAnalyseResponse implements Serializable {
    /**
     * The range of picture size
     */
    private String sizeRange;

    /**
     * Number of pictures
     */
    private Long count;

    @Serial
    private static final long serialVersionUID= 1L;
}
