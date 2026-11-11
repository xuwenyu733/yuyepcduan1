package cn.iocoder.yudao.module.fish.service.oaworkplan;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.oaworkplan.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.oaworkplan.OaWorkPlanDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * OA工作计划 Service 接口
 *
 * @author 系统管理员
 */
public interface OaWorkPlanService {

  /**
   * 创建OA工作计划
   *
   * @param createReqVO 创建信息
   * @return 编号
   */
  Long createOaWorkPlan(@Valid OaWorkPlanCreateReqVO createReqVO);

  /**
   * 更新OA工作计划
   *
   * @param updateReqVO 更新信息
   */
  void updateOaWorkPlan(@Valid OaWorkPlanUpdateReqVO updateReqVO);

  /**
   * 删除OA工作计划
   *
   * @param id 编号
   */
  void deleteOaWorkPlan(Long id);

  /**
   * 获得OA工作计划
   *
   * @param id 编号
   * @return OA工作计划
   */
  OaWorkPlanDO getOaWorkPlan(Long id);

  /**
   * 获得OA工作计划列表
   *
   * @param ids 编号
   * @return OA工作计划列表
   */
  List<OaWorkPlanDO> getOaWorkPlanList(Collection<Long> ids);

  /**
   * 获得OA工作计划分页（带权限控制）
   *
   * @param pageReqVO 分页查询
   * @return OA工作计划分页
   */
  PageResult<OaWorkPlanDO> getOaWorkPlanPage(OaWorkPlanPageReqVO pageReqVO);

  /**
   * 获得OA工作计划列表, 用于 Excel 导出
   *
   * @param exportReqVO 查询条件
   * @return OA工作计划列表
   */
  List<OaWorkPlanDO> getOaWorkPlanList(OaWorkPlanExportReqVO exportReqVO);

  /**
   * 审核OA工作计划
   *
   * @param reviewReqVO 审核信息
   */
  void reviewOaWorkPlan(@Valid OaWorkPlanReviewReqVO reviewReqVO);

  /**
   * 更新OA工作计划完成情况
   *
   * @param completeReqVO 完成情况请求
   */
  void completeOaWorkPlan(OaWorkPlanCompleteReqVO completeReqVO);

  /**
   * 评价OA工作计划
   *
   * @param evaluationReqVO 评价请求
   */
  void evaluateOaWorkPlan(OaWorkPlanEvaluationReqVO evaluationReqVO);

  /**
   * 获得OA工作计划统计
   *
   * @return 统计数据
   */
  OaWorkPlanStatisticsVO getOaWorkPlanStatistics();

}
