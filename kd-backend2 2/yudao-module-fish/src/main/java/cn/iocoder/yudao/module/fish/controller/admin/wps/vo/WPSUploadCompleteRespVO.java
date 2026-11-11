package cn.iocoder.yudao.module.fish.controller.admin.wps.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * WPS 上传完成 Response VO
 * 对应接口：POST /v3/3rd/files/{file_id}/upload/complete
 */
@Schema(description = "WPS - 上传完成 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WPSUploadCompleteRespVO {

    @Schema(description = "文档ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String file_id;

    @Schema(description = "文档下载地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com/files/1")
    private String download_url;
}



