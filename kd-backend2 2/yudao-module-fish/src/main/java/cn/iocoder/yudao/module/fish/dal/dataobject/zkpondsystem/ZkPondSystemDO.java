package cn.iocoder.yudao.module.fish.dal.dataobject.zkpondsystem;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 存塘系统 DO
 *
 * @author 系统管理员
 */
@TableName("zk_pond_system")
@KeySequence("zk_pond_system_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkPondSystemDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 系统编号
     */
    private String systemNum;
    /**
     * 系统名称
     */
    private String systemName;
    /**
     * 所在车间
     */
    private String workshopName;
    /**
     * 所在车间id
     */
    private Long workshopId;
    /**
     * 负责人
     */
    private String userName;
    /**
     * 负责人id
     */
    private Long userId;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;
    @TableField(exist = false)
    private String tenantName; // 租户名称
    @TableField(exist = false)
    private String deptName; // 部门名称

}
