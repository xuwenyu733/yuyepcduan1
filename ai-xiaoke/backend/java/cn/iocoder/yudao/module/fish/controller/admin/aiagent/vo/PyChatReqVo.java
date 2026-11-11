package cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对齐 Python 服务 /api/chat 的请求体
 * {
 *   "user_id": "...",
 *   "conversation_id": "...",
 *   "message": "...",
 *   "route_hint": "query_analysis" | "data_entry"  // 可选
 * }
 */
@Data
@NoArgsConstructor
public class PyChatReqVo {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("conversation_id")
    private String conversationId;

    @JsonProperty("message")
    private String message;

  /** 用户显式路由：query_analysis（查询/分析/诊断）| data_entry（录入数据） */
    @JsonProperty("route_hint")
    private String routeHint;
}

