package cn.iocoder.yudao.module.fish.dal.dataobject.oaworkplan;

import lombok.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * OA工作计划 DO
 *
 * @author 系统管理员
 */
@TableName("oa_work_plan")
@KeySequence("oa_work_plan_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OaWorkPlanDO extends BaseDO {

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
     * 工作内容（周计划为JSON格式，其他为文本）
     */
    private String content;
    
    /**
     * 状态：pending-待审核, approved-已批准, completed-已完成, draft-草稿, rejected-已拒绝
     */
    private String status;
    
    /**
     * 完成度（百分比，0-100）
     */
    private Integer completionRate;
    
    /**
     * 质量评分（0-5分）
     */
    private Double qualityScore;
    
    /**
     * 完成情况：completed-已完成, uncompleted-未完成
     */
    private String completionStatus;
    
    /**
     * 完成说明
     */
    private String completionComment;
    
    /**
     * 完成质量评价
     */
    private String qualityEvaluation;
    
    /**
     * 成本与效率评价
     */
    private String costEfficiencyEvaluation;
    
    /**
     * 评价打分（0-25分）
     */
    private Integer evaluationScore;
    
    /**
     * 评价人ID
     */
    private Long evaluatorId;
    
    /**
     * 评价人姓名
     */
    private String evaluatorName;
    
    /**
     * 评价时间
     */
    private LocalDateTime evaluationTime;
    
    /**
     * 提交日期
     */
    private LocalDateTime submitDate;
    
    /**
     * 审核人ID
     */
    private Long reviewerId;
    
    /**
     * 审核人姓名
     */
    private String reviewerName;
    
    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;
    
    /**
     * 审核意见
     */
    private String reviewComment;
    
    /**
     * 附件（JSON数组，存储附件URL和名称）
     */
    private String attachments;
    
    /**
     * 创建者ID（负责人）
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

