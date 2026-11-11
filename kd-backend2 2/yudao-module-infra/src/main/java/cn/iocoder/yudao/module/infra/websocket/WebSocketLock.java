package cn.iocoder.yudao.module.infra.websocket;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @创建人 lr
 * @创建时间 2025-05-14
 * @描述
 */
@Data
@NoArgsConstructor
public class WebSocketLock {
    private String uid; // 用户id
    private String sendMsg; // 发送的消息
    private String receiveMsg = "操作成功"; // 接受的消息

    private Boolean isSyncMsg; // 是否是同步消息 server->client-server
    private String version;

    public WebSocketLock(String uid, String sendMsg) {
        this.uid = uid;
        this.sendMsg = sendMsg;
    }

    public WebSocketLock(String uid, String sendMsg, String version) {
        this.uid = uid;
        this.sendMsg = sendMsg;
        this.version = version;
    }

    public String selectKey() {
        String key = new StringBuilder(uid).append(":").append(version).toString();
        return key;
    }

    public WebSocketLock(String receiveMsg) {
        this.receiveMsg = receiveMsg;
    }

    public WebSocketLock build_receiveMsg(String receiveMsg) {
        this.setReceiveMsg(receiveMsg);
        return this;
    }
}
