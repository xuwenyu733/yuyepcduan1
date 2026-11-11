package cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.to;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @创建人 lr
 * @创建时间 2025-09-02
 * @描述
 */
@Data
@NoArgsConstructor
public class MeetSummaryVo {

    private String meetingMinutes; // 纪要
    private String meetingBacklog; // 待办

    public MeetSummaryVo(String meetingMinutes, String meetingBacklog) {
        this.meetingMinutes = meetingMinutes;
        this.meetingBacklog = meetingBacklog;
    }
}
