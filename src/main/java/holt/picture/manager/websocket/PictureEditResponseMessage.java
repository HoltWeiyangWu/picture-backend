package holt.picture.manager.websocket;

import holt.picture.model.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Weiyang Wu
 * @date 2025/6/2 11:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PictureEditResponseMessage {

    /**
     * Message type, e.g. "ENTER_EDIT", EXIT_EDIT"", "EDIT_ACTION"
     */
    private String type;

    /**
     * Message
     */
    private String message;

    /**
     * Edit action from user
     */
    private String editAction;

    /**
     * User information
     */
    private UserVO user;
}
