package holt.picture.model.dto.file;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Weiyang Wu
 * @date 2025/5/5 16:40
 */
@Data
public class PictureEditByBatchRequest implements Serializable {
    /**
     * Pictures' ID list
     */
    private List<Long> pictureIdList;

    /**
     * Space id
     */
    private Long spaceId;

    /**
     * category
     */
    private String category;

    /**
     * tags（JSON array）
     */
    private List<String> tags;

    /**
     * Naming Rule
     */
    private String namingRule;

    @Serial
    private static final long serialVersionUID = 1L;
}
