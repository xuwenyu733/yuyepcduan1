package cn.iocoder.yudao.module.fish.controller.admin.zkfcrmodel.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 饵料系数FCR模型 Excel 导出 Request VO，参数和 ZkFcrModelPageReqVO 是一致的")
@Data
public class ZkFcrModelExportReqVO {

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "30569")
    private Long userId;

    @Schema(description = "所属部门", example = "3959")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "804")
    private String fid;

}
