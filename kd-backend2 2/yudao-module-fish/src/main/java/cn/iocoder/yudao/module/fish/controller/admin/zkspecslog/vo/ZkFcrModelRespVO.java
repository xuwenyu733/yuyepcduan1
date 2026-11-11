package cn.iocoder.yudao.module.fish.controller.admin.zkspecslog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 饵料系数模型 Response VO")
@Data
public class ZkFcrModelRespVO {

    @Schema(description = "车间名称")
    private String workshopName;

    @Schema(description = "系统名称")
    private String pondSystemName;

    @Schema(description = "鱼池编号")
    private String pondDetailNum;

    @Schema(description = "鱼池名称")
    private String pondName;

    @Schema(description = "条数")
    private Integer fishNum;

    @Schema(description = "平均打样规格")
    private BigDecimal avgSpecs;

    @Schema(description = "体长")
    private BigDecimal avgBodyLength;

    @Schema(description = "平均投喂量(Kg/天)")
    private BigDecimal dayTotalCount;

    @Schema(description = "投喂量Kg")
    private BigDecimal totalCount;

    @Schema(description = "总重量(Kg)")
    private BigDecimal totalWeight;

    @Schema(description = "实际饵料系数")
    private BigDecimal acturalRadio;

    @Schema(description = "平均增重")
    private BigDecimal avgGrowth;
    @Schema(description = "入池总重量")
    private BigDecimal initWeight;
    @Schema(description = "净增重")
    private BigDecimal accumulatedWeightGain;

    @Schema(description = "投喂数据来源 device|manual")
    private String feedSource;

}
