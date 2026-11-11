package cn.iocoder.yudao.module.fish.controller.admin.wps.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * WPS 文件信息 Response VO
 * 对应接口：GET /v3/3rd/files/{file_id}
 */
@Schema(description = "WPS - 文件信息 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WPSFileInfoRespVO {

    @Schema(description = "文档ID，必须与传入的file_id一致，最大长度47", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String id;

    @Schema(description = "文档名称，最大长度240，不能包含特殊字符:VI\":*?<>", requiredMode = Schema.RequiredMode.REQUIRED, example = "文档名称.docx")
    private String name;

    @Schema(description = "文档版本号，无符号int32位，从1开始，每次保存后递增", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer version;

    @Schema(description = "文档大小，单位byte", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024000")
    private Long size;

    @Schema(description = "文档创建时间戳，单位纪元秒", requiredMode = Schema.RequiredMode.REQUIRED, example = "1703123456")
    private Long create_time;

    @Schema(description = "文档最后修改时间戳，单位纪元秒", requiredMode = Schema.RequiredMode.REQUIRED, example = "1703123456")
    private Long modify_time;

    @Schema(description = "文档创建者Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "user123")
    private String creator_id;

    @Schema(description = "文档最后修改者Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "user123")
    private String modifier_id;
}



