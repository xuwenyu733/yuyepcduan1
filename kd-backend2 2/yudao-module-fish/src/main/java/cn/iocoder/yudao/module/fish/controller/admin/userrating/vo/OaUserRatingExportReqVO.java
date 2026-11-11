package cn.iocoder.yudao.module.fish.controller.admin.userrating.vo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - OA他人打分导出 Request VO")
@Data
public class OaUserRatingExportReqVO {

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


