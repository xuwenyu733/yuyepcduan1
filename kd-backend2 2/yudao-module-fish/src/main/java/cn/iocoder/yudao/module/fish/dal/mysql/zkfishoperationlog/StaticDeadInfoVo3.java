package cn.iocoder.yudao.module.fish.dal.mysql.zkfishoperationlog;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StaticDeadInfoVo3 {

    private Long pondId;
    private Long sevenDayDeath; // 投放7日死亡
    private Long totalDeath; // 累计死亡
    private Long pondFishNum;
    private Long todayDeath; // 今日死亡

}
