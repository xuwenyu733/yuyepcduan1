package cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.to;

import lombok.Builder;
import lombok.Data;

/**
 * 死亡分项计分上下文：真实今日死亡数 + 预警阈值。
 */
@Data
@Builder
public class DeathScoreContext {

    /** 单日死亡尾数预警阈值 */
    private Integer deathWarnDailyCount;

    /** 该池今日真实死亡尾数（来自 zk_pond_fish_detail）；为空则回退解析文案 */
    private Integer todayDeathCount;

    public static DeathScoreContext defaults() {
        return DeathScoreContext.builder()
                .deathWarnDailyCount(PondHealthScoreConstants.DEFAULT_DEATH_WARN_THRESHOLD)
                .build();
    }

    public int resolvedWarnThreshold() {
        if (deathWarnDailyCount == null || deathWarnDailyCount < 0) {
            return PondHealthScoreConstants.DEFAULT_DEATH_WARN_THRESHOLD;
        }
        return deathWarnDailyCount;
    }
}
