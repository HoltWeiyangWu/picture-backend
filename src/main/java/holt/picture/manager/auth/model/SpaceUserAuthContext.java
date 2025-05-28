package holt.picture.manager.auth.model;

import holt.picture.model.Picture;
import holt.picture.model.Space;
import holt.picture.model.SpaceUser;
import lombok.Data;

/**
 * Define authentication parameters when users try to log in to a space
 * @author Weiyang Wu
 * @date 2025/5/24 7:55
 */
@Data
public class SpaceUserAuthContext {
    private Long id;

    private Long pictureId;

    private Long spaceId;

    private Long spaceUserId;

    private Picture picture;

    private Space space;

    private SpaceUser spaceUser;
}
