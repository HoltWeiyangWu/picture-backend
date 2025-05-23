package holt.picture.manager.auth.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Configuration file that reads the permissions and roles from the JSON config file under resources
 * @author Weiyang Wu
 * @date 2025/5/23 21:15
 */
@Data
public class SpaceUserAuthConfig implements Serializable {

    private List<SpaceUserPermission> permissions;

    private List<SpaceUserRole> roles;

    @Serial
    private static final long serialVersionUID = 1L;
}
