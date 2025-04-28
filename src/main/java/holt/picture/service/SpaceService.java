package holt.picture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import holt.picture.model.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import holt.picture.model.User;
import holt.picture.model.dto.space.SpaceAddRequest;
import holt.picture.model.dto.space.SpaceQueryRequest;
import holt.picture.model.vo.SpaceVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author Weiyang Wu
* @date 2025-04-28 12:55:05
*/
public interface SpaceService extends IService<Space> {
    QueryWrapper<Space> getSpaceQueryWrapper(SpaceQueryRequest spaceQueryRequest);
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);
    void validSpace(Space space, boolean isAdding);
    void fillSpaceBySpaceLevel(Space space);
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);
}
