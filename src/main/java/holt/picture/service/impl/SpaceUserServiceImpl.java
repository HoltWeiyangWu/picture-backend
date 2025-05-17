package holt.picture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import holt.picture.exception.BusinessException;
import holt.picture.exception.ErrorCode;
import holt.picture.exception.ThrowUtils;
import holt.picture.model.Space;
import holt.picture.model.SpaceUser;
import holt.picture.model.User;
import holt.picture.model.dto.spaceuser.SpaceUserAddRequest;
import holt.picture.model.dto.spaceuser.SpaceUserQueryRequest;
import holt.picture.model.enums.SpaceRoleEnum;
import holt.picture.model.vo.SpaceUserVO;
import holt.picture.model.vo.SpaceVO;
import holt.picture.model.vo.UserVO;
import holt.picture.service.SpaceService;
import holt.picture.service.SpaceUserService;
import holt.picture.mapper.SpaceUserMapper;
import holt.picture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Weiyang Wu
* @date 2025-05-14 08:34:10
*/
@Service
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
    implements SpaceUserService{
    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Override
    public Long addSpaceUser(SpaceUserAddRequest request) {
        ThrowUtils.throwIf(request==null, ErrorCode.PARAMS_ERROR);
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        validateSpaceUser(spaceUser, true);
        boolean result = this.save(spaceUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return spaceUser.getId();
    }

    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest request) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if (request == null) {
            return queryWrapper;
        }
        Long id = request.getId();
        Long spaceId = request.getSpaceId();
        Long userId = request.getUserId();
        String role = request.getRole();
        queryWrapper.eq(ObjectUtil.isNotEmpty(id),"id", id);
        queryWrapper.eq(ObjectUtil.isNotEmpty(spaceId),"spaceId", spaceId);
        queryWrapper.eq(ObjectUtil.isNotEmpty(userId),"userId", userId);
        queryWrapper.eq(ObjectUtil.isNotEmpty(role),"role", role);
        return queryWrapper;
    }

    @Override
    public SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest httpServletRequest) {
        SpaceUserVO spaceUserVO = new SpaceUserVO();
        Long userId = spaceUser.getId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            BeanUtils.copyProperties(userVO, spaceUserVO);
        }
        Long spaceId = spaceUser.getSpaceId();
        if (spaceId != null && spaceId > 0) {
            Space space = spaceService.getById(spaceId);
            SpaceVO spaceVO = spaceService.getSpaceVO(space, httpServletRequest);
            BeanUtils.copyProperties(spaceVO, spaceUserVO);
        }
        return spaceUserVO;
    }

    @Override
    public List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList) {
        if (CollUtil.isEmpty(spaceUserList)) {
            return new ArrayList<>();
        }

        // 1. Convert each space-user relation object to view object
        List<SpaceUserVO> spaceUserVOList = spaceUserList.stream()
                .map(SpaceUserVO::objectToVo)
                .toList();

        // 2. Obtain a set of querying user ID and storage space ID
        Set<Long> userIdList = spaceUserList.stream()
                .map(SpaceUser::getUserId)
                .collect(Collectors.toSet());
        Set<Long> spaceIdList = spaceUserList.stream()
                .map(SpaceUser::getSpaceId)
                .collect(Collectors.toSet());

        // Mapping: ID to object for fast query
        Map<Long, List<User>> userIdToUserListMap = userService.listByIds(userIdList)
                .stream()
                .collect(Collectors.groupingBy(User::getId));
        Map<Long, List<Space>> spaceIdToSpaceListMap = spaceService.listByIds(spaceIdList)
                .stream()
                .collect(Collectors.groupingBy(Space::getId));

        // 3. Fill in the user and space info into vo list
        for (SpaceUserVO vo : spaceUserVOList) {
            Long userId = vo.getUserId();
            Long spaceId = vo.getSpaceId();

            User user = null;
            if (userIdToUserListMap.containsKey(userId)) {
                user = userIdToUserListMap.get(userId).get(0);
            }
            vo.setUserVO(userService.getUserVO(user));
            Space space = null;
            if (spaceIdToSpaceListMap.containsKey(spaceId)) {
                space = spaceIdToSpaceListMap.get(spaceId).get(0);
            }
            vo.setSpaceVO(SpaceVO.objToVo(space));
        }
        return spaceUserVOList;
    }

    @Override
    public void validateSpaceUser(SpaceUser spaceUser, boolean isAdding) {
        ThrowUtils.throwIf(spaceUser==null, ErrorCode.PARAMS_ERROR);
        Long spaceId = spaceUser.getSpaceId();
        Long userId = spaceUser.getUserId();
        if (isAdding) {
            ThrowUtils.throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCode.PARAMS_ERROR);
            User user = userService.getById(userId);
            ThrowUtils.throwIf(user==null, ErrorCode.NOT_FOUND_ERROR,"User not found");
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space==null, ErrorCode.NOT_FOUND_ERROR,"Space not found");
        }
        String role = spaceUser.getRole();
        SpaceRoleEnum spaceRoleEnum = SpaceRoleEnum.getEnum(role);
        if (role != null && spaceRoleEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "User role for this space does not exist");
        }
    }
}




