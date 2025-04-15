package holt.picture.model.dto.file;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * A request from front-end specifying picture ID to upload an image
 * If the given ID is null, it means that we are uploading a new image,
 * otherwise, we are updating an existing image by overwriting.
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
