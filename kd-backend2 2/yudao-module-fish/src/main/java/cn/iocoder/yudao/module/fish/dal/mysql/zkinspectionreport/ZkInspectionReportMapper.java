package cn.iocoder.yudao.module.fish.dal.mysql.zkinspectionreport;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkinspectionreport.ZkInspectionReportDO;
import cn.iocoder.yudao.module.fish.utils.QueryUtil;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkinspectionreport.vo.*;

/**
 * 巡检智能分析报告 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkInspectionReportMapper extends BaseMapperX<ZkInspectionReportDO> {

    default PageResult<ZkInspectionReportDO> selectPage(ZkInspectionReportPageReqVO reqVO) {
        LambdaQueryWrapperX<ZkInspectionReportDO> wrapper = new LambdaQueryWrapperX<ZkInspectionReportDO>()
                .eqIfPresent(ZkInspectionReportDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkInspectionReportDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkInspectionReportDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkInspectionReportDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkInspectionReportDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkInspectionReportDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkInspectionReportDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkInspectionReportDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkInspectionReportDO::getAiAgentReport, reqVO.getAiAgentReport())
                .eqIfPresent(ZkInspectionReportDO::getHumanReport, reqVO.getHumanReport())
                .eqIfPresent(ZkInspectionReportDO::getDifferentialAnalysis, reqVO.getDifferentialAnalysis())
                .eqIfPresent(ZkInspectionReportDO::getConclusion, reqVO.getConclusion())
                .eqIfPresent(ZkInspectionReportDO::getEvaluation, reqVO.getEvaluation())
                .betweenIfPresent(ZkInspectionReportDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkInspectionReportDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkInspectionReportDO::getFid, reqVO.getFid())
                .orderByDesc(ZkInspectionReportDO::getId);
        // 与 zk_pond 一致：deptIds IN（本部门+子部门），不用单独 eq deptId（避免与 IN 重复）
        QueryUtil.applyDeptFilter(wrapper, ZkInspectionReportDO::getDeptId, reqVO);
        return selectPage(reqVO, wrapper);
    }

    default List<ZkInspectionReportDO> selectList(ZkInspectionReportExportReqVO reqVO) {
        LambdaQueryWrapperX<ZkInspectionReportDO> wrapper = new LambdaQueryWrapperX<ZkInspectionReportDO>()
                .eqIfPresent(ZkInspectionReportDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkInspectionReportDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkInspectionReportDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkInspectionReportDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkInspectionReportDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkInspectionReportDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkInspectionReportDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkInspectionReportDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkInspectionReportDO::getAiAgentReport, reqVO.getAiAgentReport())
                .eqIfPresent(ZkInspectionReportDO::getHumanReport, reqVO.getHumanReport())
                .eqIfPresent(ZkInspectionReportDO::getDifferentialAnalysis, reqVO.getDifferentialAnalysis())
                .eqIfPresent(ZkInspectionReportDO::getConclusion, reqVO.getConclusion())
                .eqIfPresent(ZkInspectionReportDO::getEvaluation, reqVO.getEvaluation())
                .betweenIfPresent(ZkInspectionReportDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkInspectionReportDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkInspectionReportDO::getFid, reqVO.getFid())
                .orderByDesc(ZkInspectionReportDO::getId);
        QueryUtil.applyDeptFilter(wrapper, ZkInspectionReportDO::getDeptId, reqVO);
        return selectList(wrapper);
    }

}
