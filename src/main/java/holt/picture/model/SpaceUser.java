package holt.picture.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * space_user_relation_table
 */
@TableName(value ="space_user")
@Data
public class SpaceUser implements Serializable {
    /**
     * Table id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Space id
     */
    private Long spaceId;

    /**
     * User id
     */
    private Long userId;

    /**
     * User role in this space：viewer/editor/admin
     */
    private String role;

    /**
     * Create time
     */
    private Date createTime;

    /**
     * Update time
     */
    private Date updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}