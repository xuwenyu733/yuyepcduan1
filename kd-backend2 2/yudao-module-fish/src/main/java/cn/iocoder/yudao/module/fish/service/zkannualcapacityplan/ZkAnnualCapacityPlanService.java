package cn.iocoder.yudao.module.fish.service.zkannualcapacityplan;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkannualcapacityplan.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkannualcapacityplan.ZkAnnualCapacityPlanDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 年度产能计划 Service 接口
 *
 * @author admin2
 */
public interface ZkAnnualCapacityPlanService {

    /**
     * 创建年度产能计划
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkAnnualCapacityPlan(@Valid ZkAnnualCapacityPlanSaveReqVO createReqVO);

    /**
     * 更新年度产能计划
     *
     * @param updateReqVO 更新信息
     */
    void updateZkAnnualCapacityPlan(@Valid ZkAnnualCapacityPlanSaveReqVO updateReqVO);

    /**
     * 删除年度产能计划
     *
     * @param id 编号
     */
    void deleteZkAnnualCapacityPlan(Long id);

    /**
    * 批量删除年度产能计划
    *
    * @param ids 编号
    */
    void deleteZkAnnualCapacityPlanListByIds(List<Long> ids);

    /**
     * 获得年度产能计划
     *
     * @param id 编号
     * @return 年度产能计划
     */
    ZkAnnualCapacityPlanDO getZkAnnualCapacityPlan(Long id);

    /**
     * 获得年度产能计划分页
     *
     * @param pageReqVO 分页查询
     * @return 年度产能计划分页
     */
    PageResult<ZkAnnualCapacityPlanDO> getZkAnnualCapacityPlanPage(ZkAnnualCapacityPlanPageReqVO pageReqVO);

}