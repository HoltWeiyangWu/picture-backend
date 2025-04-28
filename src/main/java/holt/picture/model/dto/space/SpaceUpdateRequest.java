package holt.picture.model.dto.space;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Weiyang Wu
 * @date 2025/4/28 13:01
 */
@Data
public class SpaceUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * Space name
     */
    private String spaceName;

    /**
     * Space level 0-Personal 1-Professional 2-Flagship
     */
    private Integer spaceLevel;

    /**
     * Maximal storage size
     */
    private Long maxSize;

    /**
     * Maximal picture number
     */
    private Long maxCount;

    @Serial
    private static final long serialVersionUID = 1L;
}
