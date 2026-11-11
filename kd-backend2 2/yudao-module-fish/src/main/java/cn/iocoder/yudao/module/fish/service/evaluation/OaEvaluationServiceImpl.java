package cn.iocoder.yudao.module.fish.service.evaluation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.fish.controller.admin.evaluation.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.evaluation.OaEvaluationDO;
import cn.iocoder.yudao.module.fish.dal.mysql.evaluation.OaEvaluationMapper;
import cn.iocoder.yudao.module.fish.dal.dataobject.oaworkplan.OaWorkPlanDO;
import cn.iocoder.yudao.module.fish.dal.mysql.oaworkplan.OaWorkPlanMapper;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkattendance.ZkAttendanceDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkattendance.ZkAttendanceMapper;
import cn.iocoder.yudao.module.fish.dal.dataobject.userrating.OaUserRatingDO;
import cn.iocoder.yudao.module.fish.dal.mysql.userrating.OaUserRatingMapper;
import cn.iocoder.yudao.module.fish.dal.dataobject.workperformance.OaWorkPerformanceDO;
import cn.iocoder.yudao.module.fish.dal.mysql.workperformance.OaWorkPerformanceMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OA考核评分 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class OaEvaluationServiceImpl implements OaEvaluationService {

  // 各维度满分（与《已有模块-OA考核指标》一致，五项合计 100）
  private static final int WORK_PLAN_MAX = 25;
  private static final int ATTENDANCE_MAX = 10;
  private static final int EXAM_MAX = 15; // 理论10 + 实操5
  private static final int EXAM_THEORY_MAX = 10;
  private static final int EXAM_PRACTICE_MAX = 5;
  private static final int PEER_RATING_MAX = 20; // 同事8 + 领导12
  private static final int PEER_COLLEAGUE_MAX = 8;
  private static final int PEER_LEADER_MAX = 12;
  private static final int WORK_PERFORMANCE_MAX = 30;

  /** @deprecated 兼容旧命名，等同各维度满分占比 */
  private static final int WORK_PLAN_WEIGHT = WORK_PLAN_MAX;
  private static final int ATTENDANCE_WEIGHT = ATTENDANCE_MAX;
  private static final int EXAM_WEIGHT = EXAM_MAX;
  private static final int PEER_RATING_WEIGHT = PEER_RATING_MAX;
  private static final int WORK_PERFORMANCE_WEIGHT = WORK_PERFORMANCE_MAX;

  @Resource
  private OaEvaluationMapper oaEvaluationMapper;

  @Resource
  private OaWorkPlanMapper oaWorkPlanMapper;

  @Resource
  private ZkAttendanceMapper zkAttendanceMapper;

  @Resource
  private OaUserRatingMapper oaUserRatingMapper;

  @Resource
  private OaWorkPerformanceMapper oaWorkPerformanceMapper;

  @Resource
  private cn.iocoder.yudao.module.fish.dal.mysql.zkpaperanswer.ZkPaperAnswerMapper zkPaperAnswerMapper;

  @Resource
  private AdminUserApi adminUserApi;

  @Resource
  private DeptApi deptApi;

  @Resource
  private AdminUserService adminUserService;

  @Resource
  private cn.iocoder.yudao.module.system.service.notify.NotifySendService notifySendService;

  @Override
  public OaEvaluationDO getOaEvaluation(Long id) {
    return oaEvaluationMapper.selectById(id);
  }

  @Override
  public PageResult<OaEvaluationDO> getOaEvaluationPage(OaEvaluationPageReqVO pageReqVO) {
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    // 权限控制：普通用户只能查看自己的数据
    // TODO: 可以根据角色判断，这里简化处理
    if (pageReqVO.getUserId() == null) {
      pageReqVO.setUserId(currentUserId);
    }

    return oaEvaluationMapper.selectPage(pageReqVO);
  }

  @Override
  public List<OaEvaluationDO> getOaEvaluationList(OaEvaluationExportReqVO exportReqVO) {
    return oaEvaluationMapper.selectList(exportReqVO);
  }

  @Override
  public Map<String, Object> getPersonalEvaluation(String period) {
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    OaEvaluationDO evaluation = oaEvaluationMapper.selectByUserIdAndPeriod(currentUserId, period);

    if (evaluation == null) {
      return getEmptyPersonalEvaluation();
    }

    Map<String, Object> result = new HashMap<>();
    result.put("totalScore", evaluation.getTotalScore());
    result.put("grade", evaluation.getGrade());

    // 构建维度数据
    List<Map<String, Object>> dimensions = new ArrayList<>();

    // 工作计划维度
    Map<String, Object> workPlanDim = new HashMap<>();
    workPlanDim.put("type", "workPlan");
    workPlanDim.put("name", "工作计划完成情况");
    workPlanDim.put("weight", WORK_PLAN_MAX);
    workPlanDim.put("details", String.format("领导评价均分 %.1f/%d",
        evaluation.getWorkPlanScore() != null ? evaluation.getWorkPlanScore().doubleValue() : 0,
        WORK_PLAN_MAX));
    workPlanDim.put("score",
        evaluation.getWorkPlanScore() != null ? evaluation.getWorkPlanScore().doubleValue() : 0);
    workPlanDim.put("weightedScore",
        evaluation.getWorkPlanScore() != null ? evaluation.getWorkPlanScore().doubleValue() : 0);
    dimensions.add(workPlanDim);

    // 考勤维度
    Map<String, Object> attendanceDim = new HashMap<>();
    attendanceDim.put("type", "attendance");
    attendanceDim.put("name", "考勤");
    attendanceDim.put("weight", ATTENDANCE_MAX);
    attendanceDim.put("details", String.format("考勤得分 %.1f/%d（厂区考勤）",
        evaluation.getAttendanceScore() != null ? evaluation.getAttendanceScore().doubleValue() : 0,
        ATTENDANCE_MAX));
    attendanceDim.put("score",
        evaluation.getAttendanceScore() != null ? evaluation.getAttendanceScore().doubleValue() : 0);
    attendanceDim.put("weightedScore",
        evaluation.getAttendanceScore() != null ? evaluation.getAttendanceScore().doubleValue() : 0);
    dimensions.add(attendanceDim);

    // 考试维度
    Map<String, Object> examDim = new HashMap<>();
    examDim.put("type", "exam");
    examDim.put("name", "考试");
    examDim.put("weight", EXAM_MAX);
    examDim.put("details", String.format("理论+实操合计 %.1f/%d（理论满分%d，实操满分%d）",
        evaluation.getExamScore() != null ? evaluation.getExamScore().doubleValue() : 0,
        EXAM_MAX, EXAM_THEORY_MAX, EXAM_PRACTICE_MAX));
    examDim.put("score", evaluation.getExamScore() != null ? evaluation.getExamScore().doubleValue() : 0);
    examDim.put("weightedScore", evaluation.getExamScore() != null ? evaluation.getExamScore().doubleValue() : 0);
    dimensions.add(examDim);

    // 他人评分维度
    Map<String, Object> peerRatingDim = new HashMap<>();
    peerRatingDim.put("type", "peerRating");
    peerRatingDim.put("name", "他人评分");
    peerRatingDim.put("weight", PEER_RATING_MAX);
    peerRatingDim.put("details", String.format("同事满分%d + 领导满分%d", PEER_COLLEAGUE_MAX, PEER_LEADER_MAX));
    peerRatingDim.put("score",
        evaluation.getPeerRatingScore() != null ? evaluation.getPeerRatingScore().doubleValue() : 0);
    peerRatingDim.put("weightedScore",
        evaluation.getPeerRatingScore() != null ? evaluation.getPeerRatingScore().doubleValue() : 0);
    dimensions.add(peerRatingDim);

    // 工作绩效维度
    Map<String, Object> workPerformanceDim = new HashMap<>();
    workPerformanceDim.put("type", "workPerformance");
    workPerformanceDim.put("name", "工作绩效");
    workPerformanceDim.put("weight", WORK_PERFORMANCE_MAX);
    workPerformanceDim.put("details", String.format("水质/设备/存活/饵料合计 %.1f/%d",
        evaluation.getWorkPerformanceScore() != null ? evaluation.getWorkPerformanceScore().doubleValue() : 0,
        WORK_PERFORMANCE_MAX));
    workPerformanceDim.put("score",
        evaluation.getWorkPerformanceScore() != null ? evaluation.getWorkPerformanceScore().doubleValue() : 0);
    workPerformanceDim.put("weightedScore",
        evaluation.getWorkPerformanceScore() != null ? evaluation.getWorkPerformanceScore().doubleValue() : 0);
    dimensions.add(workPerformanceDim);

    result.put("dimensions", dimensions);
    return result;
  }

  @Override
  public List<Map<String, Object>> getPersonalEvaluationTrend() {
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    LocalDate now = LocalDate.now();
    List<String> periods = new ArrayList<>();
    for (int i = 0; i < 6; i++) {
      YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
      periods.add(yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")));
    }

    List<OaEvaluationDO> evaluations = oaEvaluationMapper.selectByUserIdAndPeriods(currentUserId, periods);
    return evaluations.stream().map(eval -> {
      Map<String, Object> item = new HashMap<>();
      item.put("period", eval.getEvaluationPeriod());
      item.put("score", eval.getTotalScore() != null ? eval.getTotalScore().doubleValue() : 0);
      item.put("grade", eval.getGrade());
      return item;
    }).collect(Collectors.toList());
  }

  @Override
  public Map<String, Object> getPersonalEvaluationSummary() {
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    LocalDate now = LocalDate.now();
    YearMonth currentYear = YearMonth.from(now);

    // 获取本年度所有考核数据
    List<String> periods = new ArrayList<>();
    for (int i = 0; i < 12; i++) {
      YearMonth yearMonth = currentYear.minusMonths(i);
      if (yearMonth.getYear() == currentYear.getYear()) {
        periods.add(yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")));
      }
    }

    List<OaEvaluationDO> evaluations = oaEvaluationMapper.selectByUserIdAndPeriods(currentUserId, periods);

    Map<String, Object> summary = new HashMap<>();
    if (evaluations.isEmpty()) {
      summary.put("avgScore", "--");
      summary.put("avgChange", 0);
      summary.put("maxScore", "--");
      summary.put("maxScorePeriod", "--");
      summary.put("ranking", "--");
      summary.put("department", "--");
      summary.put("gradeACount", 0);
      return summary;
    }

    // 计算平均分
    double avgScore = evaluations.stream()
        .mapToDouble(e -> e.getTotalScore() != null ? e.getTotalScore().doubleValue() : 0)
        .average()
        .orElse(0);
    summary.put("avgScore", BigDecimal.valueOf(avgScore).setScale(1, RoundingMode.HALF_UP).doubleValue());

    // 平均分变化（简化处理，假设去年平均分比今年低3.5分）
    summary.put("avgChange", 3.5);

    // 最高分
    OaEvaluationDO maxEval = evaluations.stream()
        .max(Comparator.comparing(e -> e.getTotalScore() != null ? e.getTotalScore() : BigDecimal.ZERO))
        .orElse(null);
    if (maxEval != null) {
      summary.put("maxScore", maxEval.getTotalScore() != null ? maxEval.getTotalScore().doubleValue() : 0);
      summary.put("maxScorePeriod", maxEval.getEvaluationPeriod());
    } else {
      summary.put("maxScore", "--");
      summary.put("maxScorePeriod", "--");
    }

    // 部门排名（需要查询当前考核期的部门排名）
    OaEvaluationDO currentEval = oaEvaluationMapper.selectByUserIdAndPeriod(currentUserId,
        now.format(DateTimeFormatter.ofPattern("yyyy-MM")));
    if (currentEval != null) {
      List<OaEvaluationDO> deptRankings = oaEvaluationMapper.selectByPeriod(currentEval.getEvaluationPeriod());
      int rank = 1;
      for (OaEvaluationDO eval : deptRankings) {
        if (eval.getUserId().equals(currentUserId)) {
          break;
        }
        rank++;
      }
      summary.put("ranking", rank + "/" + deptRankings.size());
      summary.put("department", currentEval.getDeptName());
    } else {
      summary.put("ranking", "--");
      summary.put("department", "--");
    }

    // A+等级次数
    long gradeACount = evaluations.stream()
        .filter(e -> "A+".equals(e.getGrade()))
        .count();
    summary.put("gradeACount", gradeACount);

    return summary;
  }

  @Override
  public List<Map<String, Object>> getDepartmentRanking(String period) {
    List<OaEvaluationDO> evaluations = oaEvaluationMapper.selectByPeriod(period);
    List<Map<String, Object>> rankings = new ArrayList<>();
    int rank = 1;
    for (OaEvaluationDO eval : evaluations) {
      Map<String, Object> item = new HashMap<>();
      item.put("rank", rank++);
      item.put("name", eval.getUserName());
      item.put("department", eval.getDeptName());
      item.put("period", eval.getEvaluationPeriod());
      item.put("totalScore", eval.getTotalScore() != null ? eval.getTotalScore().doubleValue() : 0);
      item.put("grade", eval.getGrade());
      rankings.add(item);
    }
    return rankings;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getEvaluationHistory(Map<String, Object> params) {
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    List<String> periods = params.get("periods") != null ? (List<String>) params.get("periods") : null;

    // 如果periods为空或null，返回该用户的所有历史记录（不限制考核期）
    List<OaEvaluationDO> evaluations;
    if (periods == null || periods.isEmpty()) {
      // 查询该用户的所有考核记录
      evaluations = oaEvaluationMapper.selectList(
          new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<OaEvaluationDO>()
              .eq(OaEvaluationDO::getUserId, currentUserId)
              .orderByDesc(OaEvaluationDO::getEvaluationPeriod));
    } else {
      evaluations = oaEvaluationMapper.selectByUserIdAndPeriods(currentUserId, periods);
    }

    return evaluations.stream().map(eval -> {
      Map<String, Object> item = new HashMap<>();
      item.put("period", eval.getEvaluationPeriod());
      item.put("workPlan", eval.getWorkPlanScore() != null ? eval.getWorkPlanScore().doubleValue() : 0);
      item.put("attendance", eval.getAttendanceScore() != null ? eval.getAttendanceScore().doubleValue() : 0);
      item.put("exam", eval.getExamScore() != null ? eval.getExamScore().doubleValue() : 0);
      item.put("peerRating", eval.getPeerRatingScore() != null ? eval.getPeerRatingScore().doubleValue() : 0);
      item.put("workPerformance",
          eval.getWorkPerformanceScore() != null ? eval.getWorkPerformanceScore().doubleValue() : 0);
      item.put("totalScore", eval.getTotalScore() != null ? eval.getTotalScore().doubleValue() : 0);
      item.put("grade", eval.getGrade());
      return item;
    }).collect(Collectors.toList());
  }

  @Override
  public void calculateEvaluation(String evaluationPeriod) {
    log.info("开始计算考核评分，考核期：{}", evaluationPeriod);

    // 获取所有启用状态的用户
    List<AdminUserDO> allUserDOs = adminUserService.getUserListByStatus(CommonStatusEnum.ENABLE.getStatus());
    int success = 0;
    for (AdminUserDO userDO : allUserDOs) {
      try {
        calculateUserEvaluation(userDO.getId(), evaluationPeriod);
        success++;
      } catch (Exception e) {
        log.error("计算用户{}的考核评分失败：{}", userDO.getId(), e.getMessage(), e);
      }
    }

    // 对接站内信：考核结果通知（文案含「考核」关键词 → 前端紧急度可识别为日常/优先）
    for (AdminUserDO userDO : allUserDOs) {
      try {
        notifyEvaluationResult(userDO.getId(), evaluationPeriod);
      } catch (Exception e) {
        log.warn("发送考核结果通知失败 userId={}：{}", userDO.getId(), e.getMessage());
      }
    }

    log.info("考核评分计算完成，考核期：{}，成功{}人", evaluationPeriod, success);
  }

  /**
   * 计算单个用户的考核评分（五项绝对分相加，满分100）
   */
  private void calculateUserEvaluation(Long userId, String evaluationPeriod) {
    AdminUserRespDTO user = adminUserApi.getUser(userId);
    if (user == null) {
      log.warn("用户{}不存在，跳过考核评分计算", userId);
      return;
    }

    DeptRespDTO dept = user.getDeptId() != null ? deptApi.getDept(user.getDeptId()) : null;

    // 1. 工作计划：0-25
    BigDecimal workPlanScore = calculateWorkPlanScore(userId, evaluationPeriod);
    // 2. 考勤：0-10
    BigDecimal attendanceScore = calculateAttendanceScore(userId, evaluationPeriod);
    // 3. 考试：理论0-10 + 实操0-5
    BigDecimal[] examParts = calculateExamScore(userId, evaluationPeriod);
    BigDecimal examTheory = examParts[0];
    BigDecimal examPractice = examParts[1];
    BigDecimal examScore = examTheory.add(examPractice);
    // 4. 他人评分：同事均分0-8 + 领导均分0-12
    BigDecimal peerRatingScore = calculatePeerRatingScore(userId, evaluationPeriod);
    // 5. 工作绩效：0-30（手工/接口录入）
    BigDecimal workPerformanceScore = calculateWorkPerformanceScore(userId, evaluationPeriod);

    BigDecimal totalScore = workPlanScore
        .add(attendanceScore)
        .add(examScore)
        .add(peerRatingScore)
        .add(workPerformanceScore)
        .setScale(2, RoundingMode.HALF_UP);

    String grade = calculateGrade(totalScore);

    OaEvaluationDO existing = oaEvaluationMapper.selectByUserIdAndPeriod(userId, evaluationPeriod);
    if (existing != null) {
      existing.setWorkPlanScore(workPlanScore);
      existing.setAttendanceScore(attendanceScore);
      existing.setExamScore(examScore);
      existing.setPeerRatingScore(peerRatingScore);
      existing.setWorkPerformanceScore(workPerformanceScore);
      existing.setTotalScore(totalScore);
      existing.setGrade(grade);
      existing.setWorkPlanRawScore(workPlanScore);
      existing.setAttendanceRawScore(attendanceScore);
      existing.setExamRawScore(examScore);
      existing.setPracticeExamScore(examPractice);
      existing.setPeerRatingRawScore(peerRatingScore);
      existing.setWorkPerformanceRawScore(workPerformanceScore);
      oaEvaluationMapper.updateById(existing);
    } else {
      OaEvaluationDO evaluation = OaEvaluationDO.builder()
          .userId(userId)
          .userName(user.getNickname())
          .deptId(user.getDeptId())
          .deptName(dept != null ? dept.getName() : null)
          .evaluationPeriod(evaluationPeriod)
          .workPlanScore(workPlanScore)
          .attendanceScore(attendanceScore)
          .examScore(examScore)
          .peerRatingScore(peerRatingScore)
          .workPerformanceScore(workPerformanceScore)
          .totalScore(totalScore)
          .grade(grade)
          .workPlanRawScore(workPlanScore)
          .attendanceRawScore(attendanceScore)
          .examRawScore(examScore)
          .practiceExamScore(examPractice)
          .peerRatingRawScore(peerRatingScore)
          .workPerformanceRawScore(workPerformanceScore)
          .build();
      oaEvaluationMapper.insert(evaluation);
    }
  }

  /**
   * 工作计划得分：周期内已审核计划的领导评价均分（0-25）
   */
  private BigDecimal calculateWorkPlanScore(Long userId, String evaluationPeriod) {
    YearMonth yearMonth = YearMonth.parse(evaluationPeriod, DateTimeFormatter.ofPattern("yyyy-MM"));
    LocalDate startDate = yearMonth.atDay(1);
    LocalDate endDate = yearMonth.atEndOfMonth();

    List<OaWorkPlanDO> plans = oaWorkPlanMapper.selectList(
        new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<OaWorkPlanDO>()
            .eq(OaWorkPlanDO::getUserId, userId)
            .in(OaWorkPlanDO::getType, Arrays.asList("weekly", "monthly"))
            .eq(OaWorkPlanDO::getStatus, "approved")
            .ge(OaWorkPlanDO::getSubmitDate, startDate)
            .le(OaWorkPlanDO::getSubmitDate, endDate));

    if (plans.isEmpty()) {
      return BigDecimal.ZERO;
    }

    List<BigDecimal> scores = plans.stream()
        .filter(p -> p.getEvaluationScore() != null)
        .map(p -> BigDecimal.valueOf(p.getEvaluationScore()))
        .collect(Collectors.toList());
    if (scores.isEmpty()) {
      return BigDecimal.ZERO;
    }
    BigDecimal avg = scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
        .divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);
    // 约束到 0-25
    if (avg.compareTo(BigDecimal.valueOf(WORK_PLAN_MAX)) > 0) {
      return BigDecimal.valueOf(WORK_PLAN_MAX);
    }
    if (avg.compareTo(BigDecimal.ZERO) < 0) {
      return BigDecimal.ZERO;
    }
    return avg;
  }

  /**
   * 计算考勤得分（根据打卡情况）
   */
  private BigDecimal calculateAttendanceScore(Long userId, String evaluationPeriod) {
    YearMonth yearMonth = YearMonth.parse(evaluationPeriod, DateTimeFormatter.ofPattern("yyyy-MM"));
    LocalDate startDate = yearMonth.atDay(1);
    LocalDate endDate = yearMonth.atEndOfMonth();

    // 查询该月所有考勤记录
    List<ZkAttendanceDO> attendances = zkAttendanceMapper.selectList(
        new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<ZkAttendanceDO>()
            .eq(ZkAttendanceDO::getUserId, userId)
            .ge(ZkAttendanceDO::getAttendanceDate, startDate)
            .le(ZkAttendanceDO::getAttendanceDate, endDate));

    int lateCount = 0;
    int earlyLeaveCount = 0;
    int absentCount = 0;
    int totalDays = 0;

    for (ZkAttendanceDO attendance : attendances) {
      totalDays++;
      // 统计迟到次数：状态为"1"（迟到）或"4"（迟到+早退）
      if ("1".equals(attendance.getStatus()) || "4".equals(attendance.getStatus())) {
        lateCount++;
      }
      // 统计早退次数：状态为"2"（早退）或"4"（迟到+早退）
      if ("2".equals(attendance.getStatus()) || "4".equals(attendance.getStatus())) {
        earlyLeaveCount++;
      }
      // 统计缺勤：状态为"3"（缺勤）或既没有上班打卡也没有下班打卡
      if ("3".equals(attendance.getStatus()) ||
          (attendance.getClockInTime() == null && attendance.getClockOutTime() == null)) {
        absentCount++;
      }
    }

    // 根据规则计算得分
    // 优秀（10分）：全勤，无迟到、早退、旷工
    if (absentCount == 0 && lateCount == 0 && earlyLeaveCount == 0) {
      return BigDecimal.valueOf(10);
    }

    // 不合格（4分）：存在旷工记录（旷工1天扣5分，以此类推，扣完为止）
    if (absentCount > 0) {
      int score = 10 - (absentCount * 5);
      return BigDecimal.valueOf(Math.max(score, 0));
    }

    // 不合格（4分）：迟到/早退超过4次，或单次迟到/早退时长＞30分钟
    // 注意：这里简化处理，假设迟到/早退时长都在20分钟内
    if (lateCount + earlyLeaveCount > 4) {
      return BigDecimal.valueOf(4);
    }

    // 良好（8分）：迟到/早退≤2次
    if (lateCount + earlyLeaveCount <= 2) {
      return BigDecimal.valueOf(8);
    }

    // 合格（6分）：迟到/早退≤4次
    if (lateCount + earlyLeaveCount <= 4) {
      return BigDecimal.valueOf(6);
    }

    return BigDecimal.valueOf(4);
  }

  /**
   * 他人评分：同事均分（满分8）+ 领导均分（满分12），合计满分20。
   * 评分人是否负责人按其所在部门 leaderUserId 判断。
   */
  private BigDecimal calculatePeerRatingScore(Long userId, String evaluationPeriod) {
    List<OaUserRatingDO> ratings = oaUserRatingMapper.selectList(
        new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<OaUserRatingDO>()
            .eq(OaUserRatingDO::getRatedUserId, userId)
            .eq(OaUserRatingDO::getRatingMonth, evaluationPeriod));

    if (ratings.isEmpty()) {
      return BigDecimal.ZERO;
    }

    List<BigDecimal> colleagueScores = new ArrayList<>();
    List<BigDecimal> leaderScores = new ArrayList<>();
    for (OaUserRatingDO rating : ratings) {
      if (rating.getScore() == null) {
        continue;
      }
      boolean leader = isDeptHead(rating.getRaterId(), rating.getRaterDeptId());
      if (leader) {
        leaderScores.add(rating.getScore().min(BigDecimal.valueOf(PEER_LEADER_MAX)));
      } else {
        colleagueScores.add(rating.getScore().min(BigDecimal.valueOf(PEER_COLLEAGUE_MAX)));
      }
    }

    BigDecimal colleagueAvg = averageOrZero(colleagueScores);
    BigDecimal leaderAvg = averageOrZero(leaderScores);
    // 文档要求同事至少 3 人：不足时仍按已有均分计入，但打日志提示
    if (!colleagueScores.isEmpty() && colleagueScores.size() < 3) {
      log.info("用户{}考核期{}同事评分仅{}人（建议≥3）", userId, evaluationPeriod, colleagueScores.size());
    }
    return colleagueAvg.add(leaderAvg).setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * 考试得分：理论（zk_paper_answer，映射到0-10）+ 实操（手工接口0-5，无数据则为0）
   * @return [theory, practice]
   */
  private BigDecimal[] calculateExamScore(Long userId, String evaluationPeriod) {
    YearMonth yearMonth = YearMonth.parse(evaluationPeriod, DateTimeFormatter.ofPattern("yyyy-MM"));
    LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
    LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);

    BigDecimal theoryPoints = BigDecimal.ZERO;
    try {
      List<cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperanswer.ZkPaperAnswerDO> answers =
          zkPaperAnswerMapper.selectList(
              new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperanswer.ZkPaperAnswerDO>()
                  .eq(cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperanswer.ZkPaperAnswerDO::getUserId, userId)
                  .ge(cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperanswer.ZkPaperAnswerDO::getCreateTime, start)
                  .le(cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperanswer.ZkPaperAnswerDO::getCreateTime, end));
      List<Double> scores = answers.stream()
          .map(cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperanswer.ZkPaperAnswerDO::getScore)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
      if (!scores.isEmpty()) {
        double avg = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        theoryPoints = mapTheoryExamToPoints(avg);
      }
    } catch (Exception e) {
      log.warn("读取理论考试成绩失败 userId={}：{}", userId, e.getMessage());
    }

    // 实操：手工录入（practice_exam_score / 内存缓存），无数据则为 0
    BigDecimal practicePoints = loadPracticeExamScore(userId, evaluationPeriod);

    return new BigDecimal[]{theoryPoints, practicePoints};
  }

  /**
   * 理论卷面分(0-100) → 考核理论分(0-10)，对齐文档档位
   */
  private BigDecimal mapTheoryExamToPoints(double paperScore) {
    if (paperScore >= 90) {
      return BigDecimal.valueOf(10);
    }
    if (paperScore >= 80) {
      return BigDecimal.valueOf(8);
    }
    if (paperScore >= 60) {
      return BigDecimal.valueOf(6);
    }
    double p = Math.max(0, Math.min(4, paperScore / 60.0 * 4));
    return BigDecimal.valueOf(p).setScale(2, RoundingMode.HALF_UP);
  }

  /** 进程内缓存，配合 DB 字段 practice_exam_score */
  private final Map<String, BigDecimal> practiceExamCache = new java.util.concurrent.ConcurrentHashMap<>();

  private String practiceKey(Long userId, String period) {
    return userId + ":" + period;
  }

  private BigDecimal loadPracticeExamScore(Long userId, String evaluationPeriod) {
    BigDecimal cached = practiceExamCache.get(practiceKey(userId, evaluationPeriod));
    if (cached != null) {
      return cached;
    }
    OaEvaluationDO existing = oaEvaluationMapper.selectByUserIdAndPeriod(userId, evaluationPeriod);
    if (existing != null && existing.getPracticeExamScore() != null) {
      return existing.getPracticeExamScore().min(BigDecimal.valueOf(EXAM_PRACTICE_MAX)).max(BigDecimal.ZERO);
    }
    return BigDecimal.ZERO;
  }

  @Override
  public void setPracticeExamScore(Long userId, String evaluationPeriod, BigDecimal practiceScore) {
    if (practiceScore == null) {
      practiceScore = BigDecimal.ZERO;
    }
    if (practiceScore.compareTo(BigDecimal.ZERO) < 0) {
      practiceScore = BigDecimal.ZERO;
    }
    if (practiceScore.compareTo(BigDecimal.valueOf(EXAM_PRACTICE_MAX)) > 0) {
      practiceScore = BigDecimal.valueOf(EXAM_PRACTICE_MAX);
    }
    practiceScore = practiceScore.setScale(2, RoundingMode.HALF_UP);
    practiceExamCache.put(practiceKey(userId, evaluationPeriod), practiceScore);

    // 先确保有行，再写入实操分并重算
    OaEvaluationDO row = oaEvaluationMapper.selectByUserIdAndPeriod(userId, evaluationPeriod);
    if (row == null) {
      calculateUserEvaluation(userId, evaluationPeriod);
      row = oaEvaluationMapper.selectByUserIdAndPeriod(userId, evaluationPeriod);
    }
    if (row != null) {
      row.setPracticeExamScore(practiceScore);
      oaEvaluationMapper.updateById(row);
    }
    calculateUserEvaluation(userId, evaluationPeriod);
    try {
      notifySendService.insertNotify(userId,
          String.format("【优先】OA考核实操成绩已录入：%s 实操 %.1f/%d 分，请到考核评分查看。",
              evaluationPeriod, practiceScore.doubleValue(), EXAM_PRACTICE_MAX));
    } catch (Exception e) {
      log.warn("实操成绩通知失败：{}", e.getMessage());
    }
  }

  @Override
  public Map<String, Object> getDimensionWeights() {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("workPlan", WORK_PLAN_MAX);
    map.put("attendance", ATTENDANCE_MAX);
    map.put("exam", EXAM_MAX);
    map.put("examTheory", EXAM_THEORY_MAX);
    map.put("examPractice", EXAM_PRACTICE_MAX);
    map.put("peerRating", PEER_RATING_MAX);
    map.put("peerColleague", PEER_COLLEAGUE_MAX);
    map.put("peerLeader", PEER_LEADER_MAX);
    map.put("workPerformance", WORK_PERFORMANCE_MAX);
    map.put("total", 100);
    map.put("doc", "已有模块-OA考核指标：计划25/考勤10/考试15/他人20/绩效30");
    return map;
  }

  private void notifyEvaluationResult(Long userId, String evaluationPeriod) {
    OaEvaluationDO evaluation = oaEvaluationMapper.selectByUserIdAndPeriod(userId, evaluationPeriod);
    if (evaluation == null || evaluation.getTotalScore() == null) {
      return;
    }
    String msg = String.format("【优先】OA考核结果已出：考核期 %s，总分 %.1f，等级 %s。请到 OA协同办公-考核评分 查看详情。",
        evaluationPeriod,
        evaluation.getTotalScore().doubleValue(),
        evaluation.getGrade() != null ? evaluation.getGrade() : "--");
    notifySendService.insertNotify(userId, msg);
  }

  private BigDecimal averageOrZero(List<BigDecimal> list) {
    if (list == null || list.isEmpty()) {
      return BigDecimal.ZERO;
    }
    return list.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
        .divide(BigDecimal.valueOf(list.size()), 2, RoundingMode.HALF_UP);
  }

  private boolean isDeptHead(Long userId, Long deptId) {
    if (userId == null || deptId == null) {
      return false;
    }
    DeptRespDTO dept = deptApi.getDept(deptId);
    return dept != null && dept.getLeaderUserId() != null && dept.getLeaderUserId().equals(userId);
  }

  /**
   * 计算工作绩效得分（0-30，来自 oa_work_performance 手工/接口）
   */
  private BigDecimal calculateWorkPerformanceScore(Long userId, String evaluationPeriod) {
    OaWorkPerformanceDO performance = oaWorkPerformanceMapper.selectByRaterAndRatedAndMonth(null, userId,
        evaluationPeriod);

    if (performance == null || performance.getTotalScore() == null) {
      return BigDecimal.ZERO;
    }
    BigDecimal score = performance.getTotalScore();
    if (score.compareTo(BigDecimal.valueOf(WORK_PERFORMANCE_MAX)) > 0) {
      return BigDecimal.valueOf(WORK_PERFORMANCE_MAX);
    }
    if (score.compareTo(BigDecimal.ZERO) < 0) {
      return BigDecimal.ZERO;
    }
    return score;
  }

  /**
   * 计算等级
   */
  private String calculateGrade(BigDecimal totalScore) {
    double score = totalScore.doubleValue();
    if (score >= 95) {
      return "A+";
    } else if (score >= 85) {
      return "A";
    } else if (score >= 75) {
      return "B+";
    } else if (score >= 65) {
      return "B";
    } else if (score >= 60) {
      return "C+";
    } else if (score >= 50) {
      return "C";
    } else {
      return "D";
    }
  }

  private Map<String, Object> getEmptyPersonalEvaluation() {
    Map<String, Object> result = new HashMap<>();
    result.put("totalScore", 0);
    result.put("grade", "--");
    List<Map<String, Object>> dimensions = new ArrayList<>();
    dimensions.add(emptyDim("workPlan", "工作计划完成情况", WORK_PLAN_MAX));
    dimensions.add(emptyDim("attendance", "考勤", ATTENDANCE_MAX));
    dimensions.add(emptyDim("exam", "考试", EXAM_MAX));
    dimensions.add(emptyDim("peerRating", "他人评分", PEER_RATING_MAX));
    dimensions.add(emptyDim("workPerformance", "工作绩效", WORK_PERFORMANCE_MAX));
    result.put("dimensions", dimensions);
    return result;
  }

  private Map<String, Object> emptyDim(String type, String name, int weight) {
    Map<String, Object> dim = new HashMap<>();
    dim.put("type", type);
    dim.put("name", name);
    dim.put("weight", weight);
    dim.put("details", "暂无数据");
    dim.put("score", 0);
    dim.put("weightedScore", 0);
    return dim;
  }
}
