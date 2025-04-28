package holt.picture.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Picture storage space
 */
@TableName(value ="space")
@Data
public class Space implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Space name
     */
    private String spaceName;

    /**
     * Space level 0-Personal 1-Business 2-Flagship
     */
    private Integer spaceLevel;

    /**
     * Maximal storage size
     */
    private Long maxSize;

    /**
     * Maximal picture number
     */
    private Long maxCount;

    /**
     * Total storage used in the current space
     */
    private Long totalSize;

    /**
     * Total number of pictures in the current space
     */
    private Long totalCount;

    /**
     * Creator ID
     */
    private Long creatorId;

    /**
     * Create time
     */
    private Date createTime;

    /**
     * Edit time
     */
    private Date editTime;

    /**
     * Update time by Admins
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