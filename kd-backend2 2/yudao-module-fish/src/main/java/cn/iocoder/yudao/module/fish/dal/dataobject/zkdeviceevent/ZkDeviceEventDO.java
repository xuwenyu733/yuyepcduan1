package cn.iocoder.yudao.module.fish.dal.dataobject.zkdeviceevent;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 设备事件记录 DO
 *
 * @author 系统管理员
 */
@TableName("zk_device_event")
@KeySequence("zk_device_event_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkDeviceEventDO extends BaseDO {

    /**
     * 记录ID
     */
    @TableId
    private Long id;
    /**
     * 设备唯一标识
     */
    private String deviceId;
    /**
     * 设备类型ID，关联iot_device_type表
     */
    private Long deviceTypeId;
    /**
     * 记录时间（精确到分钟，按5分钟间隔存储）
     */
    private LocalDateTime recordTime;
    /**
     * 监测指标ID，关联iot_monitor_metric表
     */
    private String metricId;
    /**
     * 指标值
     */
    private BigDecimal metricValue;
    /**
     * 数据状态（1：正常，2：异常，3：已处理）
     */
    private Byte dataStatus;
    /**
     * 原始数据（保留原始上传数据）
     */
    private String rawData;
    /**
     * 设备事件JSON数据
     */
    private String deviceEvent;
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

}