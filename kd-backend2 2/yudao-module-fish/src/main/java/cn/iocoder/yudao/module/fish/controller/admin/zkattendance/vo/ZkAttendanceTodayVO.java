package cn.iocoder.yudao.module.fish.controller.admin.zkattendance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalTime;

@Schema(description = "管理后台 - 今日考勤 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkAttendanceTodayVO {

    @Schema(description = "是否已上班打卡", example = "true")
    private Boolean hasClockIn;

    @Schema(description = "是否已下班打卡", example = "false")
    private Boolean hasClockOut;

    @Schema(description = "上班打卡时间")
    private LocalTime clockInTime;

    @Schema(description = "下班打卡时间")
    private LocalTime clockOutTime;

    @Schema(description = "打卡地点", example = "公司办公区")
    private String location;
}





