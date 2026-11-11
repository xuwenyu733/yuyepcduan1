package cn.iocoder.yudao.module.fish.service.zkpaperquestion;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpaperquestion.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperquestion.ZkPaperQuestionDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkpaperquestion.ZkPaperQuestionConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpaperquestion.ZkPaperQuestionMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 试题 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkPaperQuestionServiceImpl implements ZkPaperQuestionService {

    @Resource
    private ZkPaperQuestionMapper zkPaperQuestionMapper;

    @Override
    public Long createZkPaperQuestion(ZkPaperQuestionCreateReqVO createReqVO) {
        // 插入
        ZkPaperQuestionDO zkPaperQuestion = BeanUtils.toBean(createReqVO, ZkPaperQuestionDO.class);
        zkPaperQuestionMapper.insert(zkPaperQuestion);
        // 返回
        return zkPaperQuestion.getId();
    }

    @Override
    public void updateZkPaperQuestion(ZkPaperQuestionUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkPaperQuestionExists(updateReqVO.getId());
        // 更新
        ZkPaperQuestionDO updateObj = BeanUtils.toBean(updateReqVO, ZkPaperQuestionDO.class);
        zkPaperQuestionMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkPaperQuestion(Long id) {
        // 校验存在
        validateZkPaperQuestionExists(id);
        // 删除
        zkPaperQuestionMapper.deleteById(id);
    }

    private void validateZkPaperQuestionExists(Long id) {
        if (zkPaperQuestionMapper.selectById(id) == null) {
            throw exception(ZK_PAPER_QUESTION_NOT_EXISTS);
        }
    }

    @Override
    public ZkPaperQuestionDO getZkPaperQuestion(Long id) {
        return zkPaperQuestionMapper.selectById(id);
    }

    @Override
    public List<ZkPaperQuestionDO> getZkPaperQuestionList(Collection<Long> ids) {
        return zkPaperQuestionMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkPaperQuestionDO> getZkPaperQuestionPage(ZkPaperQuestionPageReqVO pageReqVO) {
        return zkPaperQuestionMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkPaperQuestionDO> getZkPaperQuestionList(ZkPaperQuestionExportReqVO exportReqVO) {
        return zkPaperQuestionMapper.selectList(exportReqVO);
    }

    @Override
    public boolean customSaveBatch(List<ZkPaperQuestionDO> serPaperQuestions) {
        zkPaperQuestionMapper.insertBatch(serPaperQuestions);
        return true;
    }

    @Override
    public void removeByPaperId(Long paperId) {
        QueryWrapper<ZkPaperQuestionDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("paper_id", paperId);
        zkPaperQuestionMapper.delete(queryWrapper);
    }

    @Override
    public List<ZkPaperQuestionDO> listByIds(List<Long> questionIds) {
        QueryWrapper<ZkPaperQuestionDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", questionIds);

        return zkPaperQuestionMapper.selectList(queryWrapper);
    }

}
