package holt.picture.model.dto.file;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * A request class for admin to change a picture review status
 * @author Weiyang Wu
 * @date 2025/4/19 10:09
 */
@Data
public class PictureReviewRequest implements Serializable {
    /**
     * Picture's ID
     */
    private long id;

    /**
     * Status: 0-Reviewing; 1-Pass; 2-Rejected
     */
    private Integer reviewStatus;

    /**
     * Review message/details
     */
    private String reviewMessage;

    @Serial
    private static final long serialVersionUID = 1L;
}
