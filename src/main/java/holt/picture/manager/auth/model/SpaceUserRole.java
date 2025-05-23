package holt.picture.manager.auth.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Define the key and name from each user role in space config file under resources
 * @author Weiyang Wu
 * @date 2025/5/23 21:17
 */
@Data
public class SpaceUserRole implements Serializable {
    /**
     * Key for a specific user role
     */
    private String key;

    /**
     * Name of a user role
     */
    private String name;

    /**
     * Permissions that this user role has
     */
    private List<String> permissions;

    /**
     * Descriptions of the permissions that this user role has
     */
    private String description;

    @Serial
    private static final long serialVersionUID = 1L;

}
