package cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.to;

import java.math.BigDecimal;

/**
 * 鱼类健康评分规格（需求口径）
 * 外部环境 20 + 外观行为 60 + 内部肌体 20 = 100
 */
public final class PondHealthScoreConstants {

    private PondHealthScoreConstants() {}

    /** 外部环境层满分 */
    public static final BigDecimal ENV_FULL = BigDecimal.valueOf(20);
    /** 外观行为层满分 */
    public static final BigDecimal BEHAVIOR_FULL = BigDecimal.valueOf(60);
    /** 内部肌体层满分 */
    public static final BigDecimal BODY_FULL = BigDecimal.valueOf(20);
    /** 综合满分 */
    public static final BigDecimal OVERALL_FULL = BigDecimal.valueOf(100);

    /** 旧版满分（用于兼容重标定） */
    public static final BigDecimal LEGACY_ENV_FULL = BigDecimal.valueOf(40);
    public static final BigDecimal LEGACY_BEHAVIOR_FULL = BigDecimal.valueOf(30);
    public static final BigDecimal LEGACY_BODY_FULL = BigDecimal.valueOf(30);

    // ---- 外观行为层分项满分 ----
    public static final int FOOD_INTAKE_AMOUNT_FULL = 15;
    public static final int FOOD_INTAKE_DEGREE_FULL = 10;
    public static final int BODY_SHAPE_FULL = 5;
    public static final int BODY_COLOR_FULL = 5;
    public static final int INJURY_FULL = 5;
    public static final int SWIMMING_FULL = 10;
    public static final int DEATH_FULL = 10;

    /** 死亡情况参照的默认单日死亡尾数预警阈值（可被 yudao.fish.health.death-warn-daily-count 覆盖） */
    public static final int DEFAULT_DEATH_WARN_THRESHOLD = 10;

    /** 等级：优秀 */
    public static final BigDecimal GRADE_EXCELLENT_MIN = BigDecimal.valueOf(85);
    /** 等级：良好下限 */
    public static final BigDecimal GRADE_GOOD_MIN = BigDecimal.valueOf(70);
}
