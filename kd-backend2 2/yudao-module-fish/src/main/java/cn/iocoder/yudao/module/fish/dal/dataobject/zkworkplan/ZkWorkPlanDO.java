package cn.iocoder.yudao.module.fish.dal.dataobject.zkworkplan;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 工作目标与计划 DO
 *
 * @author 系统管理员
 */
@TableName("zk_work_plan")
@KeySequence("zk_work_plan_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkWorkPlanDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 日计划
     */
    private String daylyPlan;
    /**
     * 周计划
     */
    private String weeklyPlan;
    /**
     * 月度目标
     */
    private String monthlyGoal;
    /**
     * 季度目标
     */
    private String quarterlyGoal;
    /**
     * 年度目标
     */
    private String annualGoal;
    /**
     * 负责人id
     */
    private Long userId;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;

}
