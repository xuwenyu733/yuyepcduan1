package cn.iocoder.yudao.module.erp.dal.dataobject.purchase;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductCategoryDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpWarehouseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * ERP 采购入库项 DO
 *
 * @author 芋道源码
 */
@TableName("erp_purchase_in_items")
@KeySequence("erp_purchase_in_items_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpPurchaseInItemDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    private Long deptId; // 部门id
    private Long userId; // 用户id
    /**
     * 采购入库编号
     *
     * 关联 {@link ErpPurchaseInDO##getId()}
     */
    private Long inId;
    /**
     * 采购订单项编号
     *
     * 关联 {@link ErpPurchaseOrderItemDO#getId()}
     * 目的：方便更新关联的采购订单项的入库数量
     */
    private Long orderItemId;
    /**
     * 仓库编号
     *
     * 关联 {@link ErpWarehouseDO#getId()}
     */
    private Long warehouseId;
    /**
     * 产品编号
     *
     * 关联 {@link ErpProductDO#getId()}
     */
    private Long productId;
    /**
     * 产品单位单位
     *
     * 冗余 {@link ErpProductDO#getUnitId()}
     */
    private Long productUnitId;

    /**
     * 产品单位单价，单位：元
     */
    private BigDecimal productPrice;
    /**
     * 数量
     */
    private BigDecimal count;
    /**
     * 总价，单位：元
     *
     * totalPrice = productPrice * count
     */
    private BigDecimal totalPrice;
    /**
     * 税率，百分比
     */
    private BigDecimal taxPercent;
    /**
     * 税额，单位：元
     *
     * taxPrice = totalPrice * taxPercent
     */
    private BigDecimal taxPrice;

    /**
     * 备注
     */
    private String remark;

    private String purchaseInNo; // 入库单编号
    private String relationOrderNo; // 入库单子编号
    /**
     * 产品分类编号
     *
     * 关联 {@link ErpProductCategoryDO#getId()}
     */
    private Long categoryId;
    private String productNo; // 商品编码
    private String productName; // 商品名称
    private String productBrand; // 商品品牌
    // private String productSpec;  // 商品规格
    /**
     * 供应商编号
     *
     * 关联 {@link ErpSupplierDO#getId()}
     */
    private Long supplierId;
    private String supplierName; // 供应商名称
    private String supplierAddress; // 供应商地址
    private String syncToCostStatus; // 数据同步成本状态(0:待同步;1:已同步;)
    private BigDecimal remainingInventory; // 剩余库存
    @TableField(exist = false)
    private BigDecimal consumeCount; // 消耗的库存

    @TableField(exist = false)
    private ErpProductDO erpProductDO;
    private BigDecimal giftNum; // 赠送数量
    private String standard; // 产品规格


    public ErpPurchaseInItemDO build_consumeCount() {
        this.consumeCount = EasyBigDecimalUtil.subtract(this.count, remainingInventory);
        return this;
    }

    public ErpPurchaseInItemDO build_productInfo(ErpProductDO erpProductDO) {
        this.productNo = erpProductDO.getProductNo();
        this.productName = erpProductDO.getName();
        this.productBrand = erpProductDO.getProductBrand();
        this.erpProductDO = erpProductDO;
        return this;
    }
    public String selectProductInfo() {
        String info = this.getProductName() + "-" + supplierName + "-" + EasyBigDecimalUtil.toScale(count, 2) +
                "尾" + "-" + erpProductDO.getStandard();
        return info;
    }
}