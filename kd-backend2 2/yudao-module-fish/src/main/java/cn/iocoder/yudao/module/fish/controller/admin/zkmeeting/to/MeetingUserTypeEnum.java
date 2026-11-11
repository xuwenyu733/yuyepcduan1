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
public enum MeetingUserTypeEnum {


    JOIN_User("参会人", "0", "")
    ,RECORD_USER("记录人", "1", "")
    ,HOST_USER("主持人", "2", "")
    ,ATTEND_USER("列席人员", "3", "")
    ,SPECIAL_USER("特邀人员", "4", "")
    ,AUDIT_USER("旁听人员", "-1", "")
    ;

    private final String name;
    private final String value;
    private final String remark;

}
