package cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyprocess;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 优化流程记录 DO
 *
 * @author admin2
 */
@TableName("zk_purify_process")
@KeySequence("zk_purify_process_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurifyProcessDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 关联档案ID
     */
    private Long archiveId;
    @TableField(exist = false)
    private String poolNumber;
    @TableField(exist = false)
    private String purchaseOrderNo;
    @TableField(exist = false)
    private String category;

    /**
     * 流程单号
     */
    private String recordNumber;
    /**
     * 状态(farming:优化中, out_pool:已完成)
     */
    private String status;
    /**
     * 开始确认时间
     */
    private LocalDateTime startTime;
    /**
     * 负责人
     */
    private String operator;
    /**
     * 累计死亡数量
     */
    private Integer totalDeathCount;
    /**
     * 累计死亡重量
     */
    private BigDecimal totalDeathWeight;
    /**
     * 累计出池数量
     */
    private Integer totalOutCount;
    /**
     * 累计出池重量
     */
    private BigDecimal totalOutWeight;
    /**
     * 当前存塘数量
     */
    private Integer currentStockCount;
    /**
     * 所属部门
     */
    private Long deptId;
    private BigDecimal mortalityRate; // 死亡率
    private BigDecimal scaleDropRate; // 掉秤率


}