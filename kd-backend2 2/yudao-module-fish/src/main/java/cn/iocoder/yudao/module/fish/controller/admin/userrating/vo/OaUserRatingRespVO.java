package cn.iocoder.yudao.module.fish.controller.admin.userrating.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - OA他人打分 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OaUserRatingRespVO extends OaUserRatingBaseVO {

  @Schema(description = "编号", required = true, example = "1")
  private Long id;

  @Schema(description = "创建时间", required = true)
  private LocalDateTime createTime;

  @Schema(description = "更新时间")
  private LocalDateTime updateTime;

  @Schema(description = "创建者")
  private String creator;

  @Schema(description = "更新者")
  private String updater;

}


