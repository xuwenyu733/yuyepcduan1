package cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsgreceive;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsg.ZkChatMsgDO;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 聊天消息接受 DO
 *
 * @author admin2
 */
@TableName("zk_chat_msg_receive")
@KeySequence("zk_chat_msg_receive_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkChatMsgReceiveDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 聊天消息id
     */
    private Long chatMsgId;
    /**
     * 消息所属人
     */
    private Long userId;
    /**
     * 消息处理类型(0:待通知;1:已通知;2:已确认;3:已查看;4:已回复;)
     */
    private Integer handleType;
    /**
     * 所属部门
     */
    private Long deptId;

    @TableField(exist = false)
    private ZkChatMsgDO zkChatMsgDO;

    public ZkChatMsgReceiveDO(Long chatMsgId, Long userId, Integer handleType, Long deptId) {
        this.chatMsgId = chatMsgId;
        this.userId = userId;
        this.handleType = handleType;
        this.deptId = deptId;
    }
}