package cn.iocoder.yudao.module.fish.dal.dataobject.zkdevice;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 设备 DO
 *
 * @author 系统管理员
 */
@TableName("zk_device")
@KeySequence("zk_device_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkDeviceDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 设备名称
     */
    private String deviceName;
    private String deviceSn;
    private String deviceMetricId;
    private BigDecimal standardStart;
    private String startComputer;
    private BigDecimal standardEnd;
    private String endComputer;
    private String deviceUnit;
    private BigDecimal currValue;

    @TableField(exist = false)
    private String standInterval;
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
     * 状态(0:关闭中;1:开启中;-1:已废弃;)
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

    private String pondNum; // 系统编号(车间序号_存塘序号)
    private String pondName; // 存塘名称
    private Long pondId; // 存塘id
    private String deviceType; // 设备类型(0:监控数值;1:监控视频;)
    private Long pondSystemId; // 存塘系统id
    private String pondSystemName; // 存塘系统名称
    private String pondDetailNum; // 存塘编号
    private Integer ruleStatus; // 规则状态(0:关闭中;1:开启中;-1:已废弃;)
}
