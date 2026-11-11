package cn.iocoder.yudao.module.fish.dal.mysql.zkmeetingrecord;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetingrecord.ZkMeetingRecordDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkmeetingrecord.vo.*;

/**
 * 会议记录 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkMeetingRecordMapper extends BaseMapperX<ZkMeetingRecordDO> {

    default PageResult<ZkMeetingRecordDO> selectPage(ZkMeetingRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkMeetingRecordDO>()
                .eqIfPresent(ZkMeetingRecordDO::getMeetingId, reqVO.getMeetingId())
                .likeIfPresent(ZkMeetingRecordDO::getMeetingRecordName, reqVO.getMeetingRecordName())
                .eqIfPresent(ZkMeetingRecordDO::getMeetingRecordContent, reqVO.getMeetingRecordContent())
                .eqIfPresent(ZkMeetingRecordDO::getMeetingPoint, reqVO.getMeetingPoint())
                .eqIfPresent(ZkMeetingRecordDO::getMeetingSummary, reqVO.getMeetingSummary())
                .eqIfPresent(ZkMeetingRecordDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkMeetingRecordDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkMeetingRecordDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkMeetingRecordDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkMeetingRecordDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkMeetingRecordDO::getFid, reqVO.getFid())
                .orderByDesc(ZkMeetingRecordDO::getId));
    }

    default List<ZkMeetingRecordDO> selectList(ZkMeetingRecordExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkMeetingRecordDO>()
                .eqIfPresent(ZkMeetingRecordDO::getMeetingId, reqVO.getMeetingId())
                .likeIfPresent(ZkMeetingRecordDO::getMeetingRecordName, reqVO.getMeetingRecordName())
                .eqIfPresent(ZkMeetingRecordDO::getMeetingRecordContent, reqVO.getMeetingRecordContent())
                .eqIfPresent(ZkMeetingRecordDO::getMeetingPoint, reqVO.getMeetingPoint())
                .eqIfPresent(ZkMeetingRecordDO::getMeetingSummary, reqVO.getMeetingSummary())
                .eqIfPresent(ZkMeetingRecordDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkMeetingRecordDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkMeetingRecordDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkMeetingRecordDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkMeetingRecordDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkMeetingRecordDO::getFid, reqVO.getFid())
                .orderByDesc(ZkMeetingRecordDO::getId));
    }

}
