package cn.iocoder.yudao.module.fish.controller.admin.wps.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

/**
 * WPS 事件通知 Request VO
 * 对应接口：POST /v3/3rd/notify
 */
@Schema(description = "WPS - 事件通知 Request VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WPSNotifyReqVO {

    @Schema(description = "文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @JsonProperty("file_id")
    private String file_id;

    @Schema(description = "事件类型：session_open, session_quit, user_join, user_quit, operate_record_export", requiredMode = Schema.RequiredMode.REQUIRED, example = "session_open")
    private String type;

    @Schema(description = "事件内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Object> content;

    @Schema(description = "会话ID（session_open事件）", example = "session123")
    @JsonProperty("session_id")
    private String session_id;

    @Schema(description = "初始版本（session_open事件）", example = "1")
    @JsonProperty("init_version")
    private Integer init_version;

    @Schema(description = "是否只读（session_open事件）", example = "false")
    private Boolean readonly;
}



