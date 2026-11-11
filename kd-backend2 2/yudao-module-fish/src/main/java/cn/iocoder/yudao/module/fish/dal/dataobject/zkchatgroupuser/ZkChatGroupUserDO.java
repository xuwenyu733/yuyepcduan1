package cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroupuser;

import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 群聊成员 DO
 *
 * @author admin2
 */
@TableName("zk_chat_group_user")
@KeySequence("zk_chat_group_user_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkChatGroupUserDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 群组id
     */
    private Long groupId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户类型(0:成员;1:群主;)
     */
    private Integer userType;
    /**
     * 组员
     */
    private Long userId;
    /**
     * 所属部门
     */
    private Long deptId;
    @TableField(exist = false)
    private String groupName;

    public ZkChatGroupUserDO(Long groupId, String userName, Integer userType, Long userId, Long deptId) {
        this.groupId = groupId;
        this.userName = userName;
        this.userType = userType;
        this.userId = userId;
        this.deptId = deptId;
    }
}