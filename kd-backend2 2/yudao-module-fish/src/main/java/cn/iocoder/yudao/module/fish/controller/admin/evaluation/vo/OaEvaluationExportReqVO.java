package cn.iocoder.yudao.module.fish.controller.admin.evaluation.vo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - OA考核评分导出 Request VO")
@Data
public class OaEvaluationExportReqVO {

    @Schema(description = "用户ID", example = "6144")
    private Long userId;

    @Schema(description = "部门ID", example = "13840")
    private Long deptId;

    @Schema(description = "考核期（格式：yyyy-MM）", example = "2024-12")
    private String evaluationPeriod;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}



