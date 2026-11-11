package cn.iocoder.yudao.module.fish.dal.mysql.zktrainingknowledge;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktrainingknowledge.TrainingKnowledgeDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zktrainingknowledge.vo.*;

/**
 * 培训知识 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface TrainingKnowledgeMapper extends BaseMapperX<TrainingKnowledgeDO> {

    default PageResult<TrainingKnowledgeDO> selectPage(TrainingKnowledgePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TrainingKnowledgeDO>()
                .likeIfPresent(TrainingKnowledgeDO::getKnowledgeName, reqVO.getKnowledgeName())
                .eqIfPresent(TrainingKnowledgeDO::getKnowledgeContent, reqVO.getKnowledgeContent())
                .eqIfPresent(TrainingKnowledgeDO::getKnowledgeRemark, reqVO.getKnowledgeRemark())
                .eqIfPresent(TrainingKnowledgeDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(TrainingKnowledgeDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(TrainingKnowledgeDO::getUserId, reqVO.getUserId())
                .eqIfPresent(TrainingKnowledgeDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(TrainingKnowledgeDO::getFid, reqVO.getFid())
                .eqIfPresent(TrainingKnowledgeDO::getCreator, reqVO.getCreator())
                .orderByDesc(TrainingKnowledgeDO::getId));
    }

    default List<TrainingKnowledgeDO> selectList(TrainingKnowledgeExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<TrainingKnowledgeDO>()
                .likeIfPresent(TrainingKnowledgeDO::getKnowledgeName, reqVO.getKnowledgeName())
                .eqIfPresent(TrainingKnowledgeDO::getKnowledgeContent, reqVO.getKnowledgeContent())
                .eqIfPresent(TrainingKnowledgeDO::getKnowledgeRemark, reqVO.getKnowledgeRemark())
                .eqIfPresent(TrainingKnowledgeDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(TrainingKnowledgeDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(TrainingKnowledgeDO::getUserId, reqVO.getUserId())
                .eqIfPresent(TrainingKnowledgeDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(TrainingKnowledgeDO::getFid, reqVO.getFid())
                .orderByDesc(TrainingKnowledgeDO::getId));
    }

}
