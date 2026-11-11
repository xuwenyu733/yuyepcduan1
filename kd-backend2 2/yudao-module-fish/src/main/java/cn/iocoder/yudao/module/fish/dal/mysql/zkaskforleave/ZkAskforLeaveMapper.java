package cn.iocoder.yudao.module.fish.dal.mysql.zkaskforleave;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkaskforleave.ZkAskforLeaveDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave.vo.*;

/**
 * 请假 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkAskforLeaveMapper extends BaseMapperX<ZkAskforLeaveDO> {

    default PageResult<ZkAskforLeaveDO> selectPage(ZkAskforLeavePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkAskforLeaveDO>()
                .likeIfPresent(ZkAskforLeaveDO::getLeaveName, reqVO.getLeaveName())
                .eqIfPresent(ZkAskforLeaveDO::getLeaveContent, reqVO.getLeaveContent())
                .eqIfPresent(ZkAskforLeaveDO::getLeaveDay, reqVO.getLeaveDay())
                .betweenIfPresent(ZkAskforLeaveDO::getStartTime, reqVO.getStartTime())
                .betweenIfPresent(ZkAskforLeaveDO::getEndTime, reqVO.getEndTime())
                .eqIfPresent(ZkAskforLeaveDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkAskforLeaveDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkAskforLeaveDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkAskforLeaveDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkAskforLeaveDO::getFid, reqVO.getFid())
                .orderByDesc(ZkAskforLeaveDO::getId));
    }

    default List<ZkAskforLeaveDO> selectList(ZkAskforLeaveExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkAskforLeaveDO>()
                .likeIfPresent(ZkAskforLeaveDO::getLeaveName, reqVO.getLeaveName())
                .eqIfPresent(ZkAskforLeaveDO::getLeaveContent, reqVO.getLeaveContent())
                .eqIfPresent(ZkAskforLeaveDO::getLeaveDay, reqVO.getLeaveDay())
                .betweenIfPresent(ZkAskforLeaveDO::getStartTime, reqVO.getStartTime())
                .betweenIfPresent(ZkAskforLeaveDO::getEndTime, reqVO.getEndTime())
                .eqIfPresent(ZkAskforLeaveDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkAskforLeaveDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkAskforLeaveDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkAskforLeaveDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkAskforLeaveDO::getFid, reqVO.getFid())
                .orderByDesc(ZkAskforLeaveDO::getId));
    }

}
