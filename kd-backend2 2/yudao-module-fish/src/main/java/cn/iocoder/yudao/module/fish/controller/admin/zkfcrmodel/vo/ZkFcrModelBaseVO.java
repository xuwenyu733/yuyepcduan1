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
import jakarta.validation.constraints.*;

/**
* 饵料系数FCR模型 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkFcrModelBaseVO {

    @Schema(description = "所属车间id", example = "23996")
    private Long workshopId;

    @Schema(description = "车间名称", example = "王五")
    private String workshopName;

    @Schema(description = "所属系统id", example = "22644")
    private Long pondSystemId;

    @Schema(description = "存塘系统编号")
    private String pondNum;

    @Schema(description = "存塘系统名称", example = "张三")
    private String pondSystemName;

    @Schema(description = "所属存塘id", example = "14576")
    private Long pondId;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "存塘名称", example = "芋艿")
    private String pondName;

    @Schema(description = "平均打样体长（cm）")
    private BigDecimal averageSamplingLength;

    @Schema(description = "平均打样规格（g）")
    private BigDecimal averageSamplingSize;

    @Schema(description = "平均投喂量（g）")
    private BigDecimal averageFeedingAmount;

    @Schema(description = "平均增重（g）")
    private BigDecimal averageWeightGain;

    @Schema(description = "实际饵料系数")
    private BigDecimal actualFcr;

    @Schema(description = "饵料系数增长率")
    private BigDecimal fcrGrowthRate;

    @Schema(description = "投喂总量")
    private BigDecimal totalFeedingAmount;

    @Schema(description = "存塘量（尾）")
    private Integer stockingQuantity;

    @Schema(description = "总重量（Kg）")
    private BigDecimal totalWeight;

    @Schema(description = "负责人id", example = "30569")
    private Long userId;

    @Schema(description = "所属部门", example = "3959")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "804")
    private String fid;

}
