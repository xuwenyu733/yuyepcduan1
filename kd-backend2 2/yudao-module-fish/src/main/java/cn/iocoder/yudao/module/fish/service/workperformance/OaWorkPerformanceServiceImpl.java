package cn.iocoder.yudao.module.fish.service.workperformance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.fish.controller.admin.workperformance.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.workperformance.OaWorkPerformanceDO;
import cn.iocoder.yudao.module.fish.dal.mysql.workperformance.OaWorkPerformanceMapper;
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
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception0;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.OA_WORK_PERFORMANCE_NOT_EXISTS;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;

/**
 * OA工作绩效评分 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class OaWorkPerformanceServiceImpl implements OaWorkPerformanceService {

    @Resource
    private OaWorkPerformanceMapper oaWorkPerformanceMapper;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private DeptApi deptApi;

    @Override
    public Long createOaWorkPerformance(OaWorkPerformanceCreateReqVO createReqVO) {
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

        // 校验权限：只有部门负责人可以评分
        if (!isDeptHead(currentUserId, currentDeptId)) {
            throw exception0(403, "只有部门负责人可以对成员进行工作绩效评分");
        }

        // 获取当前月份
        String ratingMonth = createReqVO.getRatingMonth();
        if (ratingMonth == null || ratingMonth.isEmpty()) {
            ratingMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        // 检查是否已经评分过（同一评分人对同一被评分人在同一月份只能评分一次）
        OaWorkPerformanceDO existing = oaWorkPerformanceMapper.selectByRaterAndRatedAndMonth(
                currentUserId, createReqVO.getRatedUserId(), ratingMonth);
        if (existing != null) {
            throw exception0(400, "本月已对该用户评分，请使用修改功能");
        }

        // 获取评分人和被评分人信息
        AdminUserRespDTO rater = adminUserApi.getUser(currentUserId);
        AdminUserRespDTO ratedUser = adminUserApi.getUser(createReqVO.getRatedUserId());

        // 校验必须是同部门
        if (ratedUser == null || currentDeptId == null || !currentDeptId.equals(ratedUser.getDeptId())) {
            throw exception0(400, "只能对本部门成员进行工作绩效评分");
        }

        // 计算总分
        BigDecimal totalScore = BigDecimal.ZERO;
        if (createReqVO.getWaterQualityScore() != null) {
            totalScore = totalScore.add(createReqVO.getWaterQualityScore());
        }
        if (createReqVO.getEquipmentFailureScore() != null) {
            totalScore = totalScore.add(createReqVO.getEquipmentFailureScore());
        }
        if (createReqVO.getSurvivalRateScore() != null) {
            totalScore = totalScore.add(createReqVO.getSurvivalRateScore());
        }
        if (createReqVO.getFeedCoefficientScore() != null) {
            totalScore = totalScore.add(createReqVO.getFeedCoefficientScore());
        }

        // 创建
        OaWorkPerformanceDO performance = OaWorkPerformanceDO.builder()
                .raterId(currentUserId)
                .raterName(rater != null ? rater.getNickname() : "未知")
                .raterDeptId(currentDeptId)
                .ratedUserId(createReqVO.getRatedUserId())
                .ratedUserName(ratedUser != null ? ratedUser.getNickname() : "未知")
                .ratedUserDeptId(ratedUser != null ? ratedUser.getDeptId() : null)
                .waterQualityScore(createReqVO.getWaterQualityScore())
                .equipmentFailureScore(createReqVO.getEquipmentFailureScore())
                .survivalRateScore(createReqVO.getSurvivalRateScore())
                .feedCoefficientScore(createReqVO.getFeedCoefficientScore())
                .totalScore(totalScore)
                .comment(createReqVO.getComment())
                .ratingMonth(ratingMonth)
                .build();
        oaWorkPerformanceMapper.insert(performance);
        return performance.getId();
    }

    @Override
    public void updateOaWorkPerformance(OaWorkPerformanceUpdateReqVO updateReqVO) {
        // 校验存在
        OaWorkPerformanceDO existing = validateOaWorkPerformanceExists(updateReqVO.getId());

        // 校验权限：只有评分人可以修改
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (!existing.getRaterId().equals(currentUserId)) {
            throw new RuntimeException("只能修改自己的评分");
        }

        // 计算总分
        BigDecimal totalScore = BigDecimal.ZERO;
        if (updateReqVO.getWaterQualityScore() != null) {
            totalScore = totalScore.add(updateReqVO.getWaterQualityScore());
        }
        if (updateReqVO.getEquipmentFailureScore() != null) {
            totalScore = totalScore.add(updateReqVO.getEquipmentFailureScore());
        }
        if (updateReqVO.getSurvivalRateScore() != null) {
            totalScore = totalScore.add(updateReqVO.getSurvivalRateScore());
        }
        if (updateReqVO.getFeedCoefficientScore() != null) {
            totalScore = totalScore.add(updateReqVO.getFeedCoefficientScore());
        }

        // 更新
        OaWorkPerformanceDO updateObj = BeanUtils.toBean(updateReqVO, OaWorkPerformanceDO.class);
        updateObj.setTotalScore(totalScore);
        oaWorkPerformanceMapper.updateById(updateObj);
    }

    @Override
    public void deleteOaWorkPerformance(Long id) {
        // 校验存在
        OaWorkPerformanceDO existing = validateOaWorkPerformanceExists(id);

        // 校验权限：只有评分人可以删除
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (!existing.getRaterId().equals(currentUserId)) {
            throw new RuntimeException("只能删除自己的评分");
        }

        // 删除
        oaWorkPerformanceMapper.deleteById(id);
    }

    @Override
    public OaWorkPerformanceDO getOaWorkPerformance(Long id) {
        return oaWorkPerformanceMapper.selectById(id);
    }

    @Override
    public PageResult<OaWorkPerformanceDO> getOaWorkPerformancePage(OaWorkPerformancePageReqVO pageReqVO) {
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

        // 强制使用当前月份（只能查看当月数据）
        String currentMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        pageReqVO.setRatingMonth(currentMonth);

        // 非部门负责人：返回空列表（勿抛 RuntimeException，前端会显示「系统异常」）
        if (!isDeptHead(currentUserId, currentDeptId)) {
            return PageResult.empty();
        }

        // 部门负责人：可以看到自己给所有人打分的数据
        pageReqVO.setRaterId(currentUserId);

        return oaWorkPerformanceMapper.selectPage(pageReqVO);
    }

    @Override
    public List<OaWorkPerformanceDO> getOaWorkPerformanceList(OaWorkPerformanceExportReqVO exportReqVO) {
        return oaWorkPerformanceMapper.selectList(exportReqVO);
    }

    @Override
    public List<Map<String, Object>> getRateableUsers() {
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

        // 校验权限：只有部门负责人可以评分
        if (!isDeptHead(currentUserId, currentDeptId)) {
            return Collections.emptyList();
        }

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
        List<OaWorkPerformanceDO> existingRatings = oaWorkPerformanceMapper.selectList(
                new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<OaWorkPerformanceDO>()
                        .eq(OaWorkPerformanceDO::getRaterId, currentUserId)
                        .eq(OaWorkPerformanceDO::getRatingMonth, currentMonth));

        Set<Long> ratedUserIds = existingRatings.stream()
                .map(OaWorkPerformanceDO::getRatedUserId)
                .collect(Collectors.toSet());

        // 构建可评分人员列表
        List<Map<String, Object>> rateableUsers = new ArrayList<>();
        for (AdminUserRespDTO user : deptUsers) {
            // 不能给自己打分
            if (user.getId().equals(currentUserId)) {
                continue;
            }

            // 构建用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("name", user.getNickname());
            userInfo.put("deptId", user.getDeptId());
            userInfo.put("deptName", dept.getName());
            userInfo.put("isRated", ratedUserIds.contains(user.getId()));

            // 如果已评分，获取评分信息
            if (ratedUserIds.contains(user.getId())) {
                OaWorkPerformanceDO rating = existingRatings.stream()
                        .filter(r -> r.getRatedUserId().equals(user.getId()))
                        .findFirst()
                        .orElse(null);
                if (rating != null) {
                    userInfo.put("ratingId", rating.getId());
                    userInfo.put("totalScore", rating.getTotalScore());
                    userInfo.put("comment", rating.getComment());
                }
            }

            rateableUsers.add(userInfo);
        }

        return rateableUsers;
    }

    private OaWorkPerformanceDO validateOaWorkPerformanceExists(Long id) {
        OaWorkPerformanceDO performance = oaWorkPerformanceMapper.selectById(id);
        if (performance == null) {
            throw exception(OA_WORK_PERFORMANCE_NOT_EXISTS);
        }
        return performance;
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

