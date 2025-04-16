package holt.picture.model.dto.file;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Updating a picture's information. Available only to admin
 * @author Weiyang Wu
 * @date 2025/4/16 8:43
 */
@Data
public class PictureUpdateRequest implements Serializable {
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

    @Serial
    private static final long serialVersionUID = 1L;

}
