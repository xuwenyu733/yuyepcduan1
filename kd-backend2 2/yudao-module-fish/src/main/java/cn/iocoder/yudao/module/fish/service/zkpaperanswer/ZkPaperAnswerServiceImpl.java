package cn.iocoder.yudao.module.fish.service.zkpaperanswer;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpaperanswer.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperanswer.ZkPaperAnswerDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkpaperanswer.ZkPaperAnswerConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpaperanswer.ZkPaperAnswerMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 答题记录 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkPaperAnswerServiceImpl implements ZkPaperAnswerService {

    @Resource
    private ZkPaperAnswerMapper zkPaperAnswerMapper;

    @Override
    public Long createZkPaperAnswer(ZkPaperAnswerCreateReqVO createReqVO) {
        // 插入
        ZkPaperAnswerDO zkPaperAnswer = BeanUtils.toBean(createReqVO, ZkPaperAnswerDO.class);
        zkPaperAnswerMapper.insert(zkPaperAnswer);
        // 返回
        return zkPaperAnswer.getId();
    }

    @Override
    public void updateZkPaperAnswer(ZkPaperAnswerUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkPaperAnswerExists(updateReqVO.getId());
        // 更新
        ZkPaperAnswerDO updateObj = BeanUtils.toBean(updateReqVO, ZkPaperAnswerDO.class);
        zkPaperAnswerMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkPaperAnswer(Long id) {
        // 校验存在
        validateZkPaperAnswerExists(id);
        // 删除
        zkPaperAnswerMapper.deleteById(id);
    }

    private void validateZkPaperAnswerExists(Long id) {
        if (zkPaperAnswerMapper.selectById(id) == null) {
            throw exception(ZK_PAPER_ANSWER_NOT_EXISTS);
        }
    }

    @Override
    public ZkPaperAnswerDO getZkPaperAnswer(Long id) {
        return zkPaperAnswerMapper.selectById(id);
    }

    @Override
    public List<ZkPaperAnswerDO> getZkPaperAnswerList(Collection<Long> ids) {
        return zkPaperAnswerMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkPaperAnswerDO> getZkPaperAnswerPage(ZkPaperAnswerPageReqVO pageReqVO) {
        return zkPaperAnswerMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkPaperAnswerDO> getZkPaperAnswerList(ZkPaperAnswerExportReqVO exportReqVO) {
        return zkPaperAnswerMapper.selectList(exportReqVO);
    }

}
