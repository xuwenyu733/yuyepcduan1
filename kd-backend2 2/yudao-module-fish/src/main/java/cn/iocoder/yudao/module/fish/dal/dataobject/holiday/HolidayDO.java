package cn.iocoder.yudao.module.fish.dal.dataobject.holiday;

import lombok.*;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 节假日 DO
 *
 * @author 系统管理员
 */
@TableName("oa_holiday")
@KeySequence("oa_holiday_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HolidayDO extends BaseDO {

  /**
   * 编号
   */
  @TableId
  private Long id;

  /**
   * 日期
   */
  private LocalDate holidayDate;

  /**
   * 日期类型：0=工作日、1=节假日、2=双休日、3=调休日
   */
  private Integer daycode;

  /**
   * 是否工作：0=工作、1=休息
   */
  private Integer isnotwork;

  /**
   * 年份
   */
  private Integer year;

  /**
   * 租户ID
   */
  private Long tenantId;
}

