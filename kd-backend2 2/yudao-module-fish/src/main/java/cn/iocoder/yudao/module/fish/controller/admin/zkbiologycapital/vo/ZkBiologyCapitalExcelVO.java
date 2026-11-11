package cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 生物性资产 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkBiologyCapitalExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("车间id")
    private Long workshopId;

    @ExcelProperty("车间名称")
    private String workshopName;

    @ExcelProperty("系统名称")
    private String pondSystemName;

    @ExcelProperty("系统id")
    private Long pondSystemId;

    @ExcelProperty("系统编号")
    private String pondNum;

    @ExcelProperty("存塘名称")
    private String pondName;

    @ExcelProperty("存塘id")
    private Long pondId;

    @ExcelProperty("存塘编号")
    private String pondDetailNum;

    @ExcelProperty("品名")
    private String productName;

    @ExcelProperty("品牌")
    private String brand;

    @ExcelProperty("规格")
    private String specifications;

    @ExcelProperty("库存类别")
    private String productClassification;

    @ExcelProperty("类别编号")
    private String categoryNo;

    @ExcelProperty("数量")
    private Long productNum;

    @ExcelProperty("总重量")
    private BigDecimal productWeight;

    @ExcelProperty("市场单价")
    private BigDecimal productPrice;

    @ExcelProperty("资产总计")
    private BigDecimal capitalTotal;

    @ExcelProperty("当前成本")
    private BigDecimal currCost;

    @ExcelProperty("饲料成本")
    private BigDecimal feedCost;

    @ExcelProperty("药品成本")
    private BigDecimal medicineCost;

    @ExcelProperty("设备成本")
    private BigDecimal deviceCost;

    @ExcelProperty("能源成本")
    private BigDecimal energyCost;

    @ExcelProperty("额外成本")
    private BigDecimal extraCost;

    @ExcelProperty("固定资产成本")
    private BigDecimal fixedCost;

    @ExcelProperty("生物资产成本")
    private BigDecimal biologyCost;

    @ExcelProperty("存塘个数")
    private Long pondCount;

    @ExcelProperty("总体盈亏")
    private BigDecimal overallProfitAndLoss;

    @ExcelProperty("盈亏比例")
    private BigDecimal profitAndLossRatio;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("记录人")
    private Long userId;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;
    private BigDecimal feedCount = new BigDecimal(0); // 累计饲料重量
    private BigDecimal feedPrice = new BigDecimal(0); // 累计饲料价格
    private BigDecimal fishPrice = new BigDecimal(0); // 鱼苗价格

}
