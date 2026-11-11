package cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 精准投喂鱼池列表 Response VO")
@Data
public class PrecisionFeedingPondRespVO {

    @Schema(description = "鱼池ID")
    private Long id;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "鱼池名称")
    private String pondName;

    @Schema(description = "车间名称")
    private String workshopName;

    @Schema(description = "车间ID")
    private Long workshopId;

    @Schema(description = "系统名称")
    private String pondSystemName;

    @Schema(description = "系统ID")
    private Long pondSystemId;

    @Schema(description = "批次编号（可逗号分隔多批次）")
    private String batchNumber;

    @Schema(description = "鱼类品种")
    private String fishTypeName;

    @Schema(description = "鱼类品种字典值")
    private String fishTypes;

    @Schema(description = "鱼数量")
    private Integer fishNum;

    @Schema(description = "平均体重/规格")
    private BigDecimal fishSpec;

    @Schema(description = "存塘规模(m³)")
    private String pondScale;

    @Schema(description = "总生物量")
    private BigDecimal totalWeight;

    @Schema(description = "实际饵料系数")
    private String acturalRadio;

    @Schema(description = "预估饵料系数")
    private String predictRadio;

    @Schema(description = "累计投喂量")
    private BigDecimal totalCount;

    @Schema(description = "当日投喂量")
    private BigDecimal dayTotalCount;

    @Schema(description = "鱼池状态")
    private String pondStatus;

    @Schema(description = "警报ID")
    private Long alarmId;

    @Schema(description = "养殖天数")
    private Long age;

    @Schema(description = "成长阶段")
    private String growthStage; // 成长阶段
    @Schema(description = "昨日投喂量")
    private BigDecimal yesterdayTotalCount;
    @Schema(description = "前3日平均投喂量")
    private BigDecimal avgDailyFeed;

    @Schema(description = "累计不同规格饲料投喂量")
    private java.util.List<PrecisionFeedingSpecStatVO> feedSpecStats;

    @Schema(description = "健康等级：良好/关注/介入")
    private String healthLevel;
    @Schema(description = "健康综合分")
    private BigDecimal healthScore;
    @Schema(description = "今日死亡尾数")
    private Integer todayDeathCount;
    @Schema(description = "养殖水体 m³")
    private BigDecimal waterVolume;
    @Schema(description = "平均密度 尾/m³")
    private BigDecimal avgDensity;
    @Schema(description = "水温")
    private BigDecimal temperature;
    @Schema(description = "溶氧")
    private BigDecimal dissolvedOxygen;
    @Schema(description = "氨氮")
    private BigDecimal ammonia;
    @Schema(description = "亚硝酸盐")
    private BigDecimal nitrite;
    @Schema(description = "pH")
    private BigDecimal ph;
    @Schema(description = "推荐日投喂量 kg")
    private BigDecimal recommendFeedKg;
    @Schema(description = "推荐投喂次数")
    private Integer recommendFrequency;

}
