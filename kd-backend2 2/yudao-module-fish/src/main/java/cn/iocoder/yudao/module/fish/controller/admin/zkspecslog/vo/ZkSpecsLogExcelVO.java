package cn.iocoder.yudao.module.fish.controller.admin.zkspecslog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 规格记录 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkSpecsLogExcelVO {

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

    @ExcelProperty("初始规格")
    private BigDecimal initSpecs;

    @ExcelProperty("初始数量")
    private Long initNum;

    @ExcelProperty("投喂总量")
    private BigDecimal feedCount;
    private BigDecimal currFeedCount; // 当日投喂量(Kg)

    @ExcelProperty("当前存塘数量")
    private Long currNum;

    @ExcelProperty("预估饵料系数")
    private BigDecimal predictRadio;

    @ExcelProperty("预估规格")
    private BigDecimal predictSpecs;

    @ExcelProperty("实际打样规格")
    private BigDecimal acturalSpecs;

    @ExcelProperty("实际饵料系数")
    private BigDecimal acturalRadio;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("记录人")
    private Long userId;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;
    private BigDecimal predictAccumulatedWeightGain; // 预估累计增重
    private BigDecimal acturalAccumulatedWeightGain; // 实际累计增重
    private BigDecimal biologicalAssets; // 存塘生物资产
    private BigDecimal sumBiologicalAssets; // 总生物资产
    private BigDecimal avgBaitCoefficient; // 平均饵料系数

}
