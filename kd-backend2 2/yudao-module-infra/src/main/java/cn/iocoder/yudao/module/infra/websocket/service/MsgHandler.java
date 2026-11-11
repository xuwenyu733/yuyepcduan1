package cn.iocoder.yudao.module.infra.websocket.service;

import cn.iocoder.yudao.module.infra.websocket.vo.MsgItem;
import jakarta.websocket.Session;

/**
 * @创建人 lr
 * @创建时间 2025-06-13
 * @描述
 */
public interface MsgHandler {

    void onMessage(Session session, String message);

    void normalMsgHandle(Session session, String message, MsgItem msgItem);

    /**
     * 针对当前用 queue的下一条消息进行发送
     * @param msgItem 当前消息
     * @return 是否发送
     */
    boolean sendNextMsg(MsgItem msgItem);

    /**
     * 针对 前台已经反馈收到的消息进行删除操作，避免 重复发送
     * @param msgItem 当前消息
     * @return 是否成功删除
     */
    boolean handledMsg(MsgItem msgItem);

}
