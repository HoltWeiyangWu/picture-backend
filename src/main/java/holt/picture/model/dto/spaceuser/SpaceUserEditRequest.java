package holt.picture.model.dto.spaceuser;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Edit a user role in an existing space
 * @author Weiyang Wu
 * @date 2025/5/14 8:39
 */
@Data
public class SpaceUserEditRequest implements Serializable {
    /**
     * Corresponding relation object id
     */
    private Long id;

    /**
     * User role in this space：viewer/editor/admin
     */
    private String role;

    @Serial
    private static final long serialVersionUID = 1L;
}
