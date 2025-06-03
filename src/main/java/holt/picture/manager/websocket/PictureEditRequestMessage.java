package holt.picture.manager.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Weiyang Wu
 * @date 2025/6/2 11:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PictureEditRequestMessage {
    /**
     * Message type, e.g. "ENTER_EDIT", EXIT_EDIT"", "EDIT_ACTION"
     */
    private String type;

    /**
     * Edit action from user
     */
    private String editAction;

}
