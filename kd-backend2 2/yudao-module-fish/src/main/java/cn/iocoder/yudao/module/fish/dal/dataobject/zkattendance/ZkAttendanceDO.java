package cn.iocoder.yudao.module.fish.dal.dataobject.zkattendance;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 考勤 DO
 *
 * @author 系统管理员
 */
@TableName("zk_attendance")
@KeySequence("zk_attendance_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkAttendanceDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 考勤日期
     */
    private LocalDate attendanceDate;

    /**
     * 上班打卡时间
     */
    private LocalTime clockInTime;

    /**
     * 下班打卡时间
     */
    private LocalTime clockOutTime;

    /**
     * 状态(0:正常;1:迟到;2:早退;3:缺勤;4:迟到+早退)
     */
    private String status;

    /**
     * 打卡地点
     */
    private String location;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 租户ID
     */
    private Long tenantId;
}





