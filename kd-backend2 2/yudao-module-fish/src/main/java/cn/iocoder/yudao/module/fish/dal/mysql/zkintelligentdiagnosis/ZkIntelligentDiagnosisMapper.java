package cn.iocoder.yudao.module.fish.dal.mysql.zkintelligentdiagnosis;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkintelligentdiagnosis.ZkIntelligentDiagnosisDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkintelligentdiagnosis.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 智能诊断 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkIntelligentDiagnosisMapper extends BaseMapperX<ZkIntelligentDiagnosisDO> {

    default PageResult<ZkIntelligentDiagnosisDO> selectPage(ZkIntelligentDiagnosisPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkIntelligentDiagnosisDO>()
                .eqIfPresent(ZkIntelligentDiagnosisDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkIntelligentDiagnosisDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkIntelligentDiagnosisDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkIntelligentDiagnosisDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getAiAgentReport, reqVO.getAiAgentReport())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getHumanReport, reqVO.getHumanReport())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getDifferentialAnalysis, reqVO.getDifferentialAnalysis())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getConclusion, reqVO.getConclusion())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getEvaluation, reqVO.getEvaluation())
                .betweenIfPresent(ZkIntelligentDiagnosisDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getAnalyst, reqVO.getAnalyst())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getExpert, reqVO.getExpert())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getUploadedImages, reqVO.getUploadedImages())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getStatus, reqVO.getStatus())
                .likeIfPresent(ZkIntelligentDiagnosisDO::getIllName, reqVO.getIllName())
                .orderByDesc(ZkIntelligentDiagnosisDO::getId));
    }

    default List<ZkIntelligentDiagnosisDO> selectList(ZkIntelligentDiagnosisExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkIntelligentDiagnosisDO>()
                .eqIfPresent(ZkIntelligentDiagnosisDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkIntelligentDiagnosisDO::getWorkshopName, reqVO.getWorkshopName())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkIntelligentDiagnosisDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getPondDetailNum, reqVO.getPondDetailNum())
                .likeIfPresent(ZkIntelligentDiagnosisDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getAiAgentReport, reqVO.getAiAgentReport())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getHumanReport, reqVO.getHumanReport())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getDifferentialAnalysis, reqVO.getDifferentialAnalysis())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getConclusion, reqVO.getConclusion())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getEvaluation, reqVO.getEvaluation())
                .betweenIfPresent(ZkIntelligentDiagnosisDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getAnalyst, reqVO.getAnalyst())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getExpert, reqVO.getExpert())
                .eqIfPresent(ZkIntelligentDiagnosisDO::getUploadedImages, reqVO.getUploadedImages())
                .orderByDesc(ZkIntelligentDiagnosisDO::getId));
    }

}
