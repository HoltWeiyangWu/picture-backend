package holt.picture.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Specify picture tag and category for frontend
 * @author Weiyang Wu
 * @date 2025/4/16 11:05
 */
@Data
public class PictureTagCategory implements Serializable {
    private List<String> tagList;

    private List<String> categoryList;
}
