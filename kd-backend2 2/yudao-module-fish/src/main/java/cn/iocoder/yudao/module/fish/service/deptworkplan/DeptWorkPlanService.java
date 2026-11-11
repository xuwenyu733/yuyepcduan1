package cn.iocoder.yudao.module.fish.service.deptworkplan;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.deptworkplan.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.deptworkplan.DeptWorkPlanDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 部门工作计划 Service 接口
 *
 * @author 系统管理员
 */
public interface DeptWorkPlanService {

    /**
     * 创建部门工作计划
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDeptWorkPlan(@Valid DeptWorkPlanCreateReqVO createReqVO);

    /**
     * 更新部门工作计划
     *
     * @param updateReqVO 更新信息
     */
    void updateDeptWorkPlan(@Valid DeptWorkPlanUpdateReqVO updateReqVO);

    /**
     * 删除部门工作计划
     *
     * @param id 编号
     */
    void deleteDeptWorkPlan(Long id);

    /**
     * 获得部门工作计划
     *
     * @param id 编号
     * @return 部门工作计划
     */
    DeptWorkPlanDO getDeptWorkPlan(Long id);

    /**
     * 获得部门工作计划列表
     *
     * @param ids 编号
     * @return 部门工作计划列表
     */
    List<DeptWorkPlanDO> getDeptWorkPlanList(Collection<Long> ids);

    /**
     * 获得部门工作计划分页
     *
     * @param pageReqVO 分页查询
     * @return 部门工作计划分页
     */
    PageResult<DeptWorkPlanDO> getDeptWorkPlanPage(DeptWorkPlanPageReqVO pageReqVO);

    /**
     * 获得部门工作计划列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 部门工作计划列表
     */
    List<DeptWorkPlanDO> getDeptWorkPlanList(DeptWorkPlanExportReqVO exportReqVO);
}

