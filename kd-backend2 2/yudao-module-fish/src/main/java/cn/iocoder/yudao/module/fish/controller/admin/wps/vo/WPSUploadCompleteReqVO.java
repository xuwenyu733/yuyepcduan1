package cn.iocoder.yudao.module.fish.controller.admin.wps.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

/**
 * WPS 上传完成 Request VO
 * 对应接口：POST /v3/3rd/files/{file_id}/upload/complete
 */
@Schema(description = "WPS - 上传完成 Request VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WPSUploadCompleteReqVO {

    @Schema(description = "获取上传地址时相同的请求", requiredMode = Schema.RequiredMode.REQUIRED)
    private WPSUploadAddressReqVO request;

    @Schema(description = "上传文档完成后，存储服务返回的HTTP Response", requiredMode = Schema.RequiredMode.REQUIRED)
    private WPSUploadResponseVO response;

    @Schema(description = "获取上传地址时，要求原样带回的额外参数")
    private Map<String, String> send_back_params;
}



