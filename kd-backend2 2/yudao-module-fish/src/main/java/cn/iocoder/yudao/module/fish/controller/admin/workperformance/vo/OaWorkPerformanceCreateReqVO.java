package cn.iocoder.yudao.module.fish.controller.admin.workperformance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;

/**
 * OA工作绩效评分创建 Request VO
 */
@Schema(description = "管理后台 - OA工作绩效评分创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OaWorkPerformanceCreateReqVO extends OaWorkPerformanceBaseVO {

    @Schema(description = "被评分人ID", required = true, example = "6145")
    @NotNull(message = "被评分人ID不能为空")
    private Long ratedUserId;

    @Schema(description = "水质合格率评分（0-6分）", example = "5.5")
    @DecimalMin(value = "0", message = "水质合格率评分不能小于0")
    @DecimalMax(value = "6", message = "水质合格率评分不能大于6")
    private java.math.BigDecimal waterQualityScore;

    @Schema(description = "设备故障率评分（0-6分）", example = "5.0")
    @DecimalMin(value = "0", message = "设备故障率评分不能小于0")
    @DecimalMax(value = "6", message = "设备故障率评分不能大于6")
    private java.math.BigDecimal equipmentFailureScore;

    @Schema(description = "存活率评分（0-14分）", example = "12.0")
    @DecimalMin(value = "0", message = "存活率评分不能小于0")
    @DecimalMax(value = "14", message = "存活率评分不能大于14")
    private java.math.BigDecimal survivalRateScore;

    @Schema(description = "饵料系数评分（0-4分）", example = "3.5")
    @DecimalMin(value = "0", message = "饵料系数评分不能小于0")
    @DecimalMax(value = "4", message = "饵料系数评分不能大于4")
    private java.math.BigDecimal feedCoefficientScore;

    @Schema(description = "评分说明", example = "工作表现优秀")
    private String comment;

    @Schema(description = "评分月份（格式：yyyy-MM）", example = "2024-12")
    private String ratingMonth;

}



