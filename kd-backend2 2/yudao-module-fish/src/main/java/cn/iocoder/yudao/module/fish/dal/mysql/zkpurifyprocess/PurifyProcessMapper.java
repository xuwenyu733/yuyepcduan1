package cn.iocoder.yudao.module.fish.dal.mysql.zkpurifyprocess;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyprocess.PurifyProcessDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyprocess.vo.*;

/**
 * 优化流程记录 Mapper
 *
 * @author admin2
 */
@Mapper
public interface PurifyProcessMapper extends BaseMapperX<PurifyProcessDO> {

    default PageResult<PurifyProcessDO> selectPage(PurifyProcessPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurifyProcessDO>()
                .eqIfPresent(PurifyProcessDO::getArchiveId, reqVO.getArchiveId())
                .eqIfPresent(PurifyProcessDO::getRecordNumber, reqVO.getRecordNumber())
                .eqIfPresent(PurifyProcessDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PurifyProcessDO::getStartTime, reqVO.getStartTime())
                .eqIfPresent(PurifyProcessDO::getOperator, reqVO.getOperator())
                .eqIfPresent(PurifyProcessDO::getTotalDeathCount, reqVO.getTotalDeathCount())
                .eqIfPresent(PurifyProcessDO::getTotalDeathWeight, reqVO.getTotalDeathWeight())
                .eqIfPresent(PurifyProcessDO::getTotalOutCount, reqVO.getTotalOutCount())
                .eqIfPresent(PurifyProcessDO::getTotalOutWeight, reqVO.getTotalOutWeight())
                .eqIfPresent(PurifyProcessDO::getCurrentStockCount, reqVO.getCurrentStockCount())
                .betweenIfPresent(PurifyProcessDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(PurifyProcessDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(PurifyProcessDO::getId));
    }

}