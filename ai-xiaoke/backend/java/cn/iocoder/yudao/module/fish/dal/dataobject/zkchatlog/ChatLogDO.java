package cn.iocoder.yudao.module.fish.dal.dataobject.zkchatlog;

import cn.iocoder.yudao.module.fish.controller.admin.zkchatlog.vo.ChatLogBuilder;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 聊天日志 DO
 *
 * @author 系统管理员
 */
@TableName("zk_chat_log")
@KeySequence("zk_chat_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatLogDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 消息发送人
     */
    private String roleName;
    /**
     * 聊天内容
     */
    private String chatContent;
    /**
     * 对话所属人id
     */
    private Long userId;
    /**
     * 对话所属人
     */
    private String userName;
    /**
     * 对话所属人部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;
    private BigDecimal askScore; // 评分

    public ChatLogDO(String roleName, String chatContent, Long userId, String userName, Long deptId) {
        this.roleName = roleName;
        this.chatContent = chatContent;
        this.userId = userId;
        this.userName = userName;
        this.deptId = deptId;
    }

    public ChatLogDO build(String username, Long deptId) {
        if(!ChatLogBuilder.AI_NAME.equals(this.roleName)) {
            this.setRoleName(username);
        }
        this.setUserName(username);
        this.setDeptId(deptId);
        return this;
    }

    public ChatLogDO build_fid(String fid) {
        this.fid = fid;
        return this;
    }

}
