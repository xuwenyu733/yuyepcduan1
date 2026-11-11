package cn.iocoder.yudao.module.fish.controller.admin.wps.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * WPS 文件权限 Response VO
 * 对应接口：GET /v3/3rd/files/{file_id}/permission
 */
@Schema(description = "WPS - 文件权限 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WPSFilePermissionRespVO {

    @Schema(description = "当前用户Id，当update=1或print=1时必须返回当前用户的user_id", example = "user123")
    private String user_id;

    @Schema(description = "是否具有预览权限，0-无，1-有", example = "1")
    private Integer read;

    @Schema(description = "是否具有编辑权限，0-无，1-有", example = "1")
    private Integer update;

    @Schema(description = "是否具有下载文档权限，0-无，1-有", example = "1")
    private Integer download;

    @Schema(description = "是否具有重命名文档权限，0-无，1-有", example = "0")
    private Integer rename;

    @Schema(description = "是否具有查看文档历史记录权限，0-无，1-有", example = "0")
    private Integer history;

    @Schema(description = "是否具有拷贝文档内容权限，0-无，1-有", example = "1")
    private Integer copy;

    @Schema(description = "是否具有打印文档权限，0-无，1-有", example = "1")
    private Integer print;

    @Schema(description = "是否具有另存当前文档权限，0-无，1-有（该属性现阶段暂无相关入口）", example = "0")
    private Integer saveas;

    @Schema(description = "是否具有评论文档权限，0-无，1-有", example = "1")
    private Integer comment;
}



