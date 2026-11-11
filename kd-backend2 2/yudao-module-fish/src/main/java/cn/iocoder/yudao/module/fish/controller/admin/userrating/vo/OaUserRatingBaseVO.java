package cn.iocoder.yudao.module.fish.controller.admin.userrating.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

/**
 * OA他人打分 Base VO，提供给添加、修改、详细的子 VO 使用
 */
@Data
public class OaUserRatingBaseVO {

  @Schema(description = "评分人ID", example = "6144")
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

  @Schema(description = "评分（部门成员8分，负责人12分）", example = "8.5")
  private BigDecimal score;

  @Schema(description = "评分说明", example = "工作认真负责")
  private String comment;

  @Schema(description = "评分月份（格式：yyyy-MM）", example = "2024-12")
  private String ratingMonth;

}


