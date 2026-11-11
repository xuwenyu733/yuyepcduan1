package cn.iocoder.yudao.module.fish.service.oaworkplan;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.fish.controller.admin.oaworkplan.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.oaworkplan.OaWorkPlanDO;
import cn.iocoder.yudao.module.fish.dal.mysql.oaworkplan.OaWorkPlanMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.OA_WORK_PLAN_NOT_EXISTS;

/**
 * OA工作计划 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class OaWorkPlanServiceImpl implements OaWorkPlanService {

  @Resource
  private OaWorkPlanMapper oaWorkPlanMapper;

  @Resource
  private AdminUserApi adminUserApi;

  @Resource
  private DeptApi deptApi;

  @Override
  public Long createOaWorkPlan(OaWorkPlanCreateReqVO createReqVO) {
    Long userId = SecurityFrameworkUtils.getLoginUserId();
    String userName = SecurityFrameworkUtils.getLoginUserNickname();
    Long deptId = SecurityFrameworkUtils.getLoginUserDeptId();

    // 获取部门名称
    String deptName = null;
    if (deptId != null) {
      try {
        DeptRespDTO dept = deptApi.getDept(deptId);
        if (dept != null) {
          deptName = dept.getName();
        }
      } catch (Exception e) {
        log.warn("获取部门信息失败: {}", e.getMessage());
      }
    }

    // 插入
    OaWorkPlanDO oaWorkPlan = BeanUtils.toBean(createReqVO, OaWorkPlanDO.class);
    oaWorkPlan.setUserId(userId);
    oaWorkPlan.setUserName(userName != null ? userName : "未知用户");
    oaWorkPlan.setDeptId(deptId);
    oaWorkPlan.setDeptName(deptName);

    // 如果状态是 pending（提交审核），设置提交日期
    if ("pending".equals(createReqVO.getStatus())) {
      oaWorkPlan.setSubmitDate(LocalDateTime.now());
    }

    oaWorkPlanMapper.insert(oaWorkPlan);
    // 返回
    return oaWorkPlan.getId();
  }

  @Override
  public void updateOaWorkPlan(OaWorkPlanUpdateReqVO updateReqVO) {
    // 校验存在
    OaWorkPlanDO existingPlan = validateOaWorkPlanExists(updateReqVO.getId());

    // 校验权限：只有创建者可以修改
    Long userId = SecurityFrameworkUtils.getLoginUserId();
    if (!existingPlan.getUserId().equals(userId)) {
      throw exception(OA_WORK_PLAN_NOT_EXISTS); // 使用不存在错误，避免暴露权限信息
    }

    // 更新
    OaWorkPlanDO updateObj = BeanUtils.toBean(updateReqVO, OaWorkPlanDO.class);

    // 如果状态从草稿变为待审核，设置提交日期
    if ("draft".equals(existingPlan.getStatus()) && "pending".equals(updateReqVO.getStatus())) {
      updateObj.setSubmitDate(LocalDateTime.now());
    }

    oaWorkPlanMapper.updateById(updateObj);
  }

  @Override
  public void deleteOaWorkPlan(Long id) {
    // 校验存在
    OaWorkPlanDO existingPlan = validateOaWorkPlanExists(id);

    // 校验权限：只有创建者可以删除
    Long userId = SecurityFrameworkUtils.getLoginUserId();
    if (!existingPlan.getUserId().equals(userId)) {
      throw exception(OA_WORK_PLAN_NOT_EXISTS);
    }

    // 删除
    oaWorkPlanMapper.deleteById(id);
  }

  private OaWorkPlanDO validateOaWorkPlanExists(Long id) {
    OaWorkPlanDO plan = oaWorkPlanMapper.selectById(id);
    if (plan == null) {
      throw exception(OA_WORK_PLAN_NOT_EXISTS);
    }
    return plan;
  }

  @Override
  public OaWorkPlanDO getOaWorkPlan(Long id) {
    return oaWorkPlanMapper.selectById(id);
  }

  @Override
  public List<OaWorkPlanDO> getOaWorkPlanList(Collection<Long> ids) {
    return oaWorkPlanMapper.selectBatchIds(ids);
  }

  @Override
  public PageResult<OaWorkPlanDO> getOaWorkPlanPage(OaWorkPlanPageReqVO pageReqVO) {
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    // 判断用户权限
    boolean isSuperAdmin = isSuperAdmin(currentUserId);
    boolean isDeptManager = isDeptManager(currentUserId, currentDeptId);

    // 权限控制：如果不是超管或部门管理员，只能查看自己的数据
    if (!isSuperAdmin && !isDeptManager) {
      pageReqVO.setUserId(currentUserId);
    } else if (isDeptManager && !isSuperAdmin) {
      // 部门管理员：查看本部门所有人的数据
      pageReqVO.setDeptId(currentDeptId);
    }
    // 超管：不设置过滤条件，查看所有数据。
    // 前端可能仍携带 userId/deptId 查询参数，会误与「全量统计」口径不一致，此处强制清空
    if (isSuperAdmin) {
      pageReqVO.setUserId(null);
      pageReqVO.setDeptId(null);
    }

    return oaWorkPlanMapper.selectPage(pageReqVO);
  }

  @Override
  public List<OaWorkPlanDO> getOaWorkPlanList(OaWorkPlanExportReqVO exportReqVO) {
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    // 判断用户权限
    boolean isSuperAdmin = isSuperAdmin(currentUserId);
    boolean isDeptManager = isDeptManager(currentUserId, currentDeptId);

    // 权限控制
    if (!isSuperAdmin && !isDeptManager) {
      exportReqVO.setUserId(currentUserId);
    } else if (isDeptManager && !isSuperAdmin) {
      exportReqVO.setDeptId(currentDeptId);
    }
    if (isSuperAdmin) {
      exportReqVO.setUserId(null);
      exportReqVO.setDeptId(null);
    }

    return oaWorkPlanMapper.selectList(exportReqVO);
  }

  @Override
  public void reviewOaWorkPlan(OaWorkPlanReviewReqVO reviewReqVO) {
    // 校验存在
    OaWorkPlanDO plan = validateOaWorkPlanExists(reviewReqVO.getId());

    // 校验权限：只有上级可以审核
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    // 不能审核自己的计划
    if (plan.getUserId() != null && plan.getUserId().equals(currentUserId)) {
      throw new RuntimeException("不能审核自己的计划");
    }

    boolean isSuperAdmin = isSuperAdmin(currentUserId);
    boolean isDeptManager = isDeptManager(currentUserId, currentDeptId);

    // 只有超管或部门管理员可以审核
    if (!isSuperAdmin && !isDeptManager) {
      throw new RuntimeException("无权限审核，只有上级可以审核");
    }

    // 检查是否是上级：部门管理员只能审核本部门下级的计划
    if (isDeptManager && !isSuperAdmin) {
      // 必须审核本部门的计划
      if (plan.getDeptId() == null || !plan.getDeptId().equals(currentDeptId)) {
        throw new RuntimeException("只能审核本部门的计划");
      }

      // 获取创建人的部门信息，检查是否是下级
      if (plan.getUserId() != null) {
        try {
          AdminUserRespDTO creator = adminUserApi.getUser(plan.getUserId());
          if (creator != null && creator.getDeptId() != null) {
            // 如果创建人的部门ID与当前部门ID相同，说明是同部门，可以审核
            // 如果创建人的部门ID是当前部门的子部门，也可以审核
            if (!creator.getDeptId().equals(currentDeptId)) {
              // 检查是否是子部门
              DeptRespDTO creatorDept = deptApi.getDept(creator.getDeptId());
              if (creatorDept == null || creatorDept.getParentId() == null
                  || !creatorDept.getParentId().equals(currentDeptId)) {
                throw new RuntimeException("只能审核下级部门的计划");
              }
            }
          }
        } catch (Exception e) {
          // 如果获取用户信息失败，允许审核（避免因系统问题导致无法审核）
          log.warn("获取创建人信息失败，允许审核: {}", e.getMessage());
        }
      }
    }

    // 只有待审核状态的计划才能审核
    if (!"pending".equals(plan.getStatus()) && !"pending_review".equals(plan.getStatus())
        && !"待审核".equals(plan.getStatus())) {
      throw new RuntimeException("只能审核待审核状态的计划");
    }

    // 获取审核人信息
    AdminUserRespDTO reviewer = adminUserApi.getUser(currentUserId);
    String reviewerName = reviewer != null ? reviewer.getNickname() : "未知审核人";

    // 更新审核信息
    OaWorkPlanDO updateObj = new OaWorkPlanDO();
    updateObj.setId(reviewReqVO.getId());

    if ("approved".equals(reviewReqVO.getReviewResult())) {
      updateObj.setStatus("approved");
    } else if ("rejected".equals(reviewReqVO.getReviewResult())) {
      updateObj.setStatus("rejected");
    }

    updateObj.setReviewerId(currentUserId);
    updateObj.setReviewerName(reviewerName);
    updateObj.setReviewTime(LocalDateTime.now());
    updateObj.setReviewComment(reviewReqVO.getReviewComment());

    oaWorkPlanMapper.updateById(updateObj);
  }

  @Override
  public void completeOaWorkPlan(OaWorkPlanCompleteReqVO completeReqVO) {
    // 校验存在
    OaWorkPlanDO plan = validateOaWorkPlanExists(completeReqVO.getId());

    // 校验权限：只有创建者可以更新完成情况
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    if (!plan.getUserId().equals(currentUserId)) {
      throw exception(OA_WORK_PLAN_NOT_EXISTS); // 无权限
    }

    // 只有已审核状态的计划才能更新完成情况
    if (!"approved".equals(plan.getStatus()) && !"已批准".equals(plan.getStatus()) && !"已审核".equals(plan.getStatus())) {
      throw new RuntimeException("只能更新已审核计划的完成情况");
    }

    // 更新完成情况和说明
    OaWorkPlanDO updateObj = new OaWorkPlanDO();
    updateObj.setId(completeReqVO.getId());
    updateObj.setCompletionStatus(completeReqVO.getCompletionStatus());
    updateObj.setCompletionComment(completeReqVO.getCompletionComment());

    // 如果选择已完成，同时更新状态为已完成
    if ("completed".equals(completeReqVO.getCompletionStatus())) {
      updateObj.setStatus("completed");
    }

    oaWorkPlanMapper.updateById(updateObj);
  }

  @Override
  public void evaluateOaWorkPlan(OaWorkPlanEvaluationReqVO evaluationReqVO) {
    // 校验存在
    OaWorkPlanDO plan = validateOaWorkPlanExists(evaluationReqVO.getId());

    // 校验权限：只有部门负责人可以评价
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    // 判断是否是部门负责人
    boolean isDeptManager = isDeptManager(currentUserId, currentDeptId);
    boolean isSuperAdmin = isSuperAdmin(currentUserId);

    if (!isSuperAdmin && !isDeptManager) {
      throw new RuntimeException("只有部门负责人可以评价工作计划");
    }

    // 必须评价本部门的计划
    if (plan.getDeptId() == null || !plan.getDeptId().equals(currentDeptId)) {
      if (!isSuperAdmin) {
        throw new RuntimeException("只能评价本部门的计划");
      }
    }

    // 只有已完成状态的计划才能评价（必须已保存完成情况）
    if (plan.getCompletionStatus() == null || plan.getCompletionStatus().isEmpty()) {
      throw new RuntimeException("只能评价已保存完成情况的计划");
    }

    // 获取评价人信息
    AdminUserRespDTO evaluator = adminUserApi.getUser(currentUserId);
    String evaluatorName = evaluator != null ? evaluator.getNickname() : "未知评价人";

    // 更新评价信息
    OaWorkPlanDO updateObj = new OaWorkPlanDO();
    updateObj.setId(evaluationReqVO.getId());
    updateObj.setQualityEvaluation(evaluationReqVO.getQualityEvaluation());
    updateObj.setCostEfficiencyEvaluation(evaluationReqVO.getCostEfficiencyEvaluation());
    updateObj.setEvaluationScore(evaluationReqVO.getEvaluationScore());
    updateObj.setEvaluatorId(currentUserId);
    updateObj.setEvaluatorName(evaluatorName);
    updateObj.setEvaluationTime(LocalDateTime.now());

    oaWorkPlanMapper.updateById(updateObj);
  }

  @Override
  public OaWorkPlanStatisticsVO getOaWorkPlanStatistics() {
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    // 判断用户权限
    boolean isSuperAdmin = isSuperAdmin(currentUserId);
    boolean isDeptManager = isDeptManager(currentUserId, currentDeptId);

    // 构建查询条件
    OaWorkPlanPageReqVO queryVO = new OaWorkPlanPageReqVO();
    queryVO.setPageNo(1);
    queryVO.setPageSize(10000); // 获取足够多的数据用于统计

    // 权限控制
    if (!isSuperAdmin && !isDeptManager) {
      queryVO.setUserId(currentUserId);
    } else if (isDeptManager && !isSuperAdmin) {
      queryVO.setDeptId(currentDeptId);
    }

    // 获取所有计划
    List<OaWorkPlanDO> allPlans = oaWorkPlanMapper.selectList(
        new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<OaWorkPlanDO>()
            .eqIfPresent(OaWorkPlanDO::getUserId, queryVO.getUserId())
            .eqIfPresent(OaWorkPlanDO::getDeptId, queryVO.getDeptId()));

    // 计算本月计划
    YearMonth currentMonth = YearMonth.now();
    List<OaWorkPlanDO> thisMonthPlans = allPlans.stream()
        .filter(plan -> {
          if (plan.getSubmitDate() == null && plan.getCreateTime() == null) {
            return false;
          }
          LocalDateTime date = plan.getSubmitDate() != null ? plan.getSubmitDate() : plan.getCreateTime();
          YearMonth planMonth = YearMonth.from(date);
          return planMonth.equals(currentMonth);
        })
        .collect(Collectors.toList());

    long thisMonthCount = thisMonthPlans.size();
    long thisMonthCompleted = thisMonthPlans.stream()
        .filter(plan -> "completed".equals(plan.getStatus()) || "已完成".equals(plan.getStatus()))
        .count();

    // 计算平均完成度
    List<OaWorkPlanDO> plansWithRate = thisMonthPlans.stream()
        .filter(plan -> plan.getCompletionRate() != null && plan.getCompletionRate() > 0)
        .collect(Collectors.toList());

    int avgCompletionRate = 0;
    if (!plansWithRate.isEmpty()) {
      double sum = plansWithRate.stream()
          .mapToInt(OaWorkPlanDO::getCompletionRate)
          .sum();
      avgCompletionRate = (int) Math.round(sum / plansWithRate.size());
    }

    // 计算平均质量评分
    List<OaWorkPlanDO> plansWithScore = thisMonthPlans.stream()
        .filter(plan -> plan.getQualityScore() != null && plan.getQualityScore() > 0)
        .collect(Collectors.toList());

    double avgQualityScore = 0.0;
    if (!plansWithScore.isEmpty()) {
      double sum = plansWithScore.stream()
          .mapToDouble(OaWorkPlanDO::getQualityScore)
          .sum();
      avgQualityScore = Math.round((sum / plansWithScore.size()) * 10.0) / 10.0;
    }

    // 计算待审核计划
    long pendingReview = allPlans.stream()
        .filter(plan -> "pending".equals(plan.getStatus()) ||
            "pending_review".equals(plan.getStatus()) ||
            "待审核".equals(plan.getStatus()))
        .count();

    return OaWorkPlanStatisticsVO.builder()
        .thisMonthCount(thisMonthCount)
        .thisMonthCompleted(thisMonthCompleted)
        .avgCompletionRate(avgCompletionRate)
        .avgQualityScore(avgQualityScore)
        .pendingReview(pendingReview)
        .build();
  }

  /**
   * 判断是否是超级管理员
   */
  private boolean isSuperAdmin(Long userId) {
    try {
      AdminUserRespDTO user = adminUserApi.getUser(userId);
      if (user == null) {
        return false;
      }

      // 通过角色代码判断
      String roleCodes = user.getRoleCodes();
      if (roleCodes == null || roleCodes.isEmpty()) {
        return false;
      }

      String roleCodesLower = roleCodes.toLowerCase();
      // 检查是否包含管理员相关的角色代码
      return roleCodesLower.contains("admin") ||
          roleCodesLower.contains("super_admin") ||
          roleCodesLower.contains("super_administrator") ||
          roleCodesLower.contains("administrator");
    } catch (Exception e) {
      log.warn("判断超级管理员失败: {}", e.getMessage());
      return false;
    }
  }

  /**
   * 判断是否是部门管理员（负责人）
   */
  private boolean isDeptManager(Long userId, Long deptId) {
    try {
      if (deptId == null) {
        return false;
      }

      // 获取部门信息
      DeptRespDTO dept = deptApi.getDept(deptId);
      if (dept == null) {
        return false;
      }

      // 判断是否是部门负责人（通过部门的leaderUserId字段）
      if (dept.getLeaderUserId() != null && dept.getLeaderUserId().equals(userId)) {
        return true;
      }

      // 也可以通过角色代码判断（包含"fzr"、"负责人"等）
      AdminUserRespDTO user = adminUserApi.getUser(userId);
      if (user == null) {
        return false;
      }

      String roleCodes = user.getRoleCodes();
      if (roleCodes == null || roleCodes.isEmpty()) {
        return false;
      }

      String roleCodesLower = roleCodes.toLowerCase();
      // 检查是否包含负责人相关的角色代码，且用户属于该部门
      boolean hasManagerRole = roleCodesLower.contains("fzr") ||
          roleCodesLower.contains("负责人") ||
          roleCodesLower.contains("manager");

      if (hasManagerRole && user.getDeptId() != null && user.getDeptId().equals(deptId)) {
        return true;
      }

      return false;
    } catch (Exception e) {
      log.warn("判断部门管理员失败: {}", e.getMessage());
      return false;
    }
  }

}
