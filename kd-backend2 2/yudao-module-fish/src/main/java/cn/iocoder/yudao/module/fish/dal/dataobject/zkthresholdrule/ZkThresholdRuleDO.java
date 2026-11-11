package cn.iocoder.yudao.module.fish.dal.dataobject.zkthresholdrule;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 阈值规则 DO
 *
 * @author 系统管理员
 */
@TableName("zk_threshold_rule")
@KeySequence("zk_threshold_rule_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkThresholdRuleDO extends BaseDO {

    /**
     * 规则ID
     */
    @TableId
    private Long id;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 设备类型ID，关联iot_device_type表
     */
    private Long deviceTypeId;
    /**
     * 监测指标ID，关联iot_monitor_metric表
     */
    private Long metricId;
    /**
     * 比较运算符（如：>、<、>=、<=、==、!=、between）
     */
    private String comparisonOperator;
    /**
     * 阈值1（用于单值比较）
     */
    private BigDecimal thresholdValue1;
    /**
     * 阈值2（用于范围比较，如between）
     */
    private BigDecimal thresholdValue2;
    /**
     * 是否启用（1：启用，0：禁用）
     */
    private Byte isEnabled;
    /**
     * 触发级别（1：提示，2：警告，3：严重，4：紧急）
     */
    private Byte triggerLevel;
    /**
     * 触发动作（如：发送通知、启动设备、关闭设备）
     */
    private String triggerAction;
    /**
     * 规则描述
     */
    private String description;
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
