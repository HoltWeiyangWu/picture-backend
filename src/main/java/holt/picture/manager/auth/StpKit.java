package holt.picture.manager.auth;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

/**
 * Sa-Token Kit class to handle all StpLogic accounts in this project
 * We make it a Component to ensure that DEFAULT and SPACE are initialised
 * Can be extended to other authentication accounts/systems
 * @author Weiyang Wu
 * @date 2025/5/23 22:12
 */
@Component
public class StpKit {
    public static final String SPACE_TYPE = "space";

    /**
     * Default session object in Sa-Token Kit package
     */
    public static final StpLogic DEFAULT = StpUtil.stpLogic;

    /**
     * Space session object to handle all authentication check in picture storage space
     */
    public static final StpLogic SPACE = new StpLogic(SPACE_TYPE);
}
