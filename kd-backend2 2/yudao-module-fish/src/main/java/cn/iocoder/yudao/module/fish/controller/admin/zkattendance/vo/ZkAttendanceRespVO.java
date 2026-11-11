package cn.iocoder.yudao.module.fish.controller.admin.zkattendance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 考勤 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkAttendanceRespVO extends ZkAttendanceBaseVO {

    @Schema(description = "编号", required = true, example = "1")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", required = true)
    private LocalDateTime updateTime;
}





