package holt.picture.model.vo;

import holt.picture.model.Space;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Weiyang Wu
 * @date 2025/4/28 13:04
 */
@Data
public class SpaceVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * Space name
     */
    private String spaceName;

    /**
     * Space level 0-Personal 1-Business 2-Flagship
     */
    private Integer spaceLevel;

    /**
     * Maximal storage size
     */
    private Long maxSize;

    /**
     * Maximal picture number
     */
    private Long maxCount;

    /**
     * Total storage used in the current space
     */
    private Long totalSize;

    /**
     * Total number of pictures in the current space
     */
    private Long totalCount;

    /**
     * Creator ID
     */
    private Long creatorId;

    /**
     * Create time
     */
    private Date createTime;

    /**
     * Edit time
     */
    private Date editTime;

    /**
     * Update time by Admins
     */
    private Date updateTime;

    /**
     * Space type: 0-private 1-team
     */
    private Integer spaceType;

    /**
     * A list of access permission of the current object
     */
    private List<String> permissionList = new ArrayList<>();

    private UserVO creator;

    @Serial
    private static final long serialVersionUID = 1L;

    public static Space voToObj(SpaceVO spaceVO) {
        if (spaceVO == null) {
            return null;
        }
        Space space = new Space();
        BeanUtils.copyProperties(spaceVO, space);
        return space;
    }

    public static SpaceVO objToVo(Space space) {
        if (space == null) {
            return null;
        }
        SpaceVO spaceVO = new SpaceVO();
        BeanUtils.copyProperties(space, spaceVO);
        return spaceVO;
    }
}
