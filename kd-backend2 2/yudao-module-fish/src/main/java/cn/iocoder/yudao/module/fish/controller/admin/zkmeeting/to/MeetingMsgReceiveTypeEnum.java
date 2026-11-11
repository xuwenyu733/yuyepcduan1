package cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.to;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @创建人 lr
 * @创建时间 2025-04-02
 * @描述
 */
@Getter
@AllArgsConstructor
public enum MeetingMsgReceiveTypeEnum {


    JOIN_MEET("加入会议", "son_send_to_parent_meeting_jion_meet", "")
    ,CHAT_MSG("会议聊天", "son_send_to_parent_meeting_chat_msg", "")
    ;

    private final String name;
    private final String value;
    private final String remark;

}
