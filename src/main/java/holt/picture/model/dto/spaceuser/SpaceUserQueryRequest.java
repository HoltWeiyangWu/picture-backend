package holt.picture.model.dto.spaceuser;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Weiyang Wu
 * @date 2025/5/14 8:41
 */
@Data
public class SpaceUserQueryRequest implements Serializable {
    /**
     * Corresponding relation object id
     */
    private Long id;

    /**
     * Space id
     */
    private Long spaceId;

    /**
     * User id
     */
    private Long userId;

    /**
     * User role in this space：viewer/editor/admin
     */
    private String role;

    @Serial
    private static final long serialVersionUID = 1L;
}
