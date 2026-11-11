package cn.iocoder.yudao.module.fish.service.zkdeathsurvivalrate;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate.vo.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate.vo.stats.ZkDeathSurvivalRateStatsRespVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeathsurvivalrate.ZkDeathSurvivalRateDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 死亡和存活率 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkDeathSurvivalRateService {

    /**
     * 创建死亡和存活率
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkDeathSurvivalRate(@Valid ZkDeathSurvivalRateCreateReqVO createReqVO);

    /**
     * 更新死亡和存活率
     *
     * @param updateReqVO 更新信息
     */
    void updateZkDeathSurvivalRate(@Valid ZkDeathSurvivalRateUpdateReqVO updateReqVO);

    /**
     * 删除死亡和存活率
     *
     * @param id 编号
     */
    void deleteZkDeathSurvivalRate(Long id);

    /**
     * 获得死亡和存活率
     *
     * @param id 编号
     * @return 死亡和存活率
     */
    ZkDeathSurvivalRateDO getZkDeathSurvivalRate(Long id);

    /**
     * 获得死亡和存活率列表
     *
     * @param ids 编号
     * @return 死亡和存活率列表
     */
    List<ZkDeathSurvivalRateDO> getZkDeathSurvivalRateList(Collection<Long> ids);

    /**
     * 获得死亡和存活率分页
     *
     * @param pageReqVO 分页查询
     * @return 死亡和存活率分页
     */
    PageResult<ZkDeathSurvivalRateDO> getZkDeathSurvivalRatePage(ZkDeathSurvivalRatePageReqVO pageReqVO);

    /**
     * 获得死亡和存活率列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 死亡和存活率列表
     */
    List<ZkDeathSurvivalRateDO> getZkDeathSurvivalRateList(ZkDeathSurvivalRateExportReqVO exportReqVO);

    /**
     * 获取死亡量死亡率统计数据
     * @param deptId 归属部门（可选，不传则取登录用户部门）
     * @return 统计数据
     */
    ZkDeathSurvivalRateStatsRespVO getStatistics(Long deptId);

}
