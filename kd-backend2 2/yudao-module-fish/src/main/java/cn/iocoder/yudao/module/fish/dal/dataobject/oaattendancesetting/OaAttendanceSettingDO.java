package cn.iocoder.yudao.module.fish.dal.dataobject.oaattendancesetting;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * OA考勤设置 DO
 *
 * @author 系统管理员
 */
@TableName("oa_attendance_setting")
@KeySequence("oa_attendance_setting_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OaAttendanceSettingDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    
    /**
     * 地点名称
     */
    private String name;
    
    /**
     * 详细地址
     */
    private String address;
    
    /**
     * 经度
     */
    private BigDecimal longitude;
    
    /**
     * 纬度
     */
    private BigDecimal latitude;
    
    /**
     * 允许打卡范围（米）
     */
    private Integer allowRange;
    
    /**
     * 上班时间
     */
    private LocalTime workStartTime;
    
    /**
     * 下班时间
     */
    private LocalTime workEndTime;
    
    /**
     * 状态(0:禁用;1:启用)
     */
    private Integer status;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 租户ID
     */
    private Long tenantId;
}



