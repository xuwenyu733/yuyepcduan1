package cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifypurchase;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 采购记录 DO
 *
 * @author admin2
 */
@TableName("zk_purify_purchase")
@KeySequence("zk_purify_purchase_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurifyPurchaseDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 采购单号
     */
    private String purchaseOrderNo;
    /**
     * 品类
     */
    private String category;
    /**
     * 来源（城市/地区）
     */
    private String source;
    /**
     * 具体地址
     */
    private String detailedAddress;
    /**
     * 联系人
     */
    private String contactPerson;
    /**
     * 联系电话
     */
    private String contactPhone;
    /**
     * 采购数量(kg)
     */
    private BigDecimal quantity;
    /**
     * 单价(元/kg)
     */
    private BigDecimal unitPrice;
    /**
     * 总金额(元)
     */
    private BigDecimal totalAmount;
    /**
     * 运输方式
     */
    private String transportMethod;
    /**
     * 采购日期
     */
    private LocalDateTime purchaseDate;
    /**
     * 采购负责人
     */
    private String purchaseManager;
    /**
     * 规格-单尾重(kg)
     */
    private BigDecimal avgWeight;
    /**
     * 备注
     */
    private String notes;
    /**
     * 所属部门
     */
    private Long deptId;


}