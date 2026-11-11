package cn.iocoder.yudao.module.fish.dal.mysql.zkmeetinguser;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetinguser.ZkMeetingUserDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkmeetinguser.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 会议用户 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkMeetingUserMapper extends BaseMapperX<ZkMeetingUserDO> {

    default PageResult<ZkMeetingUserDO> selectPage(ZkMeetingUserPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkMeetingUserDO>()
                .eqIfPresent(ZkMeetingUserDO::getMeetingId, reqVO.getMeetingId())
                .eqIfPresent(ZkMeetingUserDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkMeetingUserDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkMeetingUserDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkMeetingUserDO::getUserType, reqVO.getUserType())
                .betweenIfPresent(ZkMeetingUserDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkMeetingUserDO::getFid, reqVO.getFid())
                .orderByDesc(ZkMeetingUserDO::getId));
    }

    default List<ZkMeetingUserDO> selectList(ZkMeetingUserExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkMeetingUserDO>()
                .eqIfPresent(ZkMeetingUserDO::getMeetingId, reqVO.getMeetingId())
                .eqIfPresent(ZkMeetingUserDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkMeetingUserDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkMeetingUserDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkMeetingUserDO::getUserType, reqVO.getUserType())
                .betweenIfPresent(ZkMeetingUserDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkMeetingUserDO::getFid, reqVO.getFid())
                .orderByDesc(ZkMeetingUserDO::getId));
    }

    @Select({
            "SELECT id AS userId, nickname AS userName, dept_id as deptId FROM system_users ${ew.customSqlSegment}"
    })
    List<ZkMeetingUserDO> selectByUids(@Param("ew") QueryWrapper<ZkMeetingUserDO> queryWrapper);
}
