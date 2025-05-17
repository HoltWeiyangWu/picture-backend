package holt.picture.model.dto.spaceuser;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Add a user to an existing space
 * @author Weiyang Wu
 * @date 2025/5/14 8:37
 */
@Data
public class SpaceUserAddRequest implements Serializable {
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
