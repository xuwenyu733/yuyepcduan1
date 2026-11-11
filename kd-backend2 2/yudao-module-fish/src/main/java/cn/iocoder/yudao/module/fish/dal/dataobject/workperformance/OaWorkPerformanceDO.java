package cn.iocoder.yudao.module.fish.dal.dataobject.workperformance;

import lombok.*;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * OA工作绩效评分 DO
 *
 * @author 系统管理员
 */
@TableName("oa_work_performance")
@KeySequence("oa_work_performance_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OaWorkPerformanceDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 评分人ID（部门负责人）
     */
    private Long raterId;

    /**
     * 评分人姓名
     */
    private String raterName;

    /**
     * 评分人部门ID
     */
    private Long raterDeptId;

    /**
     * 被评分人ID
     */
    private Long ratedUserId;

    /**
     * 被评分人姓名
     */
    private String ratedUserName;

    /**
     * 被评分人部门ID
     */
    private Long ratedUserDeptId;

    /**
     * 水质合格率评分（0-6分）
     */
    private BigDecimal waterQualityScore;

    /**
     * 设备故障率评分（0-6分）
     */
    private BigDecimal equipmentFailureScore;

    /**
     * 存活率评分（0-14分）
     */
    private BigDecimal survivalRateScore;

    /**
     * 饵料系数评分（0-4分）
     */
    private BigDecimal feedCoefficientScore;

    /**
     * 总分（0-30分）
     */
    private BigDecimal totalScore;

    /**
     * 评分说明
     */
    private String comment;

    /**
     * 评分月份（格式：yyyy-MM）
     */
    private String ratingMonth;

    /**
     * 租户ID
     */
    private Long tenantId;

}



