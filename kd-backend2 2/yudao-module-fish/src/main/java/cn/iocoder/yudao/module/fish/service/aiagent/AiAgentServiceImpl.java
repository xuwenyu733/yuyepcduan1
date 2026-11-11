package cn.iocoder.yudao.module.fish.service.aiagent;

import cn.hutool.http.HttpUtil;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatlog.vo.ChatLogBuilder;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatlog.ChatLogDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkapplog.ZkAppLogMapper;
import cn.iocoder.yudao.module.fish.service.zkchatlog.ChatLogService;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.MediaType;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

/**
 * @创建人 lr
 * @创建时间 2025-03-25
 * @描述
 */
@Service
@Validated
@Slf4j
public class AiAgentServiceImpl implements AiAgentService{

    @Value("${aiAgentUrl}")
    private String aiAgentUrl;

    @Value("${aiAgentUrl3}")
    private String aiAgentUrl3;

    @Resource
    private ZkAppLogMapper zkAppLogMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ChatLogService chatLogService;

    @Override
    public ChatRespVo chat(ChatVo chatVo) {
        String url = String.format("%s/chat", aiAgentUrl3);
        try {
            log.warn("【ai智能体chat接口-{}】middle,请求地址3:{}", TokenUtil.userInfo(), url);
            String post = HttpUtil.post(url, JSON.toJSONString(chatVo));
            log.warn("【ai智能体chat接口-{}】middle,请求响应:{}", TokenUtil.userInfo(), post);
            return ChatRespVo.build(post).buildChatVo(chatVo);
        } catch (Exception e) {
            String title = String.format("ai智能体chat接口-异常:%s-%s", e.getMessage(), TokenUtil.userInfo());
            String content = String.format("aiAgentUrl:%s \\n\\n 参数：%s, \\n\\n 异常： %s", url, JSON.toJSONString(chatVo), ExceptionUtil.getStackTrace(e));
            EmailUtil.send(title, content);
            log.error(content);
            return new ChatRespVo(chatVo.getUid(), "不好意思呢！服务器异常，请稍后再试，异常提示："+e.getMessage());
        }
        // return new ChatRespVo("33", "测试消息，避免token消费。");
    }

    /**
     * 新接口：对齐 Python 服务 /api/chat
     */
    @Override
    public PyChatRespVo chatPy(PyChatReqVo req) {
        return chatPy(req, Collections.emptyMap());
    }

    @Override
    public PyChatRespVo chatPy(PyChatReqVo req, Map<String, String> forwardHeaders) {
        try {
            // 统一走 SSE（/api/chat/stream），chatPy 仅做“流式聚合 -> 一次性返回”，用于兼容旧调用方
            String jsonBody = objectMapper.writeValueAsString(req);
            String streamUrl = buildChatStreamUrl(aiAgentUrl);
            log.warn("【ai智能体chatPy接口-{}】middle,聚合SSE请求地址:{}", TokenUtil.userInfo(), streamUrl);

            AtomicReference<String> replyRef = new AtomicReference<>("");
            PyChatRespVo respVo = new PyChatRespVo();

            cn.hutool.http.HttpResponse resp = cn.hutool.http.HttpRequest
                    .post(streamUrl)
                    .header("Accept", "text/event-stream")
                    .header("Content-Type", "application/json")
                    .addHeaders(safeHeaders(forwardHeaders))
                    .body(jsonBody)
                    .timeout(120_000)
                    .execute(true);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(resp.bodyStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("data:")) {
                        String data = line.substring("data:".length()).trim();
                        if (data.isEmpty() || "{}".equals(data)) {
                            continue;
                        }
                        try {
                            com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(data);
                            String type = node.has("type") ? node.get("type").asText() : null;
                            if ("meta".equals(type)) {
                                if (node.hasNonNull("conversation_id")) respVo.setConversationId(node.get("conversation_id").asText());
                                if (node.hasNonNull("trace_id")) respVo.setTraceId(node.get("trace_id").asText());
                                if (node.hasNonNull("need_user_input")) respVo.setNeedUserInput(node.get("need_user_input").asBoolean());
                                if (node.hasNonNull("intent")) respVo.setIntent(node.get("intent").asText());
                                if (node.hasNonNull("session_id")) respVo.setSessionId(node.get("session_id").asText());
                                if (node.hasNonNull("session_status")) respVo.setSessionStatus(node.get("session_status").asText());
                                // 与 Python 一致：首轮 meta 与 final 后 meta 均可带 viz，后者覆盖前者
                                if (node.has("viz") && !node.get("viz").isNull()) {
                                    try {
                                        respVo.setViz(objectMapper.convertValue(node.get("viz"), new TypeReference<Map<String, Object>>() {}));
                                    } catch (Exception ignore) {
                                        // 忽略非对象 viz，避免阻断聚合
                                    }
                                }
                            } else if ("final".equals(type)) {
                                if (node.has("content")) {
                                    replyRef.set(node.get("content").asText(""));
                                }
                            } else if ("delta".equals(type)) {
                                // 兼容：若 Python 未发 final，也可用 delta 聚合
                                if (node.has("content")) {
                                    replyRef.set(replyRef.get() + node.get("content").asText(""));
                                }
                            }
                        } catch (Exception ignore) {
                            // 非 JSON 数据忽略（避免影响主流程）
                        }
                    } else if (line.startsWith("event:")) {
                        String ev = line.substring("event:".length()).trim();
                        if ("done".equalsIgnoreCase(ev)) {
                            break;
                        }
                    }
                }
            } finally {
                try {
                    resp.close();
                } catch (Exception ignore) {
                }
            }

            respVo.setReply(replyRef.get());
            return respVo;
        } catch (Exception e) {
            String title = String.format("ai智能体chatPy接口-异常:%s-%s", e.getMessage(), TokenUtil.userInfo());
            String content = String.format("aiAgentUrl:%s \\n\\n 参数：%s, \\n\\n 异常： %s", aiAgentUrl, JSON.toJSONString(req), ExceptionUtil.getStackTrace(e));
            EmailUtil.send(title, content);
            log.error(content);
            PyChatRespVo resp = new PyChatRespVo();
            resp.setReply("不好意思呢！服务器异常，请稍后再试，异常提示：" + e.getMessage());
            return resp;
        }
    }

    @Override
    public SseEmitter chatPyStream(PyChatReqVo req) {
        return chatPyStream(req, Collections.emptyMap());
    }

    @Override
    public SseEmitter chatPyStream(PyChatReqVo req, Map<String, String> forwardHeaders) {
        // 超时：0 表示不超时（由 nginx/网关/调用方自行控制）
        SseEmitter emitter = new SseEmitter(0L);
        CompletableFuture.runAsync(() -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(req);
                String streamUrl = buildChatStreamUrl(aiAgentUrl);
                log.warn("【ai智能体chatPyStream接口-{}】middle,请求地址:{}", TokenUtil.userInfo(), streamUrl);

                // Hutool 支持流式：execute(true) 不会一次性读完 body
                cn.hutool.http.HttpResponse resp = cn.hutool.http.HttpRequest
                        .post(streamUrl)
                        .header("Accept", "text/event-stream")
                        .header("Content-Type", "application/json")
                        .addHeaders(safeHeaders(forwardHeaders))
                        .body(jsonBody)
                        .timeout(120_000)
                        .execute(true);

                AtomicLong forwarded = new AtomicLong(0);
                // 与 chatPy 聚合逻辑一致：只认 final / delta，不把 progress（思考步骤）写入库
                AtomicReference<String> replyRef = new AtomicReference<>("");
                long startedAt = System.currentTimeMillis();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(resp.bodyStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        // SSE 空行表示一个事件结束；这里按行转发即可
                        if (line.startsWith("data:")) {
                            String data = line.substring("data:".length()).trim();
                            if (!data.isEmpty()) {
                                try {
                                    // 关键：按 JSON 对象发送，避免 String 被二次包裹导致前端拿到字符串而不是对象
                                    com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(data);
                                    emitter.send(SseEmitter.event().data(node, MediaType.APPLICATION_JSON));
                                    accumulateStreamReplyForPersistence(node, replyRef);
                                } catch (Exception parseEx) {
                                    // 兜底：非 JSON 仍按文本转发，避免中断流
                                    emitter.send(SseEmitter.event().data(data));
                                }
                                long n = forwarded.incrementAndGet();
                                if (n <= 5 || n % 20 == 0) {
                                    log.warn("【ai智能体chatPyStream接口-{}】forward_event count={} sample={}",
                                            TokenUtil.userInfo(), n, data.length() > 120 ? data.substring(0, 120) + "..." : data);
                                }
                            }
                        } else if (line.startsWith("event:")) {
                            String ev = line.substring("event:".length()).trim();
                            log.warn("【ai智能体chatPyStream接口-{}】upstream_event name={}", TokenUtil.userInfo(), ev);
                            if ("done".equalsIgnoreCase(ev)) {
                                break;
                            }
                        }
                    }
                    log.warn("【ai智能体chatPyStream接口-{}】stream_complete forwarded={} elapsed_ms={}",
                            TokenUtil.userInfo(), forwarded.get(), (System.currentTimeMillis() - startedAt));
                    persistAiReplyFromStream(req, replyRef.get());
                } finally {
                    try {
                        resp.close();
                    } catch (Exception ignore) {
                    }
                }
                emitter.complete();
            } catch (Exception e) {
                log.error("【ai智能体chatPyStream接口】异常: {}", e.getMessage(), e);
                try {
                    emitter.completeWithError(e);
                } catch (Exception ignore) {
                }
            }
        });
        return emitter;
    }

    private static Map<String, String> safeHeaders(Map<String, String> in) {
        if (in == null || in.isEmpty()) {
            return Collections.emptyMap();
        }
        // Hutool addHeaders 会原样带出，这里做一次轻清洗：去空值，避免异常
        java.util.LinkedHashMap<String, String> out = new java.util.LinkedHashMap<>();
        for (Map.Entry<String, String> e : in.entrySet()) {
            if (e == null) continue;
            String k = e.getKey();
            String v = e.getValue();
            if (StringUtils.isBlank(k) || StringUtils.isBlank(v)) continue;
            out.put(k, v);
        }
        return out;
    }

    /**
     * aiAgentUrl 既可能是 http://host:8000/api/chat，也可能是 http://host:8000
     * 统一拼出 Python SSE 地址：/api/chat/stream
     */
    /**
     * 流式 SSE 落库用：仅聚合 final / delta，忽略 progress（避免把「思考步骤」写入 zk_chat_log）。
     */
    private static void accumulateStreamReplyForPersistence(com.fasterxml.jackson.databind.JsonNode node, AtomicReference<String> replyRef) {
        if (node == null || !node.has("type")) {
            return;
        }
        String type = node.get("type").asText("");
        if ("final".equals(type) && node.has("content")) {
            replyRef.set(node.get("content").asText(""));
        } else if ("delta".equals(type) && node.has("content")) {
            replyRef.set(replyRef.get() + node.get("content").asText(""));
        }
    }

    /**
     * chatPyStream 结束后将 AI 最终回复写入 zk_chat_log（与 chatPy 非流式一致）。
     * 说明：AiAgentController.chatPyStream 入口仅异步保存了用户问题（ChatLogBuilder.build(ChatVo) 单条用户），此前未保存 AI 侧，导致库中缺少最终回复或列表页展示异常。
     */
    private void persistAiReplyFromStream(PyChatReqVo req, String aiContent) {
        if (req == null || StringUtils.isBlank(req.getUserId())) {
            return;
        }
        String text = StringUtils.trimToEmpty(aiContent);
        if (text.isEmpty()) {
            log.warn("【ai智能体chatPyStream接口-{}】persist_ai_skip empty_reply", TokenUtil.userInfo());
            return;
        }
        try {
            long userId = Long.parseLong(req.getUserId().trim());
            String fid = StringUtils.trimToEmpty(req.getConversationId());
            ChatLogDO aiDo = new ChatLogDO(ChatLogBuilder.AI_NAME, text, userId, "", null).build_fid(fid);
            chatLogService.customSaveList(Collections.singletonList(aiDo));
            log.warn("【ai智能体chatPyStream接口-{}】persist_ai_ok len={} fid={}",
                    TokenUtil.userInfo(), text.length(), fid);
        } catch (Exception e) {
            log.warn("【ai智能体chatPyStream接口-{}】persist_ai_fail: {}", TokenUtil.userInfo(), e.getMessage());
        }
    }

    private String buildChatStreamUrl(String base) {
        String b = (base == null) ? "" : base.trim();
        if (b.endsWith("/")) b = b.substring(0, b.length() - 1);
        if (b.endsWith("/api/chat")) {
            return b + "/stream";
        }
        return b + "/api/chat/stream";
    }

    private String buildDataEntrySubmitUrl(String base) {
        String b = (base == null) ? "" : base.trim();
        if (b.endsWith("/")) b = b.substring(0, b.length() - 1);
        // aiAgentUrl 既可能是 http://host:8000/api/chat，也可能是 http://host:8000
        if (b.endsWith("/api/chat")) {
            b = b.substring(0, b.length() - "/api/chat".length());
        }
        return b + "/api/data-entry/submit";
    }

    @Override
    public Map<String, Object> dataEntrySubmit(Map<String, Object> req, Map<String, String> forwardHeaders) {
        try {
            String jsonBody = objectMapper.writeValueAsString(req);
            String url = buildDataEntrySubmitUrl(aiAgentUrl);
            log.warn("【ai智能体dataEntrySubmit-{}】middle,请求地址:{}", TokenUtil.userInfo(), url);

            cn.hutool.http.HttpResponse resp = cn.hutool.http.HttpRequest
                    .post(url)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .addHeaders(safeHeaders(forwardHeaders))
                    .body(jsonBody)
                    .timeout(120_000)
                    .execute();

            String body = resp.body();
            try {
                return objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
            } catch (Exception ignore) {
                java.util.LinkedHashMap<String, Object> out = new java.util.LinkedHashMap<>();
                out.put("ok", resp.getStatus() < 400);
                out.put("status", resp.getStatus());
                out.put("raw", body);
                return out;
            } finally {
                try { resp.close(); } catch (Exception ignore2) {}
            }
        } catch (Exception e) {
            java.util.LinkedHashMap<String, Object> out = new java.util.LinkedHashMap<>();
            out.put("ok", false);
            out.put("error", e.getMessage());
            return out;
        }
    }

    @Override
    public ChatRespVo add_text(TextVo vo) {
        String url = String.format("%s/add_text", aiAgentUrl);
        try {
            String params = JSON.toJSONString(vo);
            log.warn("【ai智能体add_text接口-{}】middle,请求地址3:{},参数:{}", TokenUtil.userInfo(), url, params);
            String post = HttpUtil.post(url, params);
            log.warn("【ai智能体add_text接口-{}】middle,请求响应:{}", TokenUtil.userInfo(), post);
            return ChatRespVo.build(post);
        } catch (Exception e) {
            String title = String.format("ai智能体add_text接口-异常:%s-%s", e.getMessage(), TokenUtil.userInfo());
            String content = String.format("aiAgentUrl:%s \\n\\n 参数：%s, \\n\\n 异常： %s", url, "", ExceptionUtil.getStackTrace(e));
            EmailUtil.send(title, content);
            log.error(content);
            return new ChatRespVo(null, "不好意思呢！服务器异常，请稍后再试，异常提示："+e.getMessage());
        }
    }

    @Override
    public ChatRespVo delete_text(TextVo vo) {
        String url = String.format("%s/delete_text?q_id=%s", aiAgentUrl, vo.getQ_id());
        try {
            log.warn("【ai智能体delete_text接口-{}】middle,请求地址3:{}", TokenUtil.userInfo(), url);
            String post = HttpUtil.post(url, "");
            log.warn("【ai智能体delete_text接口-{}】middle,请求响应:{}", TokenUtil.userInfo(), post);
            return ChatRespVo.build(post);
        } catch (Exception e) {
            String title = String.format("ai智能体delete_text接口-异常:%s-%s", e.getMessage(), TokenUtil.userInfo());
            String content = String.format("aiAgentUrl:%s \\n\\n 参数：%s, \\n\\n 异常： %s", url, "", ExceptionUtil.getStackTrace(e));
            EmailUtil.send(title, content);
            log.error(content);
            return new ChatRespVo(null, "不好意思呢！服务器异常，请稍后再试，异常提示："+e.getMessage());
        }
    }

    @Override
    public AgentToolModel getByIds(List<Long> modelIds) {
        // 1. 获取 system_menu 列表
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in("id", modelIds);
        List<ToolModelItem> toolModelItems = zkAppLogMapper.selectModels(queryWrapper);
        // 2. 组装vo 返回
        AgentToolModel model = new AgentToolModel(toolModelItems);
        return model.build();
    }
}
