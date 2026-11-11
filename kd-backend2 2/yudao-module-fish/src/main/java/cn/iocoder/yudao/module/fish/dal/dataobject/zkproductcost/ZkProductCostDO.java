package cn.iocoder.yudao.module.fish.dal.dataobject.zkproductcost;

// import cn.iocoder.yudao.module.fish.dal.dataobject.outorderproduct.OutOrderProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 成本 DO
 *
 * @author 系统管理员
 */
@TableName("zk_product_cost")
@KeySequence("zk_product_cost_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkProductCostDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    private String productName; // 成本名称
    /**
     * 品牌
     */
    private String productBrand;
    /**
     * 规格
     */
    private String productSpec;
    /**
     * 单价
     */
    private BigDecimal productPrice;
    /**
     * 成本
     */
    private BigDecimal productCost;
    /**
     * 最终成本
     */
    private BigDecimal finalCost;
    /**
     * 备注
     */
    private String remark;
    /**
     * 类型(1:饲料;2:药品;3:设备;4:能源;5:额外;6:固定资产;7:库存;)
     */
    private String productType;
    /**
     * 负责人id
     */
    private Long userId;
    /**
     * 负责人
     */
    private String userName;
    /**
     * 负责人所属部门
     */
    private Long deptId;
    private Long tenantId;

    private String productUnit; // 商品单位
    private BigDecimal productCount; // 商品数量
    private Long outOrderProductId; // 入库单id
    private String productNo; // 商品编号

    private Long workshopId; // 所属车间id
    private Long systemId; // 所属系统id
    private Long pondId; // 所属存塘id
    @TableField(exist = false)
    private String workshopName; // 所属车间id
    @TableField(exist = false)
    private String pondNum; // 所属系统编号
    @TableField(exist = false)
    private String pondDetailNum; // 所属存塘编号
    private LocalDateTime recordTime; // 记录时间

    public ZkProductCostDO buildeByOrderProduct(ErpPurchaseInItemDO obj) {
        productNo = obj.getProductNo();
        productBrand = obj.getProductBrand();
        ErpProductDO erpProductDO = obj.getErpProductDO();
        if(erpProductDO!=null) productSpec = erpProductDO.getStandard();
        productCount = obj.getTotalPrice();
        productPrice = obj.getProductPrice();
        finalCost = obj.getTotalPrice();
        if(erpProductDO!=null) productUnit = erpProductDO.getUnit();
        productType = obj.getCategoryId()+"";
        productCount = obj.getCount();
        outOrderProductId = obj.getId();
        userId = Long.parseLong(obj.getCreator());
        // deptId = obj.get
        return this;
    }
}
