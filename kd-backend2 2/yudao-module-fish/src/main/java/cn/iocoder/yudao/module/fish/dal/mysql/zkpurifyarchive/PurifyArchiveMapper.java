package cn.iocoder.yudao.module.fish.dal.mysql.zkpurifyarchive;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyarchive.PurifyArchiveDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyarchive.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 入池档案 Mapper
 *
 * @author admin2
 */
@Mapper
public interface PurifyArchiveMapper extends BaseMapperX<PurifyArchiveDO> {

    default PageResult<PurifyArchiveDO> selectPage(PurifyArchivePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurifyArchiveDO>()
                .eqIfPresent(PurifyArchiveDO::getPurchaseRecordId, reqVO.getPurchaseRecordId())
                .eqIfPresent(PurifyArchiveDO::getPurchaseOrderNo, reqVO.getPurchaseOrderNo())
                .eqIfPresent(PurifyArchiveDO::getCategory, reqVO.getCategory())
                .eqIfPresent(PurifyArchiveDO::getSource, reqVO.getSource())
                .betweenIfPresent(PurifyArchiveDO::getInPoolTime, reqVO.getInPoolTime())
                .eqIfPresent(PurifyArchiveDO::getInPoolWeight, reqVO.getInPoolWeight())
                .eqIfPresent(PurifyArchiveDO::getAvgWeight, reqVO.getAvgWeight())
                .eqIfPresent(PurifyArchiveDO::getInPoolCount, reqVO.getInPoolCount())
                .eqIfPresent(PurifyArchiveDO::getPoolNumber, reqVO.getPoolNumber())
                .eqIfPresent(PurifyArchiveDO::getDensity, reqVO.getDensity())
                .eqIfPresent(PurifyArchiveDO::getOptimizationLevel, reqVO.getOptimizationLevel())
                .eqIfPresent(PurifyArchiveDO::getInspectionReport, reqVO.getInspectionReport())
                .eqIfPresent(PurifyArchiveDO::getInitialOxygen, reqVO.getInitialOxygen())
                .eqIfPresent(PurifyArchiveDO::getInitialPh, reqVO.getInitialPh())
                .eqIfPresent(PurifyArchiveDO::getInitialTemperature, reqVO.getInitialTemperature())
                .eqIfPresent(PurifyArchiveDO::getAppearanceScore, reqVO.getAppearanceScore())
                .eqIfPresent(PurifyArchiveDO::getVitalityScore, reqVO.getVitalityScore())
                .eqIfPresent(PurifyArchiveDO::getHealthStatus, reqVO.getHealthStatus())
                .eqIfPresent(PurifyArchiveDO::getExpectedSurvivalRate, reqVO.getExpectedSurvivalRate())
                .eqIfPresent(PurifyArchiveDO::getRiskLevel, reqVO.getRiskLevel())
                .eqIfPresent(PurifyArchiveDO::getAssessor, reqVO.getAssessor())
                .betweenIfPresent(PurifyArchiveDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(PurifyArchiveDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(PurifyArchiveDO::getId));
    }

    @Select({
            "select zpa.* from zk_purify_archive zpa " +
                    "left join zk_purify_process zpp on zpp.archive_id = zpa.id " +
                    "${ew.customSqlSegment}"
    })
    IPage<PurifyArchiveDO> customPage(Page<PurifyArchiveDO> page, @Param("ew") QueryWrapper<PurifyArchiveDO> queryWrapper);

}