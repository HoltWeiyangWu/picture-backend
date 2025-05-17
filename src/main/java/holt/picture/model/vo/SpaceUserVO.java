package holt.picture.model.vo;

import holt.picture.model.SpaceUser;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Weiyang Wu
 * @date 2025/5/14 8:43
 */
@Data
public class SpaceUserVO implements Serializable {
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

    /**
     * Create time
     */
    private Date createTime;

    /**
     * Update time
     */
    private Date updateTime;

    /**
     * Associated user view object
     */
    private UserVO userVO;

    /**
     * Associated storage space view object
     */
    private SpaceVO spaceVO;

    @Serial
    private static final long serialVersionUID = 1L;

    public static SpaceUser voToObject(SpaceUserVO vo) {
        if (vo == null) {
            return null;
        }
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(vo, spaceUser);
        return spaceUser;
    }

    public static SpaceUserVO objectToVo(SpaceUser spaceUser) {
        if (spaceUser == null) {
            return null;
        }
        SpaceUserVO spaceUserVO = new SpaceUserVO();
        BeanUtils.copyProperties(spaceUser, spaceUserVO);
        return spaceUserVO;
    }
}
