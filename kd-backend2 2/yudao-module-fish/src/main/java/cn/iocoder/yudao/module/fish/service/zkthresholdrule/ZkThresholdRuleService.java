package cn.iocoder.yudao.module.fish.service.zkthresholdrule;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkthresholdrule.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkthresholdrule.ZkThresholdRuleDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 阈值规则 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkThresholdRuleService {

    /**
     * 创建阈值规则
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkThresholdRule(@Valid ZkThresholdRuleCreateReqVO createReqVO);

    /**
     * 更新阈值规则
     *
     * @param updateReqVO 更新信息
     */
    void updateZkThresholdRule(@Valid ZkThresholdRuleUpdateReqVO updateReqVO);

    /**
     * 删除阈值规则
     *
     * @param id 编号
     */
    void deleteZkThresholdRule(Long id);

    /**
     * 获得阈值规则
     *
     * @param id 编号
     * @return 阈值规则
     */
    ZkThresholdRuleDO getZkThresholdRule(Long id);

    /**
     * 获得阈值规则列表
     *
     * @param ids 编号
     * @return 阈值规则列表
     */
    List<ZkThresholdRuleDO> getZkThresholdRuleList(Collection<Long> ids);

    /**
     * 获得阈值规则分页
     *
     * @param pageReqVO 分页查询
     * @return 阈值规则分页
     */
    PageResult<ZkThresholdRuleDO> getZkThresholdRulePage(ZkThresholdRulePageReqVO pageReqVO);

    /**
     * 获得阈值规则列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 阈值规则列表
     */
    List<ZkThresholdRuleDO> getZkThresholdRuleList(ZkThresholdRuleExportReqVO exportReqVO);

}
