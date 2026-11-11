package cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifycostdetail;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 成本明细记录 DO
 *
 * @author admin2
 */
@TableName("zk_purify_cost_detail")
@KeySequence("zk_purify_cost_detail_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurifyCostDetailDO extends BaseDO {

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
     * 成本类型(labor:人工, electricity:电费, feed:饲料, health:动保)
     */
    private String costType;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 发生日期
     */
    private LocalDateTime occurrenceDate;
    /**
     * 备注说明
     */
    private String notes;
    /**
     * 所属部门
     */
    private Long deptId;


}