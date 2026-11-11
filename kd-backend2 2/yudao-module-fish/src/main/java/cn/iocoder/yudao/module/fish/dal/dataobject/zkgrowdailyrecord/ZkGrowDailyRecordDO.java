package cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
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
 * 每日生长与环境记录 DO
 *
 * @author admin2
 */
@TableName("zk_grow_daily_record")
@KeySequence("zk_grow_daily_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkGrowDailyRecordDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 养殖批次ID
     */
    private Long batchId;
    /**
     * 养殖天数 (第X天)
     */
    private Integer dayIndex;
    /**
     * 记录日期
     */
    private LocalDateTime recordDate;
    /**
     * 当日平均水温
     */
    private BigDecimal waterTemp;
    /**
     * 当日投饵率 (%)
     */
    private BigDecimal feedRate;
    /**
     * 模型预测体重 (g)
     */
    private BigDecimal predictedWeight;
    /**
     * 模型预测体长 (cm)
     */
    private BigDecimal predictedLength;
    /**
     * 视觉识别体重 (g)
     */
    private BigDecimal visionWeight;
    /**
     * 视觉识别体长 (cm)
     */
    private BigDecimal visionLength;
    /**
     * 手动测量体重 (g)
     */
    private BigDecimal manualWeight;
    /**
     * 手动测量体长 (cm)
     */
    private BigDecimal manualLength;
    /**
     * 特定生长率 (SGR)
     */
    private BigDecimal sgr;
    private Long deptId;
    private Long tenantId;

    @TableField(exist = false)
    private String batchNo;
    private String recommendedFeedRate; // 推荐投饵率 (%)


}
