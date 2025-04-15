package holt.picture.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * picture object
 */
@TableName(value ="picture")
@Data
public class Picture implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
    private String tags;

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
     * isDeleted
     */
    @TableLogic
    private Integer isDeleted;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}