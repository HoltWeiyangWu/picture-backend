package holt.picture.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Weiyang Wu
 * @date 2025/4/10 22:07
 */
@Data
public class PictureUploadRequest implements Serializable {

    /**
     * Picture ID
     */
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;
}
