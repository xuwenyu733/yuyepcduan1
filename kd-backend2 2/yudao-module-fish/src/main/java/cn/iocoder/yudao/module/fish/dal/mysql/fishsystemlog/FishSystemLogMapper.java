package cn.iocoder.yudao.module.fish.dal.mysql.fishsystemlog;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo.FishSystemLogExportReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo.FishSystemLogPageReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.fishsystemlog.FishSystemLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * fish系统日志 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface FishSystemLogMapper extends BaseMapperX<FishSystemLogDO> {

    default PageResult<FishSystemLogDO> selectPage(FishSystemLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FishSystemLogDO>()
                .eqIfPresent(FishSystemLogDO::getPondDetailNum, reqVO.getPondDetailNum())
                .eqIfPresent(FishSystemLogDO::getLogTitle, reqVO.getLogTitle())
                .eqIfPresent(FishSystemLogDO::getLogType, reqVO.getLogType())
                .eqIfPresent(FishSystemLogDO::getRemark, reqVO.getRemark())
                .eqIfPresent(FishSystemLogDO::getOriginData, reqVO.getOriginData())
                .eqIfPresent(FishSystemLogDO::getTargetData, reqVO.getTargetData())
                .eqIfPresent(FishSystemLogDO::getOriginRes, reqVO.getOriginRes())
                .eqIfPresent(FishSystemLogDO::getTargetRes, reqVO.getTargetRes())
                .betweenIfPresent(FishSystemLogDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(FishSystemLogDO::getCreator, reqVO.getUserId())
                .eqIfPresent(FishSystemLogDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(FishSystemLogDO::getFid, reqVO.getFid())
                .orderByDesc(FishSystemLogDO::getId));
    }

    default List<FishSystemLogDO> selectList(FishSystemLogExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<FishSystemLogDO>()
                .eqIfPresent(FishSystemLogDO::getPondDetailNum, reqVO.getPondDetailNum())
                .eqIfPresent(FishSystemLogDO::getLogTitle, reqVO.getLogTitle())
                .eqIfPresent(FishSystemLogDO::getLogType, reqVO.getLogType())
                .eqIfPresent(FishSystemLogDO::getRemark, reqVO.getRemark())
                .eqIfPresent(FishSystemLogDO::getOriginData, reqVO.getOriginData())
                .eqIfPresent(FishSystemLogDO::getTargetData, reqVO.getTargetData())
                .eqIfPresent(FishSystemLogDO::getOriginRes, reqVO.getOriginRes())
                .eqIfPresent(FishSystemLogDO::getTargetRes, reqVO.getTargetRes())
                .betweenIfPresent(FishSystemLogDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(FishSystemLogDO::getUserId, reqVO.getUserId())
                .eqIfPresent(FishSystemLogDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(FishSystemLogDO::getFid, reqVO.getFid())
                .orderByDesc(FishSystemLogDO::getId));
    }

}
