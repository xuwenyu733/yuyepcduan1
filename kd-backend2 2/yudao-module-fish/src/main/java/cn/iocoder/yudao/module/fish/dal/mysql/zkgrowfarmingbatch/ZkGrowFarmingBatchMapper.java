package cn.iocoder.yudao.module.fish.dal.mysql.zkgrowfarmingbatch;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfarmingbatch.ZkGrowFarmingBatchDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowfarmingbatch.vo.*;

/**
 * 养殖批次 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkGrowFarmingBatchMapper extends BaseMapperX<ZkGrowFarmingBatchDO> {

    default PageResult<ZkGrowFarmingBatchDO> selectPage(ZkGrowFarmingBatchPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkGrowFarmingBatchDO>()
                .eqIfPresent(ZkGrowFarmingBatchDO::getBatchNo, reqVO.getBatchNo())
                .eqIfPresent(ZkGrowFarmingBatchDO::getSpeciesId, reqVO.getSpeciesId())
                .betweenIfPresent(ZkGrowFarmingBatchDO::getStartDate, reqVO.getStartDate())
                .eqIfPresent(ZkGrowFarmingBatchDO::getInitialCount, reqVO.getInitialCount())
                .eqIfPresent(ZkGrowFarmingBatchDO::getCurrentCount, reqVO.getCurrentCount())
                .eqIfPresent(ZkGrowFarmingBatchDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkGrowFarmingBatchDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkGrowFarmingBatchDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkGrowFarmingBatchDO::getId));
    }

}