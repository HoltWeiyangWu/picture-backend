package holt.picture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.model.Space;
import holt.picture.model.User;
import holt.picture.model.dto.space.SpaceQueryRequest;
import holt.picture.model.enums.SpaceLevelEnum;
import holt.picture.model.vo.SpaceVO;
import holt.picture.model.vo.UserVO;
import holt.picture.service.SpaceService;
import holt.picture.mapper.SpaceMapper;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Weiyang Wu
* @date 2025-04-28 12:55:05
*/
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceService{

    @Resource
    private UserService userService;

    @Override
    public QueryWrapper<Space> getSpaceQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        Long id = spaceQueryRequest.getId();
        Long creatorId = spaceQueryRequest.getCreatorId();
        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();


        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(creatorId), "creatorId", creatorId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "spaceName", spaceName);
        queryWrapper.like(ObjUtil.isNotEmpty(spaceLevel), "spaceLevel", spaceLevel);

        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        SpaceVO spaceVO = new SpaceVO();
        Long creatorId = space.getCreatorId();
        if (creatorId != null && creatorId > 0) {
            User creator = userService.getById(creatorId);
            UserVO creatorVO = userService.getUserVO(creator);
            spaceVO.setCreator(creatorVO);
        }
        return spaceVO;
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {

        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(),
                spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(spaceList)) {
            return spaceVOPage;
        }

        List<SpaceVO> spaceVOList = spaceList.stream()
                .map(SpaceVO::objToVo)
                .toList();
        Set<Long> userIdSet = spaceVOList.stream()
                .map(SpaceVO::getCreatorId)
                .collect(Collectors.toSet());

        // Find a unique set of users instead of finding a user according to a space
        Map<Long, List<User>> idToUserlistMap = userService.listByIds(userIdSet)
                .stream()
                .collect(Collectors.groupingBy(User::getId));

        // Append user information into the vo object
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getCreatorId();
            User user = null;
            if (idToUserlistMap.containsKey(userId)) {
                user = idToUserlistMap.get(userId).get(0);
            }
            spaceVO.setCreator(userService.getUserVO(user));
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }

    @Override
    public void validSpace(Space space, boolean isAdding) {
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getSpaceLevelEnum(spaceLevel);
        // Validate when creation
        if (isAdding) {
            ThrowUtils.throwIf(StrUtil.isBlank(spaceName), ErrorCode.PARAMS_ERROR, "Space name cannot be empty");
            ThrowUtils.throwIf(spaceLevel == null, ErrorCode.PARAMS_ERROR, "Space level cannot be empty");
        }
        ThrowUtils.throwIf(StrUtil.isNotBlank(spaceName) && spaceName.length() > 30,
                ErrorCode.PARAMS_ERROR, "Space name is too long");
        ThrowUtils.throwIf(spaceLevel != null && spaceLevelEnum == null,
                ErrorCode.PARAMS_ERROR, "Space level does not exist");
    }

    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getSpaceLevelEnum(space.getSpaceLevel());
        if (spaceLevelEnum != null) {
            long maxSize = spaceLevelEnum.getMaxSize();
            if (space.getMaxSize() == null) {
                space.setMaxSize(maxSize);
            }
            long maxCount = spaceLevelEnum.getMaxCount();
            if (space.getMaxCount() == null) {
                space.setMaxCount(maxCount);
            }
        }
    }
}




