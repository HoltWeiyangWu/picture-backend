package holt.picture.model.dto.space;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Weiyang Wu
 * @date 2025/4/28 13:01
 */
@Data
public class SpaceEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * Space name
     */
    private String spaceName;

    @Serial
    private static final long serialVersionUID = 1L;
}
