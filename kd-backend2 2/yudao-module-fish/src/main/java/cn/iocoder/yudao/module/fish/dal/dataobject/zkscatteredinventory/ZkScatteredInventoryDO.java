package cn.iocoder.yudao.module.fish.dal.dataobject.zkscatteredinventory;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 零星库存 DO
 *
 * @author admin2
 */
@TableName("zk_scattered_inventory")
@KeySequence("zk_scattered_inventory_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkScatteredInventoryDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 商品id
     */
    private Long productId;
    /**
     * 仓库id
     */
    private Long warehouseId;
    /**
     * 单位id
     */
    private Long unitId;
    /**
     * 剩余数量
     */
    private BigDecimal remainingQuantity;
    /**
     * 备注
     */
    private String remark;
    /**
     * 零星库存类型(1:手动出库;2:手动入库零星;)
     */
    private String busType;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long busId; // 业务id

    @TableField(exist = false)
    private String productName; // 商品名称
    @TableField(exist = false)
    private String unitName; // 单位名称
    @TableField(exist = false)
    private String warehouseName; // 仓库名称
    @TableField(exist = false)
    private String categoryName; // 品类名称
    @TableField(exist = false)
    private Long categoryId; // 品类id

}