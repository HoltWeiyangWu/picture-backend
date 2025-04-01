package holt.picture.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * User view object without sensitive information
 * @author Weiyang Wu
 * @date 2025/4/1 20:53
 */
@Data
public class LoginUserVO implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * account
     */
    private String userAccount;

    /**
     * user name
     */
    private String userName;

    /**
     * avatar
     */
    private String userAvatar;

    /**
     * profile
     */
    private String userProfile;

    /**
     * Roles：user/admin
     */
    private String userRole;

    /**
     * Edit time
     */
    private Date editTime;

    /**
     * Create time
     */
    private Date createTime;
}
