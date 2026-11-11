package cn.iocoder.yudao.module.fish.controller.admin.zkusemedicinelog.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用药明细 Excel 导出 Request VO，参数和 ZkUseMedicineLogPageReqVO 是一致的")
@Data
public class ZkUseMedicineLogExportReqVO {

    @Schema(description = "用药方案id(zk_use_medicine.id)", example = "18106")
    private Long zkUseMedicineId;

    @Schema(description = "系统编号(车间序号_存塘序号)")
    private String pondNum;
    private String pondDetailNum;

    @Schema(description = "存塘名称", example = "李四")
    private String pondName;

    @Schema(description = "存塘id", example = "14827")
    private Long pondId;

    @Schema(description = "存塘规模")
    private String pondScale;

    @Schema(description = "存塘鱼数")
    private Integer fishNum;

    @Schema(description = "商品编号")
    private String productNo;

    @Schema(description = "用药名称", example = "张三")
    private String medicineName;

    @Schema(description = "用药用量")
    private String midicineDosage;

    @Schema(description = "诊断标题")
    private String controlTitle;

    @Schema(description = "用药结果")
    private String medicineAfter;

    @Schema(description = "品牌")
    private String medicineBrand;

    @Schema(description = "规格")
    private String medicineSpec;

    @Schema(description = "用药时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] feedTime;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "类型(1:集群用药;2:隔离用药;)", example = "2")
    private String feedType;

    @Schema(description = "负责人id", example = "15140")
    private Long userId;

    @Schema(description = "负责人", example = "李四")
    private String userName;

    @Schema(description = "负责人所属部门", example = "30204")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
    private BigDecimal medicinePrice; // 药品价格
    private BigDecimal medicineCost; // 用药成本

}
