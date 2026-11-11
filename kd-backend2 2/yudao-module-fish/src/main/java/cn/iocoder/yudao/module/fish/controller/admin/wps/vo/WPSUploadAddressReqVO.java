package cn.iocoder.yudao.module.fish.controller.admin.wps.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

/**
 * WPS 获取上传地址 Request VO
 * 对应接口：POST /v3/3rd/files/{file_id}/upload/address
 */
@Schema(description = "WPS - 获取上传地址 Request VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WPSUploadAddressReqVO {

    @Schema(description = "文档名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "文档名称.docx")
    private String name;

    @Schema(description = "文档大小，单位byte", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024000")
    private Long size;

    @Schema(description = "文档校验和，key为算法，value为结果值", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> digest;

    @Schema(description = "是否手动保存，即用户手动ctrl/cmd+s或点击保存版本触发的保存，区别于定时触发的自动保存", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean is_manual;

    @Schema(description = "文档内包含的附件的大小，单位byte", example = "512000")
    private Long attachment_size;

    @Schema(description = "文档的MIME类型", example = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    private String content_type;
}



