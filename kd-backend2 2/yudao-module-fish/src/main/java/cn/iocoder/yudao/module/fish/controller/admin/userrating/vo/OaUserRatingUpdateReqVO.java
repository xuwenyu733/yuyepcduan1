package cn.iocoder.yudao.module.fish.controller.admin.userrating.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;

/**
 * OA他人打分更新 Request VO
 */
@Schema(description = "管理后台 - OA他人打分更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OaUserRatingUpdateReqVO extends OaUserRatingBaseVO {

  @Schema(description = "编号", required = true, example = "1")
  @NotNull(message = "编号不能为空")
  private Long id;

  @Schema(description = "评分（部门成员8分，负责人12分）", required = true, example = "8.5")
  @NotNull(message = "评分不能为空")
  @DecimalMin(value = "0", message = "评分不能小于0")
  @DecimalMax(value = "12", message = "评分不能大于12")
  private java.math.BigDecimal score;

  @Schema(description = "评分说明", example = "工作认真负责")
  private String comment;

}


