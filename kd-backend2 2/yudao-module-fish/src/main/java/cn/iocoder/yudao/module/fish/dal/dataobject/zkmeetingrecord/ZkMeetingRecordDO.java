package cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetingrecord;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 会议记录 DO
 *
 * @author 系统管理员
 */
@TableName("zk_meeting_record")
@KeySequence("zk_meeting_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkMeetingRecordDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 会议id
     */
    private Long meetingId;
    private String meetingName;
    /**
     * 会议编号
     */
    private String meetingNum;
    private String meetDay; // 会议日期
    /**
     * 记录名称
     */
    private String meetingRecordName;
    /**
     * 记录描述
     */
    private String meetingRecordContent;
    /**
     * 记录概要
     */
    private String meetingPoint;
    /**
     * 记录总结
     */
    private String meetingSummary;
    private String meetToDo; // 会议待办
    /**
     * 记录人id
     */
    private Long userId;
    /**
     * 记录人
     */
    private String userName;
    /**
     * 记录人所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 状态(0:已记录;1:待入库;2:已入库;3:待评价;3:已评价;-1:已删除;)
     */
    private String status;
    /**
     * 文件id
     */
    private String fid;
    private String localVideoUrls; // 会议录屏url
    private String remoteVideoUrls; // 会议录屏阿里云网盘url
    private Integer videoGetCount = 0; // 录屏获取次数

    public ZkMeetingRecordDO build_summaryAndMeetToDo(String meetingSummary, String meetingName) {
        this.meetingSummary = meetingSummary;
        this.meetingName = meetingName;
        return this;
    }

    public ZkMeetingRecordDO build_meetingNum(String meetingNum) {
        this.meetingNum = meetingNum;
        return this;
    }

    public ZkMeetingRecordDO add_videoGetCount() {
        if(videoGetCount == null) videoGetCount = 0;
        this.videoGetCount = videoGetCount+1;
        return this;
    }

    public ZkMeetingRecordDO(Long meetingId, String meetingNum, String meetDay, String meetingRecordContent,
                             Long userId, String userName, Long deptId, Long tenantId) {
        this.meetingId = meetingId;
        this.meetingNum = meetingNum;
        this.meetDay = meetDay;
        this.meetingRecordContent = meetingRecordContent;
        this.userId = userId;
        this.userName = userName;
        this.deptId = deptId;
        this.tenantId = tenantId;
    }
}
