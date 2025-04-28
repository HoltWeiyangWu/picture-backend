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
     * isDeleted
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * Status: 0-Reviewing; 1-Pass; 2-Rejected
     */
    private Integer reviewStatus;

    /**
     * Review message/details
     */
    private String reviewMessage;

    /**
     * Reviewer's ID
     */
    private Long reviewerId;

    /**
     * Review time
     */
    private Date reviewTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}