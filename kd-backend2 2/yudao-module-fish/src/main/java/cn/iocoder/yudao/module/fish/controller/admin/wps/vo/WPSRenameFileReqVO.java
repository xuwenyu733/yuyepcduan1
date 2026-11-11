package cn.iocoder.yudao.module.fish.controller.admin.wps.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * WPS 文档重命名 Request VO
 * 对应接口：PUT /v3/3rd/files/{file_id}/name
 */
@Schema(description = "WPS - 文档重命名 Request VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WPSRenameFileReqVO {

    @Schema(description = "新文档名称（包含扩展名）", requiredMode = Schema.RequiredMode.REQUIRED, example = "新文件名.xlsx")
    private String name;
}



