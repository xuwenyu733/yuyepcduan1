package cn.iocoder.yudao.module.fish.controller.admin.wps.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

/**
 * WPS 上传响应 VO
 * 用于上传完成接口中的response字段
 */
@Schema(description = "WPS - 上传响应 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WPSUploadResponseVO {

    @Schema(description = "上传文档时，存储服务返回的HTTP Response Status", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
    private Integer status_code;

    @Schema(description = "上传文档时，存储服务返回的HTTP Response Header")
    private Map<String, String> headers;

    @Schema(description = "上传文档时，存储服务返回的HTTP Response Body的base64编码")
    private String body;
}



