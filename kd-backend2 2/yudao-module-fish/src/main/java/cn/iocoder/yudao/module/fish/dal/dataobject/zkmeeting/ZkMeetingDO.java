package cn.iocoder.yudao.module.fish.dal.dataobject.zkmeeting;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.vo.ZkMeetingCreateReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetinguser.ZkMeetingUserDO;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 会议 DO
 *
 * @author 系统管理员
 */
@TableName("zk_meeting")
@KeySequence("zk_meeting_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkMeetingDO extends BaseDO {

    /**
     * 序号
     */
    @TableId
    private Long id;
    /**
     * 会议名称
     */
    private String meetingName;
    /**
     * 会议备注
     */
    private String meetingContent;
    /**
     * 会议编号
     */
    private String meetingNum;
    private String meetDay; // 会议日期
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 状态(0:已预定;1:待开始;2:会议中;3:已结束;-1:已撤销;)
     */
    private String status;
    /**
     * 主持人id
     */
    private Long userId;
    @TableField(exist = false)
    private List<Long> userIds;
    @TableField(exist = false)
    private String userNameStr;
    @TableField(exist = false)
    private List<ZkMeetingUserDO> zkMeetingUserDOS = new ArrayList<>(); // 参会人
    @TableField(exist = false)
    private String meetingPoint; // 会议纪要
    @TableField(exist = false)
    private String meetToDo; // 会议待办
    /**
     * 主持人
     */
    private String userName;
    /**
     * 主持人所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;

    public ZkMeetingDO(Long id, String meetingNum, Long userId, String meetingPoint, String meetToDo, String userName, Long deptId) {
        this.id = id;
        this.meetingNum = meetingNum;
        this.userId = userId;
        this.meetingPoint = meetingPoint;
        this.meetToDo = meetToDo;
        this.userName = userName;
        this.deptId = deptId;
    }

    public ZkMeetingDO build_auth(ZkMeetingCreateReqVO createReqVO) {
        this.setUserId(createReqVO.getUserId());
        this.setUserName(createReqVO.getUserName());
        this.setDeptId(createReqVO.getDeptId());
        return this;
    }
    public ZkMeetingDO build_zkMeetingUserDOS(List<ZkMeetingUserDO> zkMeetingUserDOS) {
        this.zkMeetingUserDOS = zkMeetingUserDOS;
        Set<Long> userIds = zkMeetingUserDOS.stream().map(obj -> obj.getUserId()).collect(Collectors.toSet());
        this.userIds = new ArrayList<>(userIds);
        String userNameStr = zkMeetingUserDOS.stream().map(obj -> obj.getUserName()).collect(Collectors.toSet()).stream().collect(Collectors.joining("、"));
        this.userNameStr = userNameStr;
        return this;
    }

    public Boolean userHasInMeet(Long userId) {
        Boolean flag = false;
        if(EasyListUtil.isNotEmpty(this.getUserIds())) {
            for(Long item : this.getUserIds()) {
                if(Objects.equals(item, userId)) {
                    flag = true;
                    break;
                }
            }
            return flag;
        }
        if(EasyListUtil.isNotEmpty(this.getZkMeetingUserDOS())) {
            for(ZkMeetingUserDO item : this.getZkMeetingUserDOS()) {
                if(Objects.equals(item.getUserId(), userId)) {
                    flag = true;
                    break;
                }
            }
            return flag;
        }
        return flag;
    }
}
