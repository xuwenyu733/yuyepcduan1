package cn.iocoder.yudao.module.fish.controller.admin.zkpondfish.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 存塘鱼苗 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkPondFishBaseVO {

    @Schema(description = "车间名称", example = "赵六")
    private String workshopName;

    @Schema(description = "所属车间id", example = "11711")
    private Long workshopId;

    @Schema(description = "存塘名称", example = "芋艿")
    private String pondName;

    @Schema(description = "所属存塘id", example = "17999")
    private Long pondId;
    private String pondNum; // 存塘系统编号
    private String pondDetailNum; // 存塘编号
    private String pondSystemName; // 存塘系统名称

    @Schema(description = "鱼苗种类")
    private String fishTypes;

    @Schema(description = "鱼苗数量", required = true)
    @NotNull(message = "鱼苗数量不能为空")
    private Long fishNum;

    @Schema(description = "负责人id", example = "1254")
    private Long userId;

    @Schema(description = "所属部门", example = "23880")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "18129")
    @NotNull(message = "文件id不能为空")
    private String fid;
    private Long pushFishNum; // 投放鱼苗量
    private Long splitReduceNum; // 分池减少鱼苗量
    private Long splitAddNum; // 分池鱼增加苗量
    private Long deadFishNum; // 死亡鱼苗量
    private Long saleFishNum; // 销售鱼苗量
    private String batchNumber; // 批次编号

    private BigDecimal initFishSpec; // 初始规格
    private BigDecimal currFishSpec; // 当前规格(打样后更新该值)

    private Long splitDayMortalityRate; // 分池七日死亡量
    private BigDecimal splitDayMortalityRatePer; // 分池七日死亡率
    private Long recentDayMortalityRate; // 投放七日死亡量
    private BigDecimal recentDayMortalityRatePer; // 投放七日死亡率
    // private Long accumulativeDeathToll; // 累计死亡量
    private BigDecimal mortalityRate; // 累计死亡率
    private BigDecimal accumulatedFeedingAmount; // 累计投喂量
    private BigDecimal accumulatedWeightGain; // 累计增重
    // private Long saleFishNum; // 销售数量
    private BigDecimal saleWeight; // 销售重量
    private BigDecimal avgFishSpec; // 平均规格，目前和fishSpec一个值，现在当销售的规格
    private BigDecimal biologicalAssets; // 存塘生物资产
    private BigDecimal sumBiologicalAssets; // 总生物资产
    private BigDecimal avgBaitCoefficient; // 平均饵料系数

}
