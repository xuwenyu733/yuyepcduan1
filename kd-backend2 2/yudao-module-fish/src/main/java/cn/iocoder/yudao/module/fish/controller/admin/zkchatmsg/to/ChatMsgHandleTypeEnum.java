package cn.iocoder.yudao.module.fish.controller.admin.zkchatmsg.to;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息是否处理
 * @创建人 lr
 * @创建时间 2025-04-17
 * @描述
 */
@Getter
@AllArgsConstructor
public enum ChatMsgHandleTypeEnum {


    NEED_NOTICE(0, "待通知")
    ,HAD_NOTICE(1, "已通知")
    ,HAD_CONFIRM(2, "已确认")
    ,HAD_READ(3, "已查看")
    ,HAD_REPLY(4, "已回复")
    ;

    /**
     * 状态值
     */
    private final Integer type;
    /**
     * 状态名
     */
    private final String name;

}
