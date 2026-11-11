package cn.iocoder.yudao.module.fish.controller.admin.wps.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

/**
 * WPS 获取上传地址 Response VO
 * 对应接口：POST /v3/3rd/files/{file_id}/upload/address
 */
@Schema(description = "WPS - 获取上传地址 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WPSUploadAddressRespVO {

    @Schema(description = "上传文档的URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://oss.example.com/upload")
    private String url;

    @Schema(description = "上传文档的HTTP Method，暂只支持PUT，文件实体将在Body传递", requiredMode = Schema.RequiredMode.REQUIRED, example = "PUT")
    private String method;

    @Schema(description = "上传文档时需要携带的额外请求头")
    private Map<String, String> headers;

    @Schema(description = "上传文档时需要携带的额外参数，PUT方式下在Query传递")
    private Map<String, String> params;

    @Schema(description = "上传文档后，请求完成上传接口需要原样带回的额外参数")
    private Map<String, String> send_back_params;
}



