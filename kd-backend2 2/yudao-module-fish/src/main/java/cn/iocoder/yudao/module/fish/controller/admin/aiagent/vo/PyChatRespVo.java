package cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 对齐 Python 服务 /api/chat 的响应体（核心字段）
 */
@Data
@NoArgsConstructor
public class PyChatRespVo {

    @JsonProperty("reply")
    private String reply;

    @JsonProperty("conversation_id")
    private String conversationId;

    @JsonProperty("trace_id")
    private String traceId;

    @JsonProperty("need_user_input")
    private Boolean needUserInput;

    @JsonProperty("intent")
    private String intent;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("session_status")
    private String sessionStatus;

    @JsonProperty("sql_executed")
    private List<String> sqlExecuted;

    /** 对话后 BI：viz_version=1 结构化图表载荷（与 Python ChatResponse.viz 对齐） */
    @JsonProperty("viz")
    private Map<String, Object> viz;
}

