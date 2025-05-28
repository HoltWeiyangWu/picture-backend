package holt.picture.model.vo;

import cn.hutool.json.JSONUtil;
import holt.picture.model.Picture;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Weiyang Wu
 * @date 2025/4/10 22:08
 */
@Data
public class PictureVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * picture url
     */
    private String url;

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
     * Space id
     */
    private Long spaceId;
    /**
     * create time
     */
    private Date createTime;

    /**
     * edit time
     */
    private Date editTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * Creator of this picture
     */
    private UserVO creator;

    /**
     * A list of access permission of the current object
     */
    private List<String> permissionList = new ArrayList<>();
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Convert view object to entity
     */
    public static Picture voToObject(PictureVO pictureVO) {
        if (pictureVO == null) return null;
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureVO, picture);
        // Convert types for tags
        picture.setTags(JSONUtil.toJsonStr(pictureVO.getTags()));
        return picture;
    }

    /**
     * Convert entity to view object
     */
    public static PictureVO objectToVo(Picture picture) {
        if (picture == null) return null;
        PictureVO pictureVO = new PictureVO();
        BeanUtils.copyProperties(picture, pictureVO);
        pictureVO.setTags(JSONUtil.toList(picture.getTags(), String.class));
        return pictureVO;
    }
}
