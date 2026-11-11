package cn.iocoder.yudao.module.fish.controller.admin.zkfcrmodel.vo;

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
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 饵料系数FCR模型 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkFcrModelExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("所属车间id")
    private Long workshopId;

    @ExcelProperty("车间名称")
    private String workshopName;

    @ExcelProperty("所属系统id")
    private Long pondSystemId;

    @ExcelProperty("存塘系统编号")
    private String pondNum;

    @ExcelProperty("存塘系统名称")
    private String pondSystemName;

    @ExcelProperty("所属存塘id")
    private Long pondId;

    @ExcelProperty("存塘编号")
    private String pondDetailNum;

    @ExcelProperty("存塘名称")
    private String pondName;

    @ExcelProperty("平均打样体长（cm）")
    private BigDecimal averageSamplingLength;

    @ExcelProperty("平均打样规格（g）")
    private BigDecimal averageSamplingSize;

    @ExcelProperty("平均投喂量（g）")
    private BigDecimal averageFeedingAmount;

    @ExcelProperty("平均增重（g）")
    private BigDecimal averageWeightGain;

    @ExcelProperty("实际饵料系数")
    private BigDecimal actualFcr;

    @ExcelProperty("饵料系数增长率")
    private BigDecimal fcrGrowthRate;

    @ExcelProperty("投喂总量")
    private BigDecimal totalFeedingAmount;

    @ExcelProperty("存塘量（尾）")
    private Integer stockingQuantity;

    @ExcelProperty("总重量（Kg）")
    private BigDecimal totalWeight;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("负责人id")
    private Long userId;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;

}
