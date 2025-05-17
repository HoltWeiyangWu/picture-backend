package holt.picture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import holt.picture.model.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import holt.picture.model.dto.spaceuser.SpaceUserAddRequest;
import holt.picture.model.dto.spaceuser.SpaceUserQueryRequest;
import holt.picture.model.vo.SpaceUserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author Weiyang Wu
* @date 2025-05-14 08:34:10
*/
public interface SpaceUserService extends IService<SpaceUser> {
    Long addSpaceUser(SpaceUserAddRequest request);
    void validateSpaceUser(SpaceUser spaceUser, boolean isAdding);
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest request);
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest httpServletRequest);
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);
}
