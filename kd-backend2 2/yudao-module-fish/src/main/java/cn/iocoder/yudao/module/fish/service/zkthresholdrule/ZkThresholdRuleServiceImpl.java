package cn.iocoder.yudao.module.fish.service.zkthresholdrule;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkthresholdrule.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkthresholdrule.ZkThresholdRuleDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkthresholdrule.ZkThresholdRuleConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkthresholdrule.ZkThresholdRuleMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 阈值规则 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkThresholdRuleServiceImpl implements ZkThresholdRuleService {

    @Resource
    private ZkThresholdRuleMapper zkThresholdRuleMapper;

    @Override
    public Long createZkThresholdRule(ZkThresholdRuleCreateReqVO createReqVO) {
        // 插入
        ZkThresholdRuleDO zkThresholdRule = BeanUtils.toBean(createReqVO, ZkThresholdRuleDO.class);
        zkThresholdRuleMapper.insert(zkThresholdRule);
        // 返回
        return zkThresholdRule.getId();
    }

    @Override
    public void updateZkThresholdRule(ZkThresholdRuleUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkThresholdRuleExists(updateReqVO.getId());
        // 更新
        ZkThresholdRuleDO updateObj = BeanUtils.toBean(updateReqVO, ZkThresholdRuleDO.class);
        zkThresholdRuleMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkThresholdRule(Long id) {
        // 校验存在
        validateZkThresholdRuleExists(id);
        // 删除
        zkThresholdRuleMapper.deleteById(id);
    }

    private void validateZkThresholdRuleExists(Long id) {
        if (zkThresholdRuleMapper.selectById(id) == null) {
            throw exception(ZK_THRESHOLD_RULE_NOT_EXISTS);
        }
    }

    @Override
    public ZkThresholdRuleDO getZkThresholdRule(Long id) {
        return zkThresholdRuleMapper.selectById(id);
    }

    @Override
    public List<ZkThresholdRuleDO> getZkThresholdRuleList(Collection<Long> ids) {
        return zkThresholdRuleMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkThresholdRuleDO> getZkThresholdRulePage(ZkThresholdRulePageReqVO pageReqVO) {
        return zkThresholdRuleMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkThresholdRuleDO> getZkThresholdRuleList(ZkThresholdRuleExportReqVO exportReqVO) {
        return zkThresholdRuleMapper.selectList(exportReqVO);
    }

}
