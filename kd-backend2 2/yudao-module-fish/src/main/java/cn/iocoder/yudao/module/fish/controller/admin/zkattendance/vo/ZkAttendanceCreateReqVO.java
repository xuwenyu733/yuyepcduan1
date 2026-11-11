package cn.iocoder.yudao.module.fish.controller.admin.zkattendance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.NotNull;

/**
 * 考勤创建请求 VO
 */
@Schema(description = "管理后台 - 考勤创建请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkAttendanceCreateReqVO extends ZkAttendanceBaseVO {

    @Schema(description = "打卡类型(1:上班打卡;2:下班打卡)", required = true, example = "1")
    @NotNull(message = "打卡类型不能为空")
    private Integer punchType;
}





