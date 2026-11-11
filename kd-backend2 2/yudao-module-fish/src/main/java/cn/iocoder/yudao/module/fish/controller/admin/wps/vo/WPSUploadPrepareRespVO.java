package cn.iocoder.yudao.module.fish.controller.admin.wps.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * WPS 准备上传 Response VO
 * 对应接口：GET /v3/3rd/files/{file_id}/upload/prepare
 */
@Schema(description = "WPS - 准备上传 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WPSUploadPrepareRespVO {

    @Schema(description = "文档校验和算法，支持：md5, sha1, sha256", example = "[\"sha1\"]")
    private List<String> digest_types;
}



