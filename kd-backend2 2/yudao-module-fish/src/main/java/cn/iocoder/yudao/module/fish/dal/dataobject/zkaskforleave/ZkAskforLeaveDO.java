package cn.iocoder.yudao.module.fish.dal.dataobject.zkaskforleave;

import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 请假 DO
 *
 * @author 系统管理员
 */
@TableName("zk_askfor_leave")
@KeySequence("zk_askfor_leave_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkAskforLeaveDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 请假名称
     */
    private String leaveName;
    /**
     * 请假备注
     */
    private String leaveContent;
    /**
     * 请假天数
     */
    private Object leaveDay;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 状态(0:待审批;1:已审批;2:已销假;-1:待申请;)
     */
    private String status;
    /**
     * 请假人id
     */
    private Long userId;
    private String userName;
    /**
     * 请假人所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;
    private String processInstanceId;
    private String askforType; // 请假类型(1:事假;2:调休;3:病假;4:产假;5:陪产假;6:婚嫁;7:其他;)
    private Long approveUserId; // 上级审批人
    private Long noticeUserId; // 抄送人

    public String transformerMsg() {
        String content = String.format("%s发起请假,时间：%s-%s,事由:%s-%s"
                , this.getUserName()
                , EasyDateUtil.format(this.getStartTime(), "yyyy-MM-dd HH:mm:ss")
                , EasyDateUtil.format(this.getEndTime(), "yyyy-MM-dd HH:mm:ss"), this.getLeaveName(), this.getLeaveContent());
        return content;
    }

    public ZkAskforLeaveDO(Long id, String status) {
        this.id = id;
        this.status = status;
    }
}
