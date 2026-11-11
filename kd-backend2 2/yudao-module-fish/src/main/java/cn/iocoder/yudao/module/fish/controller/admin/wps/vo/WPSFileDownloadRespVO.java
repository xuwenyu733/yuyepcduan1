package cn.iocoder.yudao.module.fish.controller.admin.wps.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

/**
 * WPS 文件下载地址 Response VO
 * 对应接口：GET /v3/3rd/files/{file_id}/download
 * 
 * 根据WPS官方文档：
 * - url字段是必须的（必须: 是）
 * - 文档下载地址需确保外网可访问且排除访问时防火墙的限制
 * - 如果下载地址设置了Referer防盗链，需要在headers中返回自定义Referer请求头
 */
@Schema(description = "WPS - 文件下载 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WPSFileDownloadRespVO {

    @Schema(description = "文档下载地址（必须），该地址需确保外网可访问且排除访问时防火墙的限制", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com/files/1")
    private String url;

    @Schema(description = "文档内容（BASE64编码，可选，一般不使用）", example = "UEsDBBQAAAAI...")
    private String content;

    @Schema(description = "文档校验和(checksum)", example = "abc123def456")
    private String digest;

    @Schema(description = "文档校验和算法，md5或sha1", example = "md5")
    private String digest_type;

    @Schema(description = "请求文档下载地址所需要的额外请求头，例如某些云存储商会要求额外的签名头等")
    private Map<String, String> headers;
}

