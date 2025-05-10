package holt.picture.service;

import com.baomidou.mybatisplus.extension.service.IService;
import holt.picture.model.Space;
import holt.picture.model.User;
import holt.picture.model.dto.space.analyse.*;
import holt.picture.model.vo.space.analyse.*;

import java.util.List;

/**
 * @author Weiyang Wu
 * @date 2025/5/9 21:31
 */
public interface SpaceAnalyseService extends IService<Space> {
    SpaceUsageAnalyseResponse getSpaceUsageAnalyse(SpaceUsageAnalyseRequest request, User loginUser);
    List<SpaceCategoryAnalyseResponse> getSpaceCategoryAnalyse(SpaceCategoryAnalyseRequest request, User loginUser);
    List<SpaceTagAnalyseResponse> getSpaceTagAnalyse(SpaceTagAnalyseRequest request, User loginUser);
    List<SpaceSizeAnalyseResponse> getSpaceSizeAnalyse(SpaceSizeAnalyseRequest request, User loginUser);
    List<SpaceUserAnalyseResponse> getSpaceUserAnalyse(SpaceUserAnalyseRequest request, User loginUser);
    List<Space> getSpaceRankAnalyse(SpaceRankAnalyseRequest request, User loginUser);
}
