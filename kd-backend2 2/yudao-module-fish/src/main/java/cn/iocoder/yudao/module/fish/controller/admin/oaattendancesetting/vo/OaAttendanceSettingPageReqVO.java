package cn.iocoder.yudao.module.fish.controller.admin.oaattendancesetting.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

@Schema(description = "管理后台 - OA考勤设置分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OaAttendanceSettingPageReqVO extends PageParam {

    @Schema(description = "地点名称", example = "公司总部")
    private String name;

    @Schema(description = "状态(0:禁用;1:启用)", example = "1")
    private Integer status;
}



