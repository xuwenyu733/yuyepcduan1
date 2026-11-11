package cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetinguser;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 会议用户 DO
 *
 * @author 系统管理员
 */
@TableName("zk_meeting_user")
@KeySequence("zk_meeting_user_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkMeetingUserDO extends BaseDO {

    /**
     * 序号
     */
    @TableId
    private Long id;
    /**
     * 会议id
     */
    private Long meetingId;
    private String meetingNum;
    /**
     * 参会人id
     */
    private Long userId;
    /**
     * 参会人
     */
    private String userName;
    /**
     * 参会人所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 用户类型(0:参会人;1:记录人;2:主持人;3:列席人员;4:特邀人员;-1:旁听人员;)
     * cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.to.MeetingUserTypeEnum
     */
    private String userType;
    /**
     * 文件id
     */
    private String fid;
    private String meetToDo; // 会议待办
    private String toDoFeedback; // 待办反馈

    @TableField(exist = false)
    private ZkMeetingUserDO lastMeetUser; // 上次会议 用户 信息

    public ZkMeetingUserDO(Long meetingId) {
        this.meetingId = meetingId;
    }

    public ZkMeetingUserDO(Long meetingId, String meetingNum, Long userId, String userName, Long deptId, String userType) {
        this.meetingId = meetingId;
        this.meetingNum = meetingNum;
        this.userId = userId;
        this.userName = userName;
        this.deptId = deptId;
        this.userType = userType;
    }
}
