package cn.iocoder.yudao.module.fish.controller.admin.oaattendancesetting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * OA考勤设置更新请求 VO
 */
@Schema(description = "管理后台 - OA考勤设置更新请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OaAttendanceSettingUpdateReqVO extends OaAttendanceSettingBaseVO {

    @Schema(description = "编号", required = true, example = "1")
    @NotNull(message = "编号不能为空")
    private Long id;

    @Schema(description = "地点名称", required = true, example = "公司总部")
    @NotNull(message = "地点名称不能为空")
    private String name;

    @Schema(description = "经度", required = true, example = "116.397428")
    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;

    @Schema(description = "纬度", required = true, example = "39.90923")
    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    @Schema(description = "允许打卡范围（米）", required = true, example = "100")
    @NotNull(message = "允许打卡范围不能为空")
    private Integer allowRange;
}



