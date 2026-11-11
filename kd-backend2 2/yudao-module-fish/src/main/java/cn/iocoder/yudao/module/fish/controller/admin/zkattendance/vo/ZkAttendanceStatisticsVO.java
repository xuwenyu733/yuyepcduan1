package cn.iocoder.yudao.module.fish.controller.admin.zkattendance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "管理后台 - 考勤统计 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkAttendanceStatisticsVO {

    @Schema(description = "本月出勤天数", example = "18")
    private Integer attendanceDays;

    @Schema(description = "应出勤天数", example = "20")
    private Integer shouldAttendanceDays;

    @Schema(description = "迟到次数", example = "2")
    private Integer lateCount;

    @Schema(description = "早退次数", example = "1")
    private Integer earlyLeaveCount;

    @Schema(description = "缺勤次数", example = "1")
    private Integer absentCount;
}





