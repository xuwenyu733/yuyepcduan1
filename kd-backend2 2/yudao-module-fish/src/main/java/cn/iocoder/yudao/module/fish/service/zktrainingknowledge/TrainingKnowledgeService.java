package cn.iocoder.yudao.module.fish.service.zktrainingknowledge;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zktrainingknowledge.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktrainingknowledge.TrainingKnowledgeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 培训知识 Service 接口
 *
 * @author 系统管理员
 */
public interface TrainingKnowledgeService {

    /**
     * 创建培训知识
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTrainingKnowledge(@Valid TrainingKnowledgeCreateReqVO createReqVO);

    /**
     * 更新培训知识
     *
     * @param updateReqVO 更新信息
     */
    void updateTrainingKnowledge(@Valid TrainingKnowledgeUpdateReqVO updateReqVO);

    /**
     * 删除培训知识
     *
     * @param id 编号
     */
    TrainingKnowledgeDO deleteTrainingKnowledge(Long id);

    /**
     * 获得培训知识
     *
     * @param id 编号
     * @return 培训知识
     */
    TrainingKnowledgeDO getTrainingKnowledge(Long id);

    /**
     * 获得培训知识列表
     *
     * @param ids 编号
     * @return 培训知识列表
     */
    List<TrainingKnowledgeDO> getTrainingKnowledgeList(Collection<Long> ids);

    /**
     * 获得培训知识分页
     *
     * @param pageReqVO 分页查询
     * @return 培训知识分页
     */
    PageResult<TrainingKnowledgeDO> getTrainingKnowledgePage(TrainingKnowledgePageReqVO pageReqVO);

    /**
     * 获得培训知识列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 培训知识列表
     */
    List<TrainingKnowledgeDO> getTrainingKnowledgeList(TrainingKnowledgeExportReqVO exportReqVO);

}
