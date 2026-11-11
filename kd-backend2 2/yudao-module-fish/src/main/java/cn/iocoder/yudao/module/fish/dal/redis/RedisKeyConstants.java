package cn.iocoder.yudao.module.fish.dal.redis;

/**
 * Fish Redis Key 枚举类
 *
 * @author 芋道源码
 */
public interface RedisKeyConstants {

    /**
     * 已经处理过的  表: zk_chat_msg_receive.id 列表
     * <p>
     * KEY 格式：had_handled_chat_msg_receive_id
     * VALUE 数据类型：List zk_chat_msg_receive.id
     */
    String HAD_HANDLED_CHAT_MSG_RECEIVE_ID = "had_handled_chat_msg_receive_id";

    /**
     * 已经处理过的  表: zk_chat_msg_receive.id 列表
     * <p>
     * KEY 格式：need_notice_chat_msg:{userId}
     * VALUE 数据类型：List zk_chat_msg
     */
    String NEED_NOTICE_CHAT_MSG = "need_notice_chat_msg";


}
