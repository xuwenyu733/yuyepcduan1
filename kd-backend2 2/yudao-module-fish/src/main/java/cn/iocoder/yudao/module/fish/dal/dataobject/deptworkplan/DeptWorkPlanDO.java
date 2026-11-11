package cn.iocoder.yudao.module.fish.dal.dataobject.deptworkplan;

import lombok.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 部门工作计划 DO
 *
 * @author 系统管理员
 */
@TableName("oa_dept_work_plan")
@KeySequence("oa_dept_work_plan_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptWorkPlanDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    
    /**
     * 计划类型：weekly-周计划, monthly-月计划, quarterly-季度计划, annual-年度计划
     */
    private String type;
    
    /**
     * 计划标题
     */
    private String title;
    
    /**
     * 计划周期（如：2024-12-16 至 2024-12-22）
     */
    private String period;
    
    /**
     * 工作内容（周计划为文本，不需要每天设置）
     */
    private String content;
    
    /**
     * 完成度（百分比，0-100）
     */
    private Integer completionRate;
    
    /**
     * 质量评分（0-5分）
     */
    private Double qualityScore;
    
    /**
     * 提交日期
     */
    private LocalDateTime submitDate;
    
    /**
     * 附件（JSON数组，存储附件URL和名称）
     */
    private String attachments;
    
    /**
     * 创建者ID（部门负责人）
     */
    private Long userId;
    
    /**
     * 创建者姓名
     */
    private String userName;
    
    /**
     * 所属部门ID
     */
    private Long deptId;
    
    /**
     * 部门名称
     */
    private String deptName;
    
    /**
     * 租户ID
     */
    private Long tenantId;
}

