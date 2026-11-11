package cn.iocoder.yudao.module.fish.service.userrating;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.fish.controller.admin.userrating.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.userrating.OaUserRatingDO;
import cn.iocoder.yudao.module.fish.dal.mysql.userrating.OaUserRatingMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.OA_USER_RATING_NOT_EXISTS;

/**
 * OA他人打分 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class OaUserRatingServiceImpl implements OaUserRatingService {

  @Resource
  private OaUserRatingMapper oaUserRatingMapper;

  @Resource
  private AdminUserApi adminUserApi;

  @Resource
  private DeptApi deptApi;

  @Override
  public Long createOaUserRating(OaUserRatingCreateReqVO createReqVO) {
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    // 校验权限
    validateRatingPermission(currentUserId, currentDeptId, createReqVO.getRatedUserId(), createReqVO.getScore());

    // 获取当前月份
    String ratingMonth = createReqVO.getRatingMonth();
    if (ratingMonth == null || ratingMonth.isEmpty()) {
      ratingMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    // 检查是否已经评分过（同一评分人对同一被评分人在同一月份只能评分一次）
    OaUserRatingDO existing = oaUserRatingMapper.selectByRaterAndRatedAndMonth(
        currentUserId, createReqVO.getRatedUserId(), ratingMonth);
    if (existing != null) {
      throw new RuntimeException("本月已对该用户评分，请使用修改功能");
    }

    // 获取评分人和被评分人信息
    AdminUserRespDTO rater = adminUserApi.getUser(currentUserId);
    AdminUserRespDTO ratedUser = adminUserApi.getUser(createReqVO.getRatedUserId());

    // 创建
    OaUserRatingDO rating = OaUserRatingDO.builder()
        .raterId(currentUserId)
        .raterName(rater != null ? rater.getNickname() : "未知")
        .raterDeptId(currentDeptId)
        .ratedUserId(createReqVO.getRatedUserId())
        .ratedUserName(ratedUser != null ? ratedUser.getNickname() : "未知")
        .ratedUserDeptId(ratedUser != null ? ratedUser.getDeptId() : null)
        .score(createReqVO.getScore())
        .comment(createReqVO.getComment())
        .ratingMonth(ratingMonth)
        .build();
    oaUserRatingMapper.insert(rating);
    return rating.getId();
  }

  @Override
  public void updateOaUserRating(OaUserRatingUpdateReqVO updateReqVO) {
    // 校验存在
    OaUserRatingDO existing = validateOaUserRatingExists(updateReqVO.getId());

    // 校验权限：只有评分人可以修改
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    if (!existing.getRaterId().equals(currentUserId)) {
      throw new RuntimeException("只能修改自己的评分");
    }

    // 获取当前月份
    String ratingMonth = existing.getRatingMonth();
    if (ratingMonth == null || ratingMonth.isEmpty()) {
      ratingMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    // 校验评分范围
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();
    validateScoreRange(currentUserId, currentDeptId, updateReqVO.getScore());

    // 更新
    OaUserRatingDO updateObj = BeanUtils.toBean(updateReqVO, OaUserRatingDO.class);
    oaUserRatingMapper.updateById(updateObj);
  }

  @Override
  public void deleteOaUserRating(Long id) {
    // 校验存在
    OaUserRatingDO existing = validateOaUserRatingExists(id);

    // 校验权限：只有评分人可以删除
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    if (!existing.getRaterId().equals(currentUserId)) {
      throw new RuntimeException("只能删除自己的评分");
    }

    // 删除
    oaUserRatingMapper.deleteById(id);
  }

  @Override
  public OaUserRatingDO getOaUserRating(Long id) {
    return oaUserRatingMapper.selectById(id);
  }

  @Override
  public PageResult<OaUserRatingDO> getOaUserRatingPage(OaUserRatingPageReqVO pageReqVO) {
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    // 强制使用当前月份（只能查看当月数据）
    String currentMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    pageReqVO.setRatingMonth(currentMonth);

    // 判断是否是部门负责人
    boolean isDeptHead = isDeptHead(currentUserId, currentDeptId);

    // 权限控制
    if (isDeptHead) {
      // 部门负责人：可以看到自己给所有人打分的数据
      pageReqVO.setRaterId(currentUserId);
    } else {
      // 普通成员：只能看到自己被打分的数据
      pageReqVO.setRatedUserId(currentUserId);
    }

    return oaUserRatingMapper.selectPage(pageReqVO);
  }

  @Override
  public List<OaUserRatingDO> getOaUserRatingList(OaUserRatingExportReqVO exportReqVO) {
    return oaUserRatingMapper.selectList(exportReqVO);
  }

  @Override
  public List<Map<String, Object>> getRateableUsers() {
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    // 判断是否是部门负责人
    boolean isDeptHead = isDeptHead(currentUserId, currentDeptId);

    // 获取当前月份
    String currentMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

    // 获取部门信息
    DeptRespDTO dept = deptApi.getDept(currentDeptId);
    if (dept == null) {
      return Collections.emptyList();
    }

    // 获取部门所有成员
    List<AdminUserRespDTO> deptUsers = adminUserApi.getUserListByDeptIds(Collections.singletonList(currentDeptId));

    // 获取已评分的人员ID列表
    List<OaUserRatingDO> existingRatings = oaUserRatingMapper.selectList(
        new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<OaUserRatingDO>()
            .eq(OaUserRatingDO::getRaterId, currentUserId)
            .eq(OaUserRatingDO::getRatingMonth, currentMonth));

    Set<Long> ratedUserIds = existingRatings.stream()
        .map(OaUserRatingDO::getRatedUserId)
        .collect(Collectors.toSet());

    // 构建可评分人员列表
    List<Map<String, Object>> rateableUsers = new ArrayList<>();
    for (AdminUserRespDTO user : deptUsers) {
      // 不能给自己打分
      if (user.getId().equals(currentUserId)) {
        continue;
      }

      // 部门成员不能给部门负责人打分
      if (!isDeptHead) {
        if (dept.getLeaderUserId() != null && dept.getLeaderUserId().equals(user.getId())) {
          continue;
        }
      }

      // 构建用户信息
      Map<String, Object> userInfo = new HashMap<>();
      userInfo.put("id", user.getId());
      userInfo.put("name", user.getNickname());
      userInfo.put("deptId", user.getDeptId());
      userInfo.put("deptName", dept.getName());
      userInfo.put("isRated", ratedUserIds.contains(user.getId()));
      userInfo.put("maxScore", isDeptHead ? 12 : 8);

      // 如果已评分，获取评分信息
      if (ratedUserIds.contains(user.getId())) {
        OaUserRatingDO rating = existingRatings.stream()
            .filter(r -> r.getRatedUserId().equals(user.getId()))
            .findFirst()
            .orElse(null);
        if (rating != null) {
          userInfo.put("ratingId", rating.getId());
          userInfo.put("score", rating.getScore());
          userInfo.put("comment", rating.getComment());
        }
      }

      rateableUsers.add(userInfo);
    }

    return rateableUsers;
  }

  private OaUserRatingDO validateOaUserRatingExists(Long id) {
    OaUserRatingDO rating = oaUserRatingMapper.selectById(id);
    if (rating == null) {
      throw exception(OA_USER_RATING_NOT_EXISTS);
    }
    return rating;
  }

  /**
   * 校验评分权限
   */
  private void validateRatingPermission(Long raterId, Long raterDeptId, Long ratedUserId, BigDecimal score) {
    // 不能给自己打分
    if (raterId.equals(ratedUserId)) {
      throw new RuntimeException("不能给自己打分");
    }

    // 获取被评分人信息
    AdminUserRespDTO ratedUser = adminUserApi.getUser(ratedUserId);
    if (ratedUser == null) {
      throw new RuntimeException("被评分人不存在");
    }

    // 判断评分人是否是部门负责人
    boolean isDeptHead = isDeptHead(raterId, raterDeptId);

    // 部门成员不能给部门负责人打分
    if (!isDeptHead) {
      DeptRespDTO dept = deptApi.getDept(raterDeptId);
      if (dept != null && dept.getLeaderUserId() != null && dept.getLeaderUserId().equals(ratedUserId)) {
        throw new RuntimeException("部门成员不能给部门负责人打分");
      }
    }

    // 校验必须是同部门
    if (!raterDeptId.equals(ratedUser.getDeptId())) {
      throw new RuntimeException("只能给同部门成员打分");
    }

    // 校验评分范围
    validateScoreRange(raterId, raterDeptId, score);
  }

  /**
   * 校验评分范围
   */
  private void validateScoreRange(Long raterId, Long raterDeptId, BigDecimal score) {
    boolean isDeptHead = isDeptHead(raterId, raterDeptId);
    int maxScore = isDeptHead ? 12 : 8;

    if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal(maxScore)) > 0) {
      throw new RuntimeException(String.format("评分范围：0-%d分", maxScore));
    }
  }

  /**
   * 判断是否是部门负责人
   */
  private boolean isDeptHead(Long userId, Long deptId) {
    if (deptId == null) {
      return false;
    }
    DeptRespDTO dept = deptApi.getDept(deptId);
    return dept != null && dept.getLeaderUserId() != null && dept.getLeaderUserId().equals(userId);
  }
}
