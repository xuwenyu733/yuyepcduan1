package cn.iocoder.yudao.framework.common.base_api;

import java.util.Map;

/**
 * 只要是websocket链接，每个15s左右时间会和服务器有心跳，此时 如果需要通知客户端，则可以 处理逻辑
 */
public interface BaseWebSocketHandle {

    Map<String, Object> heartCheckHandle(String userId, String deviceType);

}
