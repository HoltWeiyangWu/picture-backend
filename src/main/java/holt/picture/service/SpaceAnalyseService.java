package holt.picture.service;

import com.baomidou.mybatisplus.extension.service.IService;
import holt.picture.model.Space;
import holt.picture.model.User;
import holt.picture.model.dto.space.analyse.SpaceUsageAnalyseRequest;
import holt.picture.model.vo.space.analyse.SpaceUsageAnalyseResponse;

/**
 * @author Weiyang Wu
 * @date 2025/5/9 21:31
 */
public interface SpaceAnalyseService extends IService<Space> {
    SpaceUsageAnalyseResponse getSpaceUsageAnalyse(SpaceUsageAnalyseRequest request, User loginUser);
}
