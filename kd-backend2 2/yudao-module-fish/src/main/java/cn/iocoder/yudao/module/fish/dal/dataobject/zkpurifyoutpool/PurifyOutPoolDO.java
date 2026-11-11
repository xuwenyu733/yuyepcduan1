package cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyoutpool;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 出池登记 DO
 *
 * @author admin2
 */
@TableName("zk_purify_out_pool")
@KeySequence("zk_purify_out_pool_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurifyOutPoolDO extends BaseDO {

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
     * 出池时间
     */
    private LocalDateTime outPoolTime;
    /**
     * 出池重量(kg)
     */
    private BigDecimal outPoolWeight;
    /**
     * 出池数量
     */
    private Integer outPoolCount;
    /**
     * 规格等级
     */
    private String specification;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 销售单价
     */
    private BigDecimal salesPrice;
    /**
     * 销售总额
     */
    private BigDecimal totalAmount;
    /**
     * 品质评价(优/良/中/差)
     */
    private String fishQuality;
    /**
     * 检测人
     */
    private String testOperator;
    /**
     * 所属部门
     */
    private Long deptId;


}