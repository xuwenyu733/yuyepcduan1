package cn.iocoder.yudao.module.fish.controller.admin.oaattendancesetting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "管理后台 - OA考勤设置导出 Request VO")
@Data
public class OaAttendanceSettingExportReqVO {

    @Schema(description = "地点名称", example = "公司总部")
    private String name;

    @Schema(description = "状态(0:禁用;1:启用)", example = "1")
    private Integer status;
}



