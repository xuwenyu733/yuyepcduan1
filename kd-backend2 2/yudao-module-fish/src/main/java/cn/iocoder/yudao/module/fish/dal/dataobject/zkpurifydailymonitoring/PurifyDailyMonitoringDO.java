package cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifydailymonitoring;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 日常监测记录 DO
 *
 * @author admin2
 */
@TableName("zk_purify_daily_monitoring")
@KeySequence("zk_purify_daily_monitoring_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurifyDailyMonitoringDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 关联流程ID
     */
    private Long processId;
    @TableField(exist = false)
    private String recordNumber;
    /**
     * 监测日期
     */
    private LocalDateTime monitorDate;
    /**
     * 溶氧(mg/L)
     */
    private BigDecimal oxygen;
    /**
     * PH值
     */
    private BigDecimal ph;
    /**
     * 水温(℃)
     */
    private BigDecimal temperature;
    /**
     * 投喂量(kg)
     */
    private BigDecimal feedAmount;
    /**
     * 投喂时间
     */
    private LocalDateTime feedTime;
    /**
     * 健康状况
     */
    private String healthStatus;
    /**
     * 当日死亡数
     */
    private Integer deathCount;
    /**
     * 当日死亡重量
     */
    private BigDecimal deathWeight;
    /**
     * 所属部门
     */
    private Long deptId;


}