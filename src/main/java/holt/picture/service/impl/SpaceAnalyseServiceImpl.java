package holt.picture.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.mapper.SpaceMapper;
import holt.picture.model.Picture;
import holt.picture.model.Space;
import holt.picture.model.User;
import holt.picture.model.dto.space.analyse.*;
import holt.picture.model.vo.space.analyse.*;
import holt.picture.service.PictureService;
import holt.picture.service.SpaceAnalyseService;
import holt.picture.service.SpaceService;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public List<SpaceCategoryAnalyseResponse> getSpaceCategoryAnalyse(SpaceCategoryAnalyseRequest request, User loginUser) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        checkSpaceAnalyseAuth(request, loginUser);

        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyseQueryWrapper(request, queryWrapper);

        queryWrapper.select("category AS category",
                        "COUNT(*) AS count",
                        "SUM(picSize) AS totalSize")
                .groupBy("category");
        // Query and convert result
        return pictureService.getBaseMapper().selectMaps(queryWrapper)
                .stream()
                .map(result -> {
                    String category = result.get("category") != null ? (String) result.get("category") : "Default";
                    Long count = ((Number) result.get("count")).longValue();
                    Long totalSize = ((Number) result.get("totalSize")).longValue();
                    return new SpaceCategoryAnalyseResponse(category, count, totalSize);
                })
                .toList();
    }

    @Override
    public List<SpaceTagAnalyseResponse> getSpaceTagAnalyse(SpaceTagAnalyseRequest request, User loginUser) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        checkSpaceAnalyseAuth(request, loginUser);

        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyseQueryWrapper(request, queryWrapper);

        // Query and convert result
        queryWrapper.select("tags");
        List<String> tagsJsonList = pictureService.getBaseMapper().selectObjs(queryWrapper)
                .stream()
                .filter(ObjUtil::isNotNull)
                .map(Object::toString)
                .toList();
        Map<String, Long> tagCountMap = tagsJsonList.stream()
                .flatMap(tagsJson-> JSONUtil.toList(tagsJson, String.class).stream())
                .collect(Collectors.groupingBy(tag->tag, Collectors.counting()));
        return tagCountMap.entrySet().stream()
                .sorted((e1, e2)-> Long.compare(e2.getValue(), e1.getValue())) // Descending order
                .map(entry-> new SpaceTagAnalyseResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public List<SpaceSizeAnalyseResponse> getSpaceSizeAnalyse(SpaceSizeAnalyseRequest request, User loginUser) {
        ThrowUtils.throwIf(request== null, ErrorCode.PARAMS_ERROR);
        checkSpaceAnalyseAuth(request, loginUser);

        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyseQueryWrapper(request, queryWrapper);

        queryWrapper.select("picSize");
        List<Long> picSizes = pictureService.getBaseMapper().selectObjs(queryWrapper)
                .stream()
                .map(size -> ((Number) size).longValue())
                .toList();

        Map<String, Long> sizeRanges = new LinkedHashMap<>();
        sizeRanges.put("<100KB", picSizes.stream().filter(size->size < 100 * 1024).count());
        sizeRanges.put("100KB-500KB", picSizes.stream().filter(size->size >= 100 * 1024 && size < 500 * 1024).count());
        sizeRanges.put("500KB-1MB", picSizes.stream().filter(size->size >= 500 * 1024 && size < 1024 * 1024).count());
        sizeRanges.put(">1MB", picSizes.stream().filter(size->size >= 1024 * 1024).count());

        return sizeRanges.entrySet().stream()
                .map(entry-> new SpaceSizeAnalyseResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public List<SpaceUserAnalyseResponse> getSpaceUserAnalyse(SpaceUserAnalyseRequest request, User loginUser) {
        ThrowUtils.throwIf(request==null, ErrorCode.PARAMS_ERROR);
        checkSpaceAnalyseAuth(request, loginUser);

        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        Long userId = request.getUserId();
        queryWrapper.eq(ObjUtil.isNotNull(userId),"creatorId", userId);
        fillAnalyseQueryWrapper(request, queryWrapper);

        String timeDimension = request.getTimeDimension();
        switch (timeDimension) {
            case "day":
                queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m-%d') AS period", "COUNT(*) AS count");
                break;
            case "week":
                queryWrapper.select("YEARWEEK(createTime) AS period", "COUNT(*) AS count");
                break;
            case "month":
                queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m') AS period", "COUNT(*) AS count");
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Invalid time dimension");
        }

        // Group and order
        queryWrapper.groupBy("period").orderByAsc("period");

        // Query result and convert types
        List<Map<String, Object>> queryResult = pictureService.getBaseMapper().selectMaps(queryWrapper);
        return queryResult.stream()
                .map(result-> {
                    String period = result.get("period").toString();
                    Long count = ((Number) result.get("count")).longValue();
                    return new SpaceUserAnalyseResponse(period, count);
                })
                .toList();
    }

    @Override
    public List<Space> getSpaceRankAnalyse(SpaceRankAnalyseRequest request, User loginUser) {
        ThrowUtils.throwIf(request==null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR);

        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "spaceName", "creatorId", "totalSize")
                .orderByDesc("totalSize")
                .last("LIMIT " + request.getTopN());
        return spaceService.list(queryWrapper);
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
