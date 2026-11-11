package cn.iocoder.yudao.module.fish.controller.admin.workperformance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

/**
 * OA工作绩效评分 Base VO，提供给添加、修改、详细的子 VO 使用
 */
@Data
public class OaWorkPerformanceBaseVO {

    @Schema(description = "评分人ID（部门负责人）", example = "6144")
    private Long raterId;

    @Schema(description = "评分人姓名", example = "张三")
    private String raterName;

    @Schema(description = "评分人部门ID", example = "13840")
    private Long raterDeptId;

    @Schema(description = "被评分人ID", example = "6145")
    private Long ratedUserId;

    @Schema(description = "被评分人姓名", example = "李四")
    private String ratedUserName;

    @Schema(description = "被评分人部门ID", example = "13840")
    private Long ratedUserDeptId;

    @Schema(description = "水质合格率评分（0-6分）", example = "5.5")
    private BigDecimal waterQualityScore;

    @Schema(description = "设备故障率评分（0-6分）", example = "5.0")
    private BigDecimal equipmentFailureScore;

    @Schema(description = "存活率评分（0-14分）", example = "12.0")
    private BigDecimal survivalRateScore;

    @Schema(description = "饵料系数评分（0-4分）", example = "3.5")
    private BigDecimal feedCoefficientScore;

    @Schema(description = "总分（0-30分）", example = "26.0")
    private BigDecimal totalScore;

    @Schema(description = "评分说明", example = "工作表现优秀")
    private String comment;

    @Schema(description = "评分月份（格式：yyyy-MM）", example = "2024-12")
    private String ratingMonth;

}



