package cn.iocoder.yudao.module.fish.dal.dataobject.userrating;

import lombok.*;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * OA他人打分 DO
 *
 * @author 系统管理员
 */
@TableName("oa_user_rating")
@KeySequence("oa_user_rating_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OaUserRatingDO extends BaseDO {

  /**
   * 编号
   */
  @TableId
  private Long id;

  /**
   * 评分人ID
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
   * 评分（部门成员8分，负责人12分）
   */
  private BigDecimal score;

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


