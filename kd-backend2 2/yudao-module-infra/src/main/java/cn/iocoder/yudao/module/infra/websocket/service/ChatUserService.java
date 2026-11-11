package cn.iocoder.yudao.module.infra.websocket.service;


import cn.iocoder.yudao.module.infra.websocket.vo.ChatUserList;
import cn.iocoder.yudao.module.infra.websocket.vo.MsgItem;

/**
 * @创建人 lr
 * @创建时间 2025-06-13
 * @描述
 */
public interface ChatUserService {
    ChatUserList selectOnLineUsers(MsgItem msgItem);
}
