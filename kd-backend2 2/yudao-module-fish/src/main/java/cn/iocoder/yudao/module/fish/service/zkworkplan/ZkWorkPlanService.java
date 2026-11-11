package cn.iocoder.yudao.module.fish.service.zkworkplan;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkworkplan.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkworkplan.ZkWorkPlanDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 工作目标与计划 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkWorkPlanService {

    /**
     * 创建工作目标与计划
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkWorkPlan(@Valid ZkWorkPlanCreateReqVO createReqVO);

    /**
     * 更新工作目标与计划
     *
     * @param updateReqVO 更新信息
     */
    void updateZkWorkPlan(@Valid ZkWorkPlanUpdateReqVO updateReqVO);

    /**
     * 删除工作目标与计划
     *
     * @param id 编号
     */
    void deleteZkWorkPlan(Long id);

    /**
     * 获得工作目标与计划
     *
     * @param id 编号
     * @return 工作目标与计划
     */
    ZkWorkPlanDO getZkWorkPlan(Long id);

    /**
     * 获得工作目标与计划列表
     *
     * @param ids 编号
     * @return 工作目标与计划列表
     */
    List<ZkWorkPlanDO> getZkWorkPlanList(Collection<Long> ids);

    /**
     * 获得工作目标与计划分页
     *
     * @param pageReqVO 分页查询
     * @return 工作目标与计划分页
     */
    PageResult<ZkWorkPlanDO> getZkWorkPlanPage(ZkWorkPlanPageReqVO pageReqVO);

    /**
     * 获得工作目标与计划列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 工作目标与计划列表
     */
    List<ZkWorkPlanDO> getZkWorkPlanList(ZkWorkPlanExportReqVO exportReqVO);

}
