package holt.picture.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.mapper.SpaceMapper;
import holt.picture.model.Picture;
import holt.picture.model.Space;
import holt.picture.model.User;
import holt.picture.model.dto.space.analyse.SpaceAnalyseRequest;
import holt.picture.model.dto.space.analyse.SpaceUsageAnalyseRequest;
import holt.picture.model.vo.space.analyse.SpaceUsageAnalyseResponse;
import holt.picture.service.PictureService;
import holt.picture.service.SpaceAnalyseService;
import holt.picture.service.SpaceService;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Weiyang Wu
 * @date 2025/5/9 21:32
 */
@Service
public class SpaceAnalyseServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceAnalyseService {

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private PictureService pictureService;

    @Override
    public SpaceUsageAnalyseResponse getSpaceUsageAnalyse(SpaceUsageAnalyseRequest request, User loginUser) {
        // 1. Validate parameters
        if (request.isQueryAll() || request.isQueryPublic()) {
            // Query for public space or all picture spaces should be made from Picture table
            // Check authorisation
            checkSpaceAnalyseAuth(request, loginUser);
            QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("picSize");
            fillAnalyseQueryWrapper(request, queryWrapper);
            List<Object> pictureObjList = pictureService.getBaseMapper().selectObjs(queryWrapper);
            long usedSize = pictureObjList.stream()
                    .mapToLong(obj -> (Long) obj)
                    .sum();
            long usedCount = pictureObjList.size();
            SpaceUsageAnalyseResponse spaceUsageAnalyseResponse = new SpaceUsageAnalyseResponse();
            spaceUsageAnalyseResponse.setUsedSize(usedSize);
            spaceUsageAnalyseResponse.setUsedCount(usedCount);
            // Public space or all storage space do not have max size/count nor ratios
            spaceUsageAnalyseResponse.setSizeUsageRatio(null);
            spaceUsageAnalyseResponse.setCountUsageRatio(null);
            spaceUsageAnalyseResponse.setMaxSize(null);
            spaceUsageAnalyseResponse.setMaxCount(null);
            return spaceUsageAnalyseResponse;
        } else {
            // Query for a particular space should be made from Space table
            Long spaceId = request.getSpaceId();
            ThrowUtils.throwIf(spaceId == null || spaceId <= 0, ErrorCode.PARAMS_ERROR);
            Space space = spaceService.getById(spaceId);
            checkSpaceAnalyseAuth(request, loginUser);
            SpaceUsageAnalyseResponse spaceUsageAnalyseResponse = new SpaceUsageAnalyseResponse();
            spaceUsageAnalyseResponse.setUsedSize(space.getTotalSize());
            spaceUsageAnalyseResponse.setUsedCount(space.getTotalCount());
            spaceUsageAnalyseResponse.setMaxSize(space.getMaxSize());
            spaceUsageAnalyseResponse.setMaxCount(space.getMaxCount());
            double sizeUsageRatio =
                    NumberUtil.round(space.getTotalSize() * 100.0 / space.getMaxSize(), 2).doubleValue();
            double countUsageRatio =
                    NumberUtil.round(space.getTotalCount() * 100.0 / space.getMaxCount(), 2).doubleValue();
            spaceUsageAnalyseResponse.setSizeUsageRatio(sizeUsageRatio);
            spaceUsageAnalyseResponse.setCountUsageRatio(countUsageRatio);
            return spaceUsageAnalyseResponse;
        }
    }

    private void checkSpaceAnalyseAuth(SpaceAnalyseRequest spaceAnalyseRequest, User loginUser) {
        boolean queryAll = spaceAnalyseRequest.isQueryAll();
        Long spaceId = spaceAnalyseRequest.getSpaceId();
        boolean queryPublic = spaceAnalyseRequest.isQueryPublic();

        if (queryAll || queryPublic) {
            // Only admin can analyse public space or all picture spaces
            ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR);
        } else {
            // Only creator or admin can analyse a particular picture space
            ThrowUtils.throwIf(spaceId == null, ErrorCode.PARAMS_ERROR, "Space ID is null");
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR, "Space not exist");
            spaceService.checkIfOwnerOrAdmin(loginUser, space);
        }
    }

    private void fillAnalyseQueryWrapper(SpaceAnalyseRequest spaceAnalyseRequest, QueryWrapper<Picture> queryWrapper) {
        boolean queryAll = spaceAnalyseRequest.isQueryAll();
        if (queryAll) {
            return;
        }
        boolean queryPublic = spaceAnalyseRequest.isQueryPublic();
        if (queryPublic) {
            queryWrapper.isNull("spaceId");
            return;
        }
        Long spaceId = spaceAnalyseRequest.getSpaceId();
        if (spaceId != null) {
            queryWrapper.eq("spaceId", spaceId);
            return;
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "Failed to identify the query range");
    }
}
