package cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroup;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroupuser.ZkChatGroupUserDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;

/**
 * 群聊组 DO
 *
 * @author admin2
 */
@TableName("zk_chat_group")
@KeySequence("zk_chat_group_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkChatGroupDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 群组名称
     */
    private String groupName;
    /**
     * 备注
     */
    private String remark;
    /**
     * 记录人
     */
    private Long userId;
    /**
     * 所属部门
     */
    private Long deptId;

    private String avatar; // 群组图片

    @TableField(exist = false)
    private String userNames;
    @TableField(exist = false)
    private List<ZkChatGroupUserDO> userList;


}