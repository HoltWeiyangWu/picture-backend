package holt.picture.model.dto.file;

import holt.picture.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
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

    @Serial
    private static final long serialVersionUID = 1L;
}
