package cn.iocoder.yudao.framework.common.base_api.fish;

import java.util.Set;

/**
 * 群聊、一对一聊天 基本接口
 */
public interface BaseChatMsgReceive {

    /**
     * 当前消息已经处理过了
     * @param chatMsgReceiveIdSet 已经通知的消息id
     * @return 处理结果
     */
    Object msgIsHandled(Set<Long> chatMsgReceiveIdSet);

    /**
     * 判断当前用户是否有下一条消息需要发送
     * @param userId 当前用户
     * @return 是否有发送消息，true 是发送了消息，false则没发送消息
     */
    boolean sendNextMsg(String userId);

    boolean judgeIsHandled(Long chatMsgReceiveId);
}
