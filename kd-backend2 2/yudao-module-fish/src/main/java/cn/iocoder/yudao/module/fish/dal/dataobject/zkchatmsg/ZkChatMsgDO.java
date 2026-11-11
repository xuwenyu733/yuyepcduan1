package cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsg;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 聊天消息 DO
 *
 * @author admin2
 */
@TableName("zk_chat_msg")
@KeySequence("zk_chat_msg_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkChatMsgDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 群组id(群聊消息时有效)
     */
    private Long groupId;
    /**
     * 接收人id(1对1时有效)
     */
    private Long targetUserId;
    /**
     * 消息(文件消息时是url)
     */
    private String msg;
    /**
     * 文件id
     */
    private String fid;
    /**
     * 消息类型(0:文本;1:图片;2:视频;3:文档;99:其他;)
     */
    private Integer msgType;
    /**
     * 接受消息的设备(app,pc)
     * cn.iocoder.yudao.framework.common.enums.DeviceTypeEnum
     */
    private String deviceType = "app"; // pc app
    /**
     * 发送人
     */
    private Long userId;
    /**
     * 所属部门
     */
    private Long deptId;
    @TableField(exist = false)
    private String groupName;
    @TableField(exist = false)
    private String targetUserName;
    @TableField(exist = false)
    private String targetDeptId;
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String msgInfo;

    public ZkChatMsgDO build_msgInfo() {
        String info = String.format("%s:%s->%s", "个人消息", userName, targetUserName);
        if(groupId != null) {
            info = String.format("%s:%s->%s", "群组消息", userName, groupName);
        }
        this.setMsgInfo(info);
        return this;
    }

}