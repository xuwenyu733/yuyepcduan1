package cn.iocoder.yudao.module.fish.service.zktrainingknowledge;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zktrainingknowledge.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktrainingknowledge.TrainingKnowledgeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zktrainingknowledge.TrainingKnowledgeConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zktrainingknowledge.TrainingKnowledgeMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 培训知识 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class TrainingKnowledgeServiceImpl implements TrainingKnowledgeService {

    @Resource
    private TrainingKnowledgeMapper trainingKnowledgeMapper;

    @Override
    public Long createTrainingKnowledge(TrainingKnowledgeCreateReqVO createReqVO) {
        // 插入
        TrainingKnowledgeDO trainingKnowledge = BeanUtils.toBean(createReqVO, TrainingKnowledgeDO.class);
        trainingKnowledgeMapper.insert(trainingKnowledge);
        // 返回
        return trainingKnowledge.getId();
    }

    @Override
    public void updateTrainingKnowledge(TrainingKnowledgeUpdateReqVO updateReqVO) {
        // 校验存在
        validateTrainingKnowledgeExists(updateReqVO.getId());
        // 更新
        TrainingKnowledgeDO updateObj = BeanUtils.toBean(updateReqVO, TrainingKnowledgeDO.class);
        trainingKnowledgeMapper.updateById(updateObj);
    }

    @Override
    public TrainingKnowledgeDO deleteTrainingKnowledge(Long id) {
        // 校验存在
        TrainingKnowledgeDO trainingKnowledgeDO = validateTrainingKnowledgeExists(id);
        // 删除
        trainingKnowledgeMapper.deleteById(id);
        return trainingKnowledgeDO;
    }

    private TrainingKnowledgeDO validateTrainingKnowledgeExists(Long id) {
        TrainingKnowledgeDO trainingKnowledgeDO = trainingKnowledgeMapper.selectById(id);
        if (trainingKnowledgeDO == null) {
            throw exception(TRAINING_KNOWLEDGE_NOT_EXISTS);
        }
        return trainingKnowledgeDO;
    }

    @Override
    public TrainingKnowledgeDO getTrainingKnowledge(Long id) {
        return trainingKnowledgeMapper.selectById(id);
    }

    @Override
    public List<TrainingKnowledgeDO> getTrainingKnowledgeList(Collection<Long> ids) {
        return trainingKnowledgeMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<TrainingKnowledgeDO> getTrainingKnowledgePage(TrainingKnowledgePageReqVO pageReqVO) {
        return trainingKnowledgeMapper.selectPage(pageReqVO);
    }

    @Override
    public List<TrainingKnowledgeDO> getTrainingKnowledgeList(TrainingKnowledgeExportReqVO exportReqVO) {
        return trainingKnowledgeMapper.selectList(exportReqVO);
    }

}
