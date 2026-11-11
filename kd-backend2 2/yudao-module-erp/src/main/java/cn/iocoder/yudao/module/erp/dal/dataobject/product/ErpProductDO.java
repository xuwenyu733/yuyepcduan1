package cn.iocoder.yudao.module.erp.dal.dataobject.product;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * ERP 产品 DO
 *
 * @author 芋道源码
 */
@TableName("erp_product")
@KeySequence("erp_product_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpProductDO extends BaseDO {

    /**
     * 产品编号
     */
    @TableId
    private Long id;
    private Long deptId; // 部门id
    private Long userId; // 用户id
    /**
     * 产品名称
     */
    private String name;
    /**
     * 产品条码
     */
    private String barCode;
    private String productBrand; // 商品品牌
    /**
     * 产品分类编号
     *
     * 关联 {@link ErpProductCategoryDO#getId()}
     */
    private Long categoryId;
    /**
     * 单位编号
     *
     * 关联 {@link ErpProductUnitDO#getId()}
     */
    private Long unitId;
    /**
     * 产品状态
     *
     * 枚举 {@link cn.iocoder.yudao.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;
    /**
     * 产品规格
     */
    private String standard;
    /**
     * 产品备注
     */
    private String remark;
    /**
     * 保质期天数
     */
    private Integer expiryDay;
    /**
     * 基础重量（kg）
     */
    private BigDecimal weight;
    /**
     * 采购价格，单位：元
     */
    private BigDecimal purchasePrice;
    /**
     * 销售价格，单位：元
     */
    private BigDecimal salePrice;
    private BigDecimal warningThreshold; // 预警阀值
    /**
     * 最低价格，单位：元
     */
    private BigDecimal minPrice;
    private String fishTypes; // 生物品类(zk_pond_fish_types)
    private String productNo;
    @TableField(exist = false)
    private String productName;
    @TableField(exist = false)
    private String unit;
    @TableField(exist = false)
    private BigDecimal stockCount = new BigDecimal(0); // 商品库存
    @TableField(exist = false)
    private String stockInfo; // 库存信息
    @TableField(exist = false)
    private String warehouseIds; // 仓库ids
    @TableField(exist = false)
    private BigDecimal count;

    public ErpProductDO build_info() {
        this.productNo = this.barCode;
        this.productName = this.name;
        return this;
    }

}