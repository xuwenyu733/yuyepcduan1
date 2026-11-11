package cn.iocoder.yudao.module.fish.controller.admin.oaattendancesetting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OA考勤设置响应 VO
 */
@Schema(description = "管理后台 - OA考勤设置响应 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OaAttendanceSettingRespVO extends OaAttendanceSettingBaseVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}



