package cn.iocoder.yudao.module.fish.controller.admin.aiagent;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Java -> Python 中转时的安全 header 透传工具。
 *
 * 说明：
 * - 仅透传白名单 header，避免无意间把敏感/无关 header（如 Cookie）带到 Python；
 * - 主要用于透传 Authorization（Bearer token）、tenant-id、链路追踪等。
 */
public final class ForwardHeaderUtil {

    private ForwardHeaderUtil() {
    }

    public static final List<String> SAFE_FORWARD_HEADERS = Arrays.asList(
            "Authorization",
            "tenant-id",
            "X-Trace-Id",
            "traceparent",
            "tracestate"
    );

    public static Map<String, String> extractForwardHeaders(HttpServletRequest request) {
        Map<String, String> out = new LinkedHashMap<>();
        if (request == null) {
            return out;
        }
        for (String h : SAFE_FORWARD_HEADERS) {
            try {
                String v = request.getHeader(h);
                if (StringUtils.isNotBlank(v)) {
                    out.put(h, v);
                }
            } catch (Exception ignore) {
            }
        }
        // 额外透传：Java 基地址，供 Python 回调 Java 业务接口使用（避免 Python 侧必须配置 JAVA_API_BASE）
        try {
            String proto = request.getHeader("X-Forwarded-Proto");
            if (StringUtils.isBlank(proto)) {
                proto = request.getScheme();
            }
            String host = request.getHeader("X-Forwarded-Host");
            if (StringUtils.isBlank(host)) {
                host = request.getHeader("Host");
            }
            if (StringUtils.isBlank(host)) {
                host = request.getServerName() + ":" + request.getServerPort();
            }
            String ctx = StringUtils.defaultString(request.getContextPath());
            // yudao 的管理端接口统一前缀为 /admin-api
            String base = proto + "://" + host + ctx + "/admin-api";
            out.put("X-Java-Api-Base", base);
        } catch (Exception ignore) {
        }
        return out;
    }
}

