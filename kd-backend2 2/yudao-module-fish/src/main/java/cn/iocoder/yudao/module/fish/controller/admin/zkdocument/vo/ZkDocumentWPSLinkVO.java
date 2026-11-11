package cn.iocoder.yudao.module.fish.controller.admin.zkdocument.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "管理后台 - WPS WebOffice SDK配置 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkDocumentWPSLinkVO {

    @Schema(description = "WPS编辑链接（已废弃，WebOffice SDK不使用）", example = "")
    @Deprecated
    private String editUrl;

    @Schema(description = "WPS查看链接（已废弃，WebOffice SDK不使用）", example = "")
    @Deprecated
    private String viewUrl;

    @Schema(description = "WPS链接（用于uni-app web-view打开）", example = "https://weboffice.wps.cn/office/?app_id=xxx&file_id=xxx&token=xxx")
    private String url;

    @Schema(description = "WPS访问令牌（已废弃，WebOffice SDK不需要token）", example = "")
    @Deprecated
    private String token;

    @Schema(description = "文件ID（WebOffice SDK必需）", example = "123", required = true)
    private String fileId;
}

