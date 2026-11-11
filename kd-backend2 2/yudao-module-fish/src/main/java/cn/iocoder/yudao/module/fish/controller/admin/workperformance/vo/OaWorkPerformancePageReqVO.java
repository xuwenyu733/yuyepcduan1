package cn.iocoder.yudao.module.fish.controller.admin.workperformance.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - OA工作绩效评分分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OaWorkPerformancePageReqVO extends PageParam {

    @Schema(description = "评分人ID", example = "6144")
    private Long raterId;

    @Schema(description = "被评分人ID", example = "6145")
    private Long ratedUserId;

    @Schema(description = "评分月份（格式：yyyy-MM）", example = "2024-12")
    private String ratingMonth;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}



