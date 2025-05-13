package holt.picture.model.dto.space;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Weiyang Wu
 * @date 2025/4/28 12:59
 */
@Data
public class SpaceAddRequest implements Serializable {
    /**
     * Space name
     */
    private String spaceName;

    /**
     * Space level 0-Personal 1-Professional 2-Flagship
     */
    private Integer spaceLevel;

    /**
     * Space type: 0-private 1-team
     */
    private Integer spaceType;

    @Serial
    private static final long serialVersionUID = 1L;
}
