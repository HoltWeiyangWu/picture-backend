package holt.picture.model.dto.file;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Request details to import pictures by batch
 * @author Weiyang Wu
 * @date 2025/4/25 11:26
 */
@Data
public class UploadPictureByBatchRequest implements Serializable {

    /**
     * Search text for batch import
     */
    private String searchText;

    /**
     * Size of batch
     */
    private Integer count = 10;

    /**
     * Picture name prefix
     */
    private String namePrefix;

    @Serial
    private static final long serialVersionUID = 1L;
}
