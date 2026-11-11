package cn.iocoder.yudao.module.fish.controller.admin.zkusemedicinelog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
* 用药明细 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkUseMedicineLogBaseVO {

    @Schema(description = "用药方案id(zk_use_medicine.id)", required = true, example = "18106")
    @NotNull(message = "用药方案id(zk_use_medicine.id)不能为空")
    private Long zkUseMedicineId;

    @Schema(description = "系统编号(车间序号_存塘序号)")
    private String pondNum;
    private String pondDetailNum;

    @Schema(description = "存塘名称", example = "李四")
    private String pondName;

    @Schema(description = "存塘id", required = true, example = "14827")
    @NotNull(message = "存塘id不能为空")
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

    @Schema(description = "用药时间", required = true)
    @NotNull(message = "用药时间不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime feedTime;

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
    private BigDecimal medicinePrice; // 药品价格
    private BigDecimal medicineCost; // 用药成本
    private List<Long> pondIdArr; // 存塘ids
    private String pondDetailNumStr; // 存塘编号

}
