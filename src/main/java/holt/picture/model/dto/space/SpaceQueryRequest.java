package holt.picture.model.dto.space;

import holt.picture.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Weiyang Wu
 * @date 2025/4/28 13:02
 */
@EqualsAndHashCode(callSuper=true)
@Data
public class SpaceQueryRequest extends PageRequest implements Serializable {
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
     * Creator ID
     */
    private Long creatorId;

    /**
     * Space type: 0-private 1-team
     */
    private Integer spaceType;

    @Serial
    private static final long serialVersionUID = 1L;

}
