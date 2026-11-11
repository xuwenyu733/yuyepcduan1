package cn.iocoder.yudao.module.fish.dal.mysql.zkpurifycostdetail;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifycostdetail.PurifyCostDetailDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifycostdetail.vo.*;

/**
 * 成本明细记录 Mapper
 *
 * @author admin2
 */
@Mapper
public interface PurifyCostDetailMapper extends BaseMapperX<PurifyCostDetailDO> {

    default PageResult<PurifyCostDetailDO> selectPage(PurifyCostDetailPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurifyCostDetailDO>()
                .eqIfPresent(PurifyCostDetailDO::getProcessId, reqVO.getProcessId())
                .eqIfPresent(PurifyCostDetailDO::getCostType, reqVO.getCostType())
                .eqIfPresent(PurifyCostDetailDO::getAmount, reqVO.getAmount())
                .betweenIfPresent(PurifyCostDetailDO::getOccurrenceDate, reqVO.getOccurrenceDate())
                .eqIfPresent(PurifyCostDetailDO::getNotes, reqVO.getNotes())
                .betweenIfPresent(PurifyCostDetailDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(PurifyCostDetailDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(PurifyCostDetailDO::getId));
    }

}