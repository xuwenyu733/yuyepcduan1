package cn.iocoder.yudao.module.fish.dal.dataobject.zkworkshop;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 车间 DO
 *
 * @author 系统管理员
 */
@TableName("zk_workshop")
@KeySequence("zk_workshop_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkWorkshopDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 车间名称
     */
    private String workshopName;
    /**
     * 具体位置
     */
    private String workshopPosition;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 维度
     */
    private String dimension;
    /**
     * 负责人
     */
    private String userName;
    /**
     * 状态(0:待规划;1:规划中;2:养殖中;-1:已废弃;)
     */
    private String status;
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
