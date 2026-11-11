package cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 会议创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ZkMeetingCreateReqVO extends ZkMeetingBaseVO {

    /**
     * cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.to.MeetingMsgReceiveTypeEnum
     */
    private String receiveType; // 接受消息类型
    private String chatMsg; // 会议语音识别内容
    private Long meetingId; // 会议id
    private String version;
    private String deviceType = "pc"; // pc app
    private String token;


    public ZkMeetingCreateReqVO build_meetingId(Long meetingId) {
        this.meetingId = meetingId;
        return this;
    }
    public ZkMeetingCreateReqVO build_meetDay(String meetDay) {
        this.setMeetDay(meetDay);
        return this;
    }

}
