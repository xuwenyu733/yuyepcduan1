package cn.iocoder.yudao.module.fish.controller.admin.zkbiologicalasset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 生物资产评估 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkBiologicalAssetExcelVO {

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

    @ExcelProperty("批次编号")
    private String batchNumber;

    @ExcelProperty("鱼苗种类")
    private String fishTypes;

    @ExcelProperty("鱼存量")
    private Long fishNum;

    @ExcelProperty("鱼规格克每尾")
    private Integer fishSpecification;

    @ExcelProperty("总重量")
    private BigDecimal totalWeight;

    @ExcelProperty("鱼价格")
    private BigDecimal fishPrice;

    @ExcelProperty("生物资产")
    private BigDecimal biologicalAsset;

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
