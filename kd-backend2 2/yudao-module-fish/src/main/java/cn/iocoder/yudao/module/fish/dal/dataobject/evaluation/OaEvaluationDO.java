package cn.iocoder.yudao.module.fish.dal.dataobject.evaluation;

import lombok.*;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * OA考核评分 DO
 *
 * @author 系统管理员
 */
@TableName("oa_evaluation")
@KeySequence("oa_evaluation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OaEvaluationDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 考核期（格式：yyyy-MM）
     */
    private String evaluationPeriod;

    /**
     * 工作计划得分（加权后）
     */
    private BigDecimal workPlanScore;

    /**
     * 考勤得分（加权后）
     */
    private BigDecimal attendanceScore;

    /**
     * 考试得分（加权后）
     */
    private BigDecimal examScore;

    /**
     * 他人评分得分（加权后）
     */
    private BigDecimal peerRatingScore;

    /**
     * 工作绩效得分（加权后）
     */
    private BigDecimal workPerformanceScore;

    /**
     * 总分（0-100分）
     */
    private BigDecimal totalScore;

    /**
     * 等级（A+, A, B+, B, C等）
     */
    private String grade;

    /**
     * 工作计划原始得分
     */
    private BigDecimal workPlanRawScore;

    /**
     * 考勤原始得分
     */
    private BigDecimal attendanceRawScore;

    /**
     * 考试原始得分
     */
    private BigDecimal examRawScore;

    /**
     * 实操考试得分（0-5，手工录入；理论来自试卷）
     */
    private BigDecimal practiceExamScore;

    /**
     * 他人评分原始得分
     */
    private BigDecimal peerRatingRawScore;

    /**
     * 工作绩效原始得分
     */
    private BigDecimal workPerformanceRawScore;

    /**
     * 租户ID
     */
    private Long tenantId;

}



