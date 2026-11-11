package cn.iocoder.yudao.module.fish.dal.mysql.zkmeeting;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeeting.ZkMeetingDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.vo.*;

/**
 * 会议 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkMeetingMapper extends BaseMapperX<ZkMeetingDO> {

    default PageResult<ZkMeetingDO> selectPage(ZkMeetingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkMeetingDO>()
                .likeIfPresent(ZkMeetingDO::getMeetingName, reqVO.getMeetingName())
                .eqIfPresent(ZkMeetingDO::getMeetingContent, reqVO.getMeetingContent())
                .eqIfPresent(ZkMeetingDO::getMeetingNum, reqVO.getMeetingNum())
                .betweenIfPresent(ZkMeetingDO::getStartTime, reqVO.getStartTime())
                .betweenIfPresent(ZkMeetingDO::getEndTime, reqVO.getEndTime())
                .eqIfPresent(ZkMeetingDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkMeetingDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkMeetingDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkMeetingDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkMeetingDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkMeetingDO::getFid, reqVO.getFid())
                .orderByDesc(ZkMeetingDO::getId));
    }

    default List<ZkMeetingDO> selectList(ZkMeetingExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkMeetingDO>()
                .likeIfPresent(ZkMeetingDO::getMeetingName, reqVO.getMeetingName())
                .eqIfPresent(ZkMeetingDO::getMeetingContent, reqVO.getMeetingContent())
                .eqIfPresent(ZkMeetingDO::getMeetingNum, reqVO.getMeetingNum())
                .betweenIfPresent(ZkMeetingDO::getStartTime, reqVO.getStartTime())
                .betweenIfPresent(ZkMeetingDO::getEndTime, reqVO.getEndTime())
                .eqIfPresent(ZkMeetingDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkMeetingDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkMeetingDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkMeetingDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkMeetingDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkMeetingDO::getFid, reqVO.getFid())
                .orderByDesc(ZkMeetingDO::getId));
    }

}
