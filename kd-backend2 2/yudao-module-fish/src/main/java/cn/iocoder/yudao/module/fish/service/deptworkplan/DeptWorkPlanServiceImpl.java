package cn.iocoder.yudao.module.fish.service.deptworkplan;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.fish.controller.admin.deptworkplan.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.deptworkplan.DeptWorkPlanDO;
import cn.iocoder.yudao.module.fish.dal.mysql.deptworkplan.DeptWorkPlanMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.DEPT_WORK_PLAN_NOT_EXISTS;

/**
 * 部门工作计划 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class DeptWorkPlanServiceImpl implements DeptWorkPlanService {

  @Resource
  private DeptWorkPlanMapper deptWorkPlanMapper;

  @Resource
  private AdminUserApi adminUserApi;

  @Resource
  private DeptApi deptApi;

  @Override
  public Long createDeptWorkPlan(DeptWorkPlanCreateReqVO createReqVO) {
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    String userName = SecurityFrameworkUtils.getLoginUserNickname();

    // 获取部门名称
    String deptName = null;
    if (currentDeptId != null) {
      try {
        DeptRespDTO dept = deptApi.getDept(currentDeptId);
        if (dept != null) {
          deptName = dept.getName();
        }
      } catch (Exception e) {
        log.warn("获取部门信息失败: {}", e.getMessage());
      }
    }

    // 插入
    DeptWorkPlanDO plan = BeanUtils.toBean(createReqVO, DeptWorkPlanDO.class);
    plan.setUserId(currentUserId);
    plan.setUserName(userName != null ? userName : "未知用户");
    plan.setDeptId(currentDeptId);
    plan.setDeptName(deptName);

    // 设置提交日期
    plan.setSubmitDate(LocalDateTime.now());

    deptWorkPlanMapper.insert(plan);
    // 返回
    return plan.getId();
  }

  @Override
  public void updateDeptWorkPlan(DeptWorkPlanUpdateReqVO updateReqVO) {
    // 校验存在
    DeptWorkPlanDO plan = validateDeptWorkPlanExists(updateReqVO.getId());

    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    // 创建者 / 部门负责人 / 超管 可修改
    boolean isOwner = plan.getUserId() != null && plan.getUserId().equals(currentUserId);
    boolean isManager = isDeptManager(currentUserId, currentDeptId);
    if (!isOwner && !isManager && !isSuperAdmin(currentUserId)) {
      throw new RuntimeException("无权限修改该工作计划");
    }

    // 非超管时：负责人仅可改本部门；本人可改自己的
    if (!isSuperAdmin(currentUserId) && isManager && !isOwner) {
      if (plan.getDeptId() == null || !plan.getDeptId().equals(currentDeptId)) {
        throw new RuntimeException("只能修改本部门的工作计划");
      }
    }

    // 更新
    DeptWorkPlanDO updateObj = BeanUtils.toBean(updateReqVO, DeptWorkPlanDO.class);
    deptWorkPlanMapper.updateById(updateObj);
  }

  @Override
  public void deleteDeptWorkPlan(Long id) {
    // 校验存在
    DeptWorkPlanDO plan = validateDeptWorkPlanExists(id);

    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    // 创建者 / 部门负责人 / 超管 可删除
    boolean isOwner = plan.getUserId() != null && plan.getUserId().equals(currentUserId);
    boolean isManager = isDeptManager(currentUserId, currentDeptId);
    if (!isOwner && !isManager && !isSuperAdmin(currentUserId)) {
      throw new RuntimeException("无权限删除该工作计划");
    }

    if (!isSuperAdmin(currentUserId) && isManager && !isOwner) {
      if (plan.getDeptId() == null || !plan.getDeptId().equals(currentDeptId)) {
        throw new RuntimeException("只能删除本部门的工作计划");
      }
    }

    // 删除
    deptWorkPlanMapper.deleteById(id);
  }

  private DeptWorkPlanDO validateDeptWorkPlanExists(Long id) {
    DeptWorkPlanDO plan = deptWorkPlanMapper.selectById(id);
    if (plan == null) {
      throw exception(DEPT_WORK_PLAN_NOT_EXISTS);
    }
    return plan;
  }

  @Override
  public DeptWorkPlanDO getDeptWorkPlan(Long id) {
    DeptWorkPlanDO plan = deptWorkPlanMapper.selectById(id);

    // 权限检查
    if (plan != null) {
      Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
      Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

      boolean isManager = isDeptManager(currentUserId, currentDeptId);

      if (!isManager) {
        // 员工：只能查看本部门的数据
        if (plan.getDeptId() == null || !plan.getDeptId().equals(currentDeptId)) {
          throw new RuntimeException("无权限查看该工作计划");
        }
      } else {
        // 部门负责人：可以查看本部门 + 父部门的计划
        if (plan.getDeptId() != null && !plan.getDeptId().equals(currentDeptId)) {
          // 检查是否是父部门的计划
          try {
            DeptRespDTO currentDept = deptApi.getDept(currentDeptId);
            if (currentDept == null || currentDept.getParentId() == null
                || !currentDept.getParentId().equals(plan.getDeptId())) {
              throw new RuntimeException("无权限查看该工作计划");
            }
          } catch (Exception e) {
            throw new RuntimeException("无权限查看该工作计划");
          }
        }
      }
    }

    return plan;
  }

  @Override
  public List<DeptWorkPlanDO> getDeptWorkPlanList(Collection<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    List<DeptWorkPlanDO> list = deptWorkPlanMapper.selectList(new LambdaQueryWrapperX<DeptWorkPlanDO>()
        .in(DeptWorkPlanDO::getId, ids));

    // 权限过滤
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    boolean isManager = isDeptManager(currentUserId, currentDeptId);

    if (!isManager) {
      // 员工：只能查看本部门的数据
      list = list.stream()
          .filter(plan -> plan.getDeptId() != null && plan.getDeptId().equals(currentDeptId))
          .collect(java.util.stream.Collectors.toList());
    } else {
      // 部门负责人：可以查看本部门 + 父部门的计划
      try {
        DeptRespDTO currentDept = deptApi.getDept(currentDeptId);
        Long parentDeptId = currentDept != null ? currentDept.getParentId() : null;

        final Long finalParentDeptId = parentDeptId;
        list = list.stream()
            .filter(plan -> {
              if (plan.getDeptId() == null) {
                return false;
              }
              // 本部门的计划
              if (plan.getDeptId().equals(currentDeptId)) {
                return true;
              }
              // 父部门的计划
              if (finalParentDeptId != null && plan.getDeptId().equals(finalParentDeptId)) {
                return true;
              }
              return false;
            })
            .collect(java.util.stream.Collectors.toList());
      } catch (Exception e) {
        log.warn("获取部门信息失败，只返回本部门数据: {}", e.getMessage());
        list = list.stream()
            .filter(plan -> plan.getDeptId() != null && plan.getDeptId().equals(currentDeptId))
            .collect(java.util.stream.Collectors.toList());
      }
    }

    return list;
  }

  @Override
  public PageResult<DeptWorkPlanDO> getDeptWorkPlanPage(DeptWorkPlanPageReqVO pageReqVO) {
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    boolean isManager = isDeptManager(currentUserId, currentDeptId);

    if (!isManager) {
      // 员工：只能查看本部门的数据
      pageReqVO.setDeptId(currentDeptId);
    } else {
      // 部门负责人：可以查看本部门 + 父部门的计划
      // 如果pageReqVO中没有设置deptId，需要查询本部门和父部门
      if (pageReqVO.getDeptId() == null) {
        try {
          DeptRespDTO currentDept = deptApi.getDept(currentDeptId);
          Long parentDeptId = currentDept != null ? currentDept.getParentId() : null;

          // 查询本部门和父部门的计划
          List<Long> deptIds = new java.util.ArrayList<>();
          deptIds.add(currentDeptId);
          if (parentDeptId != null) {
            deptIds.add(parentDeptId);
          }
          return deptWorkPlanMapper.selectPage(pageReqVO,
              new LambdaQueryWrapperX<DeptWorkPlanDO>()
                  .likeIfPresent(DeptWorkPlanDO::getTitle, pageReqVO.getTitle())
                  .eqIfPresent(DeptWorkPlanDO::getType, pageReqVO.getType())
                  .eqIfPresent(DeptWorkPlanDO::getUserId, pageReqVO.getUserId())
                  .in(DeptWorkPlanDO::getDeptId, deptIds)
                  .betweenIfPresent(DeptWorkPlanDO::getCreateTime, pageReqVO.getCreateTime())
                  .betweenIfPresent(DeptWorkPlanDO::getSubmitDate, pageReqVO.getSubmitDate())
                  .orderByDesc(DeptWorkPlanDO::getSubmitDate)
                  .orderByDesc(DeptWorkPlanDO::getId));
        } catch (Exception e) {
          log.warn("获取部门信息失败，只查询本部门数据: {}", e.getMessage());
          pageReqVO.setDeptId(currentDeptId);
        }
      }
    }

    return deptWorkPlanMapper.selectPage(pageReqVO);
  }

  @Override
  public List<DeptWorkPlanDO> getDeptWorkPlanList(DeptWorkPlanExportReqVO exportReqVO) {
    Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
    Long currentDeptId = SecurityFrameworkUtils.getLoginUserDeptId();

    boolean isManager = isDeptManager(currentUserId, currentDeptId);

    if (!isManager) {
      // 员工：只能查看本部门的数据
      exportReqVO.setDeptId(currentDeptId);
    } else {
      // 部门负责人：可以查看本部门 + 父部门的计划
      if (exportReqVO.getDeptId() == null) {
        try {
          DeptRespDTO currentDept = deptApi.getDept(currentDeptId);
          Long parentDeptId = currentDept != null ? currentDept.getParentId() : null;

          // 查询本部门和父部门的计划
          List<Long> deptIds = new java.util.ArrayList<>();
          deptIds.add(currentDeptId);
          if (parentDeptId != null) {
            deptIds.add(parentDeptId);
          }
          return deptWorkPlanMapper.selectList(new LambdaQueryWrapperX<DeptWorkPlanDO>()
              .likeIfPresent(DeptWorkPlanDO::getTitle, exportReqVO.getTitle())
              .eqIfPresent(DeptWorkPlanDO::getType, exportReqVO.getType())
              .eqIfPresent(DeptWorkPlanDO::getUserId, exportReqVO.getUserId())
              .in(DeptWorkPlanDO::getDeptId, deptIds)
              .betweenIfPresent(DeptWorkPlanDO::getCreateTime, exportReqVO.getCreateTime())
              .betweenIfPresent(DeptWorkPlanDO::getSubmitDate, exportReqVO.getSubmitDate())
              .orderByDesc(DeptWorkPlanDO::getSubmitDate)
              .orderByDesc(DeptWorkPlanDO::getId));
        } catch (Exception e) {
          log.warn("获取部门信息失败，只查询本部门数据: {}", e.getMessage());
          exportReqVO.setDeptId(currentDeptId);
        }
      }
    }

    return deptWorkPlanMapper.selectList(exportReqVO);
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

  /** 超管 / 租户管理员 */
  private boolean isSuperAdmin(Long userId) {
    try {
      if (userId == null) {
        return false;
      }
      AdminUserRespDTO user = adminUserApi.getUser(userId);
      if (user == null || user.getRoleCodes() == null) {
        return false;
      }
      String codes = user.getRoleCodes().toLowerCase();
      return codes.contains("admin")
          || codes.contains("super_admin")
          || codes.contains("super_administrator")
          || codes.contains("tenant_admin");
    } catch (Exception e) {
      log.warn("判断超管失败: {}", e.getMessage());
      return false;
    }
  }
}
