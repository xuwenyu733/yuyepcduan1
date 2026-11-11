package cn.iocoder.yudao.module.fish.controller.admin.zkattendance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

/**
 * 考勤 Base VO，提供给添加、修改、详细的子 VO 使用
 */
@Data
public class ZkAttendanceBaseVO {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "张三")
    private String userName;

    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    @Schema(description = "考勤日期", required = true)
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate attendanceDate;

    @Schema(description = "上班打卡时间")
    private LocalTime clockInTime;

    @Schema(description = "下班打卡时间")
    private LocalTime clockOutTime;

    @Schema(description = "状态(0:正常;1:迟到;2:早退;3:缺勤;4:迟到+早退)", example = "0")
    private String status;

    @Schema(description = "打卡地点", example = "公司办公区")
    private String location;

    @Schema(description = "经度", example = "116.397128")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "39.916527")
    private BigDecimal latitude;
}





