package cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologycapital;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
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
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 生物性资产 DO
 *
 * @author 系统管理员
 */
@TableName("zk_biology_capital")
@KeySequence("zk_biology_capital_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkBiologyCapitalDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 车间id
     */
    private Long workshopId;
    /**
     * 车间名称
     */
    private String workshopName;
    /**
     * 系统名称
     */
    private String pondSystemName;
    /**
     * 系统id
     */
    private Long pondSystemId;
    /**
     * 系统编号
     */
    private String pondNum;
    /**
     * 存塘名称
     */
    private String pondName;
    /**
     * 存塘id
     */
    private Long pondId;
    /**
     * 存塘编号
     */
    private String pondDetailNum;
    /**
     * 品名
     */
    private String productName;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 规格
     */
    private String specifications;
    /**
     * 库存类别
     */
    private String productClassification;
    /**
     * 类别编号
     */
    private String categoryNo;
    /**
     * 数量
     */
    private Long productNum;
    /**
     * 总重量
     */
    private BigDecimal productWeight = new BigDecimal(0);
    /**
     * 市场单价
     */
    private BigDecimal productPrice = new BigDecimal(0);
    /**
     * 资产总计
     */
    private BigDecimal capitalTotal = new BigDecimal(0);
    /**
     * 当前成本
     */
    private BigDecimal currCost = new BigDecimal(0);
    /**
     * 饲料成本
     */
    private BigDecimal feedCost = new BigDecimal(0);
    /**
     * 药品成本
     */
    private BigDecimal medicineCost = new BigDecimal(0);
    /**
     * 设备成本
     */
    private BigDecimal deviceCost = new BigDecimal(0);
    /**
     * 能源成本
     */
    private BigDecimal energyCost = new BigDecimal(0);
    /**
     * 额外成本
     */
    private BigDecimal extraCost = new BigDecimal(0);
    /**
     * 固定资产成本
     */
    private BigDecimal fixedCost = new BigDecimal(0);
    /**
     * 生物资产成本
     */
    private BigDecimal biologyCost = new BigDecimal(0);
    /**
     * 存塘个数
     */
    private Long pondCount;
    /**
     * 总体盈亏
     */
    private BigDecimal overallProfitAndLoss = new BigDecimal(0);
    /**
     * 盈亏比例
     */
    private BigDecimal profitAndLossRatio = new BigDecimal(0);
    /**
     * 记录人
     */
    private Long userId;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;
    private BigDecimal feedCount = new BigDecimal(0); // 累计饲料重量
    private BigDecimal feedPrice = new BigDecimal(0); // 累计饲料价格
    private BigDecimal fishPrice = new BigDecimal(0); // 鱼苗价格

    public ZkBiologyCapitalDO(Long workshopId, String workshopName, String pondSystemName, Long pondSystemId,
                              String pondNum, String pondName, Long pondId, String pondDetailNum,
                              String productName, String specifications, Long productNum, Long userId, Long deptId) {
        this.workshopId = workshopId;
        this.workshopName = workshopName;
        this.pondSystemName = pondSystemName;
        this.pondSystemId = pondSystemId;
        this.pondNum = pondNum;
        this.pondName = pondName;
        this.pondId = pondId;
        this.pondDetailNum = pondDetailNum;
        this.productName = productName;
        this.specifications = specifications;
        this.productNum = productNum;
        this.userId = userId;
        this.deptId = deptId;
    }

    public ZkBiologyCapitalDO build_update(Long productNum) {
        this.productNum = productNum;
        BigDecimal spec = new BigDecimal(this.specifications);
        return this.calculateInfo(productPrice, spec);
    }

    public ZkBiologyCapitalDO build_feedCost(BigDecimal feedCost, BigDecimal feedCount, BigDecimal feedPrice) {
        this.feedCost = EasyBigDecimalUtil.add(feedCost, this.getFeedCost());
        this.currCost = EasyBigDecimalUtil.add(this.currCost, feedCost);
        this.feedCount = EasyBigDecimalUtil.add(this.feedCount, feedCount);
        this.feedPrice = feedPrice;
        return this;
    }

    public ZkBiologyCapitalDO build_medicineCost(BigDecimal medicineCost) {
        this.medicineCost = EasyBigDecimalUtil.add(medicineCost, this.getMedicineCost());
        this.currCost = EasyBigDecimalUtil.add(this.currCost, medicineCost);
        return this;
    }

    public ZkBiologyCapitalDO calculateInfo(BigDecimal productPrice, BigDecimal spec) {
        BigDecimal productWeight = EasyBigDecimalUtil.multiply(spec, new BigDecimal(productNum));
        this.productWeight = EasyBigDecimalUtil.divide(productWeight, new BigDecimal(1000));
        this.capitalTotal = EasyBigDecimalUtil.multiply(this.productWeight, productPrice);
        this.productPrice = productPrice;
        this.specifications = spec.toString();
        return this;
    }

}
