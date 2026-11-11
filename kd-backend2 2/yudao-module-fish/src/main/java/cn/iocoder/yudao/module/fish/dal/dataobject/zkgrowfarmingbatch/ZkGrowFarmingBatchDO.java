package cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfarmingbatch;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 养殖批次 DO
 *
 * @author admin2
 */
@TableName("zk_grow_farming_batch")
@KeySequence("zk_grow_farming_batch_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkGrowFarmingBatchDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 批次编号
     */
    private String batchNo;
    /**
     * 鱼类品种ID
     */
    private Long speciesId;
    /**
     * 投苗日期
     */
    private LocalDateTime startDate;
    /**
     * 初始投放数量
     */
    private Integer initialCount;
    /**
     * 当前存栏数量
     */
    private Integer currentCount;
    /**
     * 状态 (1:进行中, 2:已收成)
     */
    private Integer status;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long tenantId;
    @TableField(exist = false)
    private String scientificName;


}
