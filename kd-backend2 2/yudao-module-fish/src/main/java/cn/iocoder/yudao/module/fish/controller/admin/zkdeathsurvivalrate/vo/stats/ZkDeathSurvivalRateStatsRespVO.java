package cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate.vo.stats;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "管理后台 - 死亡和存活率统计 Response VO")
public class ZkDeathSurvivalRateStatsRespVO {

    @Schema(description = "顶部统计")
    private TopStats topStats;

    @Schema(description = "7天死亡趋势")
    private TrendStats trendStats;

    @Schema(description = "各鱼池死亡对比和详情")
    private List<PoolStats> poolStatsList;

    @Data
    @Schema(description = "顶部统计")
    public static class TopStats {
        @Schema(description = "今日死亡量")
        private Integer dayDead;
        
        @Schema(description = "本周死亡量")
        private Integer weekDead;
        
        @Schema(description = "本月死亡数")
        private Integer monthDead;
        @Schema(description = "共死亡数")
        private Long sumDead;
        @Schema(description = "存塘鱼数")
        private Long sumFishNum;

        @Schema(description = "平均死亡率")
        private BigDecimal avgDeadPerEvent;

        public TopStats build_avgDeadPerEvent(Long sumPondFishNum) {
            this.sumFishNum = sumPondFishNum;
            this.sumDead = Long.parseLong(this.avgDeadPerEvent+"");
            BigDecimal sum = EasyBigDecimalUtil.add(this.avgDeadPerEvent, new BigDecimal(sumPondFishNum));
            this.avgDeadPerEvent = EasyBigDecimalUtil.divide(this.avgDeadPerEvent, sum);
            return this;
        }
    }

    @Data
    @Schema(description = "7天死亡趋势")
    public static class TrendStats {
        private Integer deathCountYesterday;
        private BigDecimal mortalityRateYesterday;
        
        private Integer deathCountTwoDaysAgo;
        private BigDecimal mortalityRateTwoDaysAgo;
        
        private Integer deathCountThreeDaysAgo;
        private BigDecimal mortalityRateThreeDaysAgo;
        
        private Integer deathCountFourDaysAgo;
        private BigDecimal mortalityRateFourDaysAgo;
        
        private Integer deathCountFiveDaysAgo;
        private BigDecimal mortalityRateFiveDaysAgo;
        
        private Integer deathCountSixDaysAgo;
        private BigDecimal mortalityRateSixDaysAgo;
        
        private Integer deathCountSevenDaysAgo;
        private BigDecimal mortalityRateSevenDaysAgo;
    }

    @Data
    @Schema(description = "鱼池统计")
    public static class PoolStats {
        @Schema(description = "鱼池ID")
        private Long pondId;
        
        @Schema(description = "鱼池名称")
        private String pondName;
        
        @Schema(description = "今日死亡量")
        private Integer dayDead;
        
        @Schema(description = "本周死亡量")
        private Integer weekDead;
        
        @Schema(description = "本月死亡数")
        private Integer monthDead;

        @Schema(description = "累计死亡数（死亡存活率台账）")
        private Integer deathQuantity;

        @Schema(description = "存活率")
        private Object survivalRate;
    }
}
