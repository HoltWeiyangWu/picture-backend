package holt.picture.manager.auth.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Define the key and name from permissions in space config file under resources
 * @author Weiyang Wu
 * @date 2025/5/23 21:17
 */
@Data
public class SpaceUserPermission implements Serializable {
    /**
     * Key for a specific user permission
     */
    private String key;

    /**
     * Value for a specific user permission
     */
    private String name;

    /**
     * Description of the permission details
     */
    private String description;

    @Serial
    private static final long serialVersionUID = 1L;
}
