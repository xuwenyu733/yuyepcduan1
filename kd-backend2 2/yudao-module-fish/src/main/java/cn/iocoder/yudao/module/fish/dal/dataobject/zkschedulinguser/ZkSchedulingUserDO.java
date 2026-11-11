package cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulinguser;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 排班用户 DO
 *
 * @author 系统管理员
 */
@TableName("zk_scheduling_user")
@KeySequence("zk_scheduling_user_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkSchedulingUserDO extends BaseDO {

    /**
     * 序号
     */
    @TableId
    private Long id;
    /**
     * 排班id
     */
    private Long schedulingId;
    /**
     * 被排班人id
     */
    private Long userId;
    /**
     * 被排班人
     */
    private String userName;
    /**
     * 被排班人所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;

}
