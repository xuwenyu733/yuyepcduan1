package cn.iocoder.yudao.module.fish.controller.admin.oaattendancesetting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * OA考勤设置 Base VO，提供给添加、修改、详细的子 VO 使用
 */
@Data
public class OaAttendanceSettingBaseVO {

    @Schema(description = "地点名称", example = "公司总部")
    private String name;

    @Schema(description = "详细地址", example = "北京市朝阳区xxx街道xxx号")
    private String address;

    @Schema(description = "经度", example = "116.397428")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "39.90923")
    private BigDecimal latitude;

    @Schema(description = "允许打卡范围（米）", example = "100")
    private Integer allowRange;

    @Schema(description = "上班时间", example = "09:00:00")
    private LocalTime workStartTime;

    @Schema(description = "下班时间", example = "18:00:00")
    private LocalTime workEndTime;

    @Schema(description = "状态(0:禁用;1:启用)", example = "1")
    private Integer status;

    @Schema(description = "排序", example = "0")
    private Integer sortOrder;
}



