package holt.picture.model.dto.file;

import holt.picture.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Request object for requesting a page of pictures
 * Each field of this class represents a query field
 * @author Weiyang Wu
 * @date 2025/4/16 8:55
 */
@EqualsAndHashCode(callSuper=true)
@Data
public class PictureQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * picture name
     */
    private String name;

    /**
     * introduction
     */
    private String introduction;

    /**
     * category
     */
    private String category;

    /**
     * tags（JSON array）
     */
    private List<String> tags;

    /**
     * picture file size
     */
    private Long picSize;

    /**
     * picture width
     */
    private Integer picWidth;

    /**
     * picture height
     */
    private Integer picHeight;

    /**
     * picture width-to-height ratio
     */
    private Double picScale;

    /**
     * picture format
     */
    private String picFormat;

    /**
     * creator id
     */
    private Long creatorId;

    /**
     * General search input
     */
    private String searchText;

    /**
     * Status: 0-Reviewing; 1-Pass; 2-Rejected
     */
    private Integer reviewStatus;

    /**
     * Review message/details
     */
    private String reviewMessage;

    /**
     * Reviewer's ID
     */
    private Long reviewerId;

    /**
     * Space ID
     */
    private Long spaceId;

    /**
     * Whether to query picture with null space ID
     */
    private boolean nullSpaceId;

    /**
     * Starting point of the edit time
     */
    private Date startEditTime;

    /**
     * Ending3
     * point of the edit time
     */
    private Date endEditTime;

    @Serial
    private static final long serialVersionUID = 1L;
}
