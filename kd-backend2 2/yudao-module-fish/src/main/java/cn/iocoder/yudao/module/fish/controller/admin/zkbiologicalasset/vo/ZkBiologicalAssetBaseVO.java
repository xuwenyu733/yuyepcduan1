package cn.iocoder.yudao.module.fish.controller.admin.zkbiologicalasset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 生物资产评估 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkBiologicalAssetBaseVO {

    @Schema(description = "所属车间id", example = "32710")
    private Long workshopId;

    @Schema(description = "车间名称", example = "李四")
    private String workshopName;

    @Schema(description = "所属系统id", example = "30613")
    private Long pondSystemId;

    @Schema(description = "存塘系统编号")
    private String pondNum;

    @Schema(description = "存塘系统名称", example = "赵六")
    private String pondSystemName;

    @Schema(description = "所属存塘id", example = "27179")
    private Long pondId;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "存塘名称", example = "张三")
    private String pondName;

    @Schema(description = "批次编号")
    private String batchNumber;

    @Schema(description = "鱼苗种类")
    private String fishTypes;

    @Schema(description = "鱼存量")
    private Long fishNum;

    @Schema(description = "鱼规格克每尾")
    private Integer fishSpecification;

    @Schema(description = "总重量")
    private BigDecimal totalWeight;

    @Schema(description = "鱼价格", example = "16560")
    private BigDecimal fishPrice;

    @Schema(description = "生物资产")
    private BigDecimal biologicalAsset;

    @Schema(description = "负责人id", example = "16959")
    private Long userId;

    @Schema(description = "所属部门", example = "20770")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "13453")
    private String fid;

}
