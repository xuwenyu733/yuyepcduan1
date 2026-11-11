package cn.iocoder.yudao.module.fish.controller.admin.zkusemedicinelog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 用药明细 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkUseMedicineLogExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("用药方案id(zk_use_medicine.id)")
    private Long zkUseMedicineId;

    @ExcelProperty("系统编号(车间序号_存塘序号)")
    private String pondNum;
    private String pondDetailNum;

    @ExcelProperty("存塘名称")
    private String pondName;

    @ExcelProperty("存塘id")
    private Long pondId;

    @ExcelProperty("存塘规模")
    private String pondScale;

    @ExcelProperty("存塘鱼数")
    private Integer fishNum;

    @ExcelProperty("商品编号")
    private String productNo;

    @ExcelProperty("用药名称")
    private String medicineName;

    @ExcelProperty("用药用量")
    private String midicineDosage;

    @ExcelProperty("诊断标题")
    private String controlTitle;

    @ExcelProperty("用药结果")
    private String medicineAfter;

    @ExcelProperty("品牌")
    private String medicineBrand;

    @ExcelProperty("规格")
    private String medicineSpec;

    @ExcelProperty("用药时间")
    private LocalDateTime feedTime;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("类型(1:集群用药;2:隔离用药;)")
    private String feedType;

    @ExcelProperty("负责人id")
    private Long userId;

    @ExcelProperty("负责人")
    private String userName;

    @ExcelProperty("负责人所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    private BigDecimal medicinePrice; // 药品价格
    private BigDecimal medicineCost; // 用药成本

}
