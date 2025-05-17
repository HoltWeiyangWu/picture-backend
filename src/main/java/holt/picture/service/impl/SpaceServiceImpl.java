package holt.picture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.model.Space;
import holt.picture.model.SpaceUser;
import holt.picture.model.User;
import holt.picture.model.dto.space.SpaceAddRequest;
import holt.picture.model.dto.space.SpaceQueryRequest;
import holt.picture.model.enums.SpaceLevelEnum;
import holt.picture.model.enums.SpaceRoleEnum;
import holt.picture.model.enums.SpaceTypeEnum;
import holt.picture.model.vo.SpaceVO;
import holt.picture.model.vo.UserVO;
import holt.picture.service.SpaceService;
import holt.picture.mapper.SpaceMapper;
import holt.picture.service.SpaceUserService;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private TransactionTemplate transactionTemplate;

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
        Integer spaceType = spaceQueryRequest.getSpaceType();

        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(creatorId), "creatorId", creatorId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceType), "spaceType", spaceType);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "spaceName", spaceName);
        queryWrapper.like(ObjUtil.isNotEmpty(spaceLevel), "spaceLevel", spaceLevel);

        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        SpaceVO spaceVO = SpaceVO.objToVo(space);
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
        Integer spaceType = space.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(space.getSpaceType());
        // Validate when creation
        if (isAdding) {
            ThrowUtils.throwIf(StrUtil.isBlank(spaceName), ErrorCode.PARAMS_ERROR, "Space name cannot be empty");
            ThrowUtils.throwIf(spaceLevel == null, ErrorCode.PARAMS_ERROR, "Space level cannot be empty");
            ThrowUtils.throwIf(spaceType==null, ErrorCode.PARAMS_ERROR, "Space type cannot be empty");
        }
        ThrowUtils.throwIf(StrUtil.isNotBlank(spaceName) && spaceName.length() > 30,
                ErrorCode.PARAMS_ERROR, "Space name is too long");
        ThrowUtils.throwIf(spaceLevel != null && spaceLevelEnum == null,
                ErrorCode.PARAMS_ERROR, "Space level does not exist");
        ThrowUtils.throwIf(spaceType!=null && spaceTypeEnum==null,
                ErrorCode.PARAMS_ERROR, "Space type does not exist");
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

    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        // 1. Fill in parameter values
        if (StrUtil.isBlank(spaceAddRequest.getSpaceName())) {
            spaceAddRequest.setSpaceName("Default space");
        }
        if (spaceAddRequest.getSpaceLevel() == null) {
            spaceAddRequest.setSpaceLevel(SpaceLevelEnum.PERSONAL.getValue());
        }
        if (spaceAddRequest.getSpaceType() == null) {
            spaceAddRequest.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }
        // Convert DTO to model object
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddRequest, space);
        this.fillSpaceBySpaceLevel(space);
        // 2. Validate parameters
        this.validSpace(space, true);
        // 3. Check authorisation, non-admin users can only personal-level space
        Long userId = loginUser.getId();
        space.setCreatorId(userId);
        boolean isCreatingNonPersonalSpace = SpaceLevelEnum.PERSONAL.getValue() != space.getSpaceLevel();
        if (isCreatingNonPersonalSpace && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "You are not allowed to create a space of this level");
        }

        // 4. Ensure that users can only create one space
        String lock = String.valueOf(userId).intern(); // intern() is to ensure that the lock string is the same object
        synchronized (lock) {
            Long newSpaceId = transactionTemplate.execute(status -> {
                // Check if there exists a space
                boolean exists = this.lambdaQuery()
                        .eq(Space::getCreatorId, userId)
                        .eq(Space::getSpaceType, spaceAddRequest.getSpaceType())
                        .exists();
                ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "Only one space can be created");
                boolean result = this.save(space);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "Failed to save space into database");
                // If the space is a team space, then record the space-user relation into database
                if (SpaceTypeEnum.TEAM.getValue().equals(spaceAddRequest.getSpaceType())) {
                    SpaceUser spaceUser = new SpaceUser();
                    spaceUser.setId(userId);
                    spaceUser.setSpaceId(space.getId());
                    spaceUser.setRole(SpaceRoleEnum.ADMIN.getValue());
                    result = spaceUserService.save(spaceUser);
                    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "Failed to save space-user relation");
                }

                return space.getId();
            });
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        }
    }


    /**
     * Helper function to check if the user is authorised to access the current space object
     */
    @Override
    public void checkIfOwnerOrAdmin(User loginUser, Space space) {
        boolean isCreator = space.getCreatorId().equals(loginUser.getId());
        boolean isAdmin = userService.isAdmin(loginUser);
        ThrowUtils.throwIf(!isCreator && !isAdmin, ErrorCode.NO_AUTH_ERROR);
    }
}




