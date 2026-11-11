package cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.to;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondhealth.ZkPondHealthDO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 按需求口径重算三层得分与异常汇总。
 * <p>
 * 不改表结构：分项得分写入各层 information JSON 的 itemScores 字段。
 * 兼容旧数据：仅有「正常/异常」标签或旧 40/30/30 满分时自动映射。
 */
@Slf4j
public final class PondHealthScoreCalculator {

    private static final Pattern NUMBER = Pattern.compile("(\\d+(?:\\.\\d+)?)");

    private PondHealthScoreCalculator() {}

    /**
     * 就地补齐/纠正 DO 上的三层分、综合分、异常信息与 information JSON。
     */
    public static void enrich(ZkPondHealthDO health) {
        enrich(health, DeathScoreContext.defaults());
    }

    public static void enrich(ZkPondHealthDO health, DeathScoreContext deathCtx) {
        if (health == null) {
            return;
        }
        DeathScoreContext ctx = deathCtx != null ? deathCtx : DeathScoreContext.defaults();
        try {
            JSONObject envInfo = parseObj(health.getEnvironmentalLayerInformation());
            JSONObject behaviorInfo = parseObj(health.getBehavioralLayerInformation());
            JSONObject bodyInfo = parseObj(health.getBodyLayerInformation());

            normalizeBehaviorFields(behaviorInfo);
            applyRealDeathCount(behaviorInfo, ctx);

            ScorePart env = scoreEnvironment(envInfo, health.getEnvironmentalLayerRating());
            ScorePart behavior = scoreBehavior(behaviorInfo, ctx);
            ScorePart body = scoreBody(bodyInfo, health.getBodyLayerRating());

            envInfo.put("itemScores", env.itemScores);
            envInfo.put("layerFull", PondHealthScoreConstants.ENV_FULL);
            behaviorInfo.put("itemScores", behavior.itemScores);
            behaviorInfo.put("layerFull", PondHealthScoreConstants.BEHAVIOR_FULL);
            behaviorInfo.put("deathWarnThreshold", ctx.resolvedWarnThreshold());
            if (ctx.getTodayDeathCount() != null) {
                behaviorInfo.put("todayDeathCount", ctx.getTodayDeathCount());
            }
            bodyInfo.put("itemScores", body.itemScores);
            bodyInfo.put("layerFull", PondHealthScoreConstants.BODY_FULL);

            BigDecimal overall = env.score.add(behavior.score).add(body.score)
                    .setScale(2, RoundingMode.HALF_UP);

            List<String> exceptions = new ArrayList<>();
            exceptions.addAll(env.exceptions);
            exceptions.addAll(behavior.exceptions);
            exceptions.addAll(body.exceptions);

            health.setEnvironmentalLayerRating(env.score);
            health.setBehavioralLayerRating(behavior.score);
            health.setBodyLayerRating(body.score);
            health.setOverallRating(overall);
            health.setEnvironmentalLayerInformation(envInfo.toJSONString());
            health.setBehavioralLayerInformation(behaviorInfo.toJSONString());
            health.setBodyLayerInformation(bodyInfo.toJSONString());
            health.setExceptionMessage(String.join("\n", exceptions));
            health.setNumberOfExceptions(exceptions.size());
        } catch (Exception e) {
            log.warn("[PondHealthScoreCalculator] enrich failed, id={}", health.getId(), e);
        }
    }

    /** 有真实今日死亡数时，覆盖 death 文案，保证与预警阈值可比 */
    private static void applyRealDeathCount(JSONObject behaviorInfo, DeathScoreContext ctx) {
        if (ctx.getTodayDeathCount() == null) {
            return;
        }
        int n = Math.max(0, ctx.getTodayDeathCount());
        if (n <= 0) {
            behaviorInfo.put("death", "无");
        } else {
            behaviorInfo.put("death", "今日死亡" + n + "尾");
        }
        behaviorInfo.put("deadFishSituation", behaviorInfo.getString("death"));
    }

    private static JSONObject parseObj(String raw) {
        if (StrUtil.isBlank(raw)) {
            return new JSONObject();
        }
        try {
            Object parsed = JSON.parse(raw);
            if (parsed instanceof JSONObject) {
                return (JSONObject) parsed;
            }
            if (parsed instanceof String) {
                Object nested = JSON.parse((String) parsed);
                if (nested instanceof JSONObject) {
                    return (JSONObject) nested;
                }
            }
        } catch (Exception ignored) {
            // fall through
        }
        return new JSONObject();
    }

    /** 把旧字段映射到新分项字段，便于前端统一读取 */
    private static void normalizeBehaviorFields(JSONObject info) {
        putIfAbsent(info, "foodIntakeAmount", firstNonBlank(info, "foodIntakeAmount", "foodSituation", "摄食量"));
        putIfAbsent(info, "foodIntakeDegree", firstNonBlank(info, "foodIntakeDegree", "foodSituation", "摄食程度"));
        putIfAbsent(info, "bodyShape", firstNonBlank(info, "bodyShape", "体型"));
        putIfAbsent(info, "bodyColor", firstNonBlank(info, "bodyColor", "bodyColorMucus", "体色"));
        putIfAbsent(info, "injury", firstNonBlank(info, "injury", "损伤"));
        putIfAbsent(info, "swimming", firstNonBlank(info, "swimming", "swimmingState", "游泳情况", "游动状态"));
        putIfAbsent(info, "death", firstNonBlank(info, "death", "deadFishSituation", "死亡情况", "死鱼情况"));
        // 缺省：体型/损伤未录入时按正常占位，避免整层无分
        if (StrUtil.isBlank(info.getString("bodyShape"))) {
            info.put("bodyShape", "正常");
        }
        if (StrUtil.isBlank(info.getString("injury"))) {
            info.put("injury", "无");
        }
    }

    private static void putIfAbsent(JSONObject info, String key, String value) {
        if (StrUtil.isBlank(info.getString(key)) && StrUtil.isNotBlank(value)) {
            info.put(key, value);
        }
    }

    private static String firstNonBlank(JSONObject info, String... keys) {
        for (String key : keys) {
            String v = info.getString(key);
            if (StrUtil.isNotBlank(v)) {
                return v;
            }
        }
        return null;
    }

    private static ScorePart scoreEnvironment(JSONObject info, BigDecimal legacyRating) {
        ScorePart part = new ScorePart();
        JSONObject scores = new JSONObject();
        // 6 项合计 20：4+4+3+3+3+3
        int doScore = scoreWaterMetric("溶氧 DO", info.get("doOxygen"), 4,
                v -> v >= 5 ? 4 : (v >= 3 ? 2 : 0), part.exceptions);
        int nitriteScore = scoreWaterMetric("亚硝酸盐", info.get("nitrite"), 4,
                v -> v <= 0.1 ? 4 : (v <= 0.2 ? 2 : 0), part.exceptions);
        int ammoniaScore = scoreWaterMetric("氨氮", info.get("ammoniaNitrogen"), 3,
                v -> v <= 0.5 ? 3 : (v <= 1.0 ? 1 : 0), part.exceptions);
        int phScore = scoreWaterMetric("pH", info.get("phPh"), 3,
                v -> (v >= 6.5 && v <= 8.5) ? 3 : ((v >= 6.0 && v <= 9.0) ? 1 : 0), part.exceptions);
        int tempScore = scoreWaterMetric("水温", info.get("doTemp"), 3,
                v -> (v >= 18 && v <= 30) ? 3 : ((v >= 15 && v <= 32) ? 1 : 0), part.exceptions);
        int nitrateScore = scoreWaterMetric("硝酸盐", info.get("nitrate"), 3,
                v -> v <= 50 ? 3 : (v <= 80 ? 1 : 0), part.exceptions);

        scores.put("doOxygen", doScore);
        scores.put("nitrite", nitriteScore);
        scores.put("ammoniaNitrogen", ammoniaScore);
        scores.put("phPh", phScore);
        scores.put("doTemp", tempScore);
        scores.put("nitrate", nitrateScore);

        boolean hasAnyMetric = info.get("doOxygen") != null || info.get("nitrite") != null
                || info.get("ammoniaNitrogen") != null || info.get("phPh") != null
                || info.get("doTemp") != null || info.get("nitrate") != null;
        if (!hasAnyMetric && legacyRating != null) {
            // 无明细时按旧满分 40 重标定到 20
            part.score = rescale(legacyRating, PondHealthScoreConstants.LEGACY_ENV_FULL,
                    PondHealthScoreConstants.ENV_FULL);
        } else {
            part.score = BigDecimal.valueOf(doScore + nitriteScore + ammoniaScore + phScore + tempScore + nitrateScore);
        }
        part.itemScores = scores;
        return part;
    }

    private static ScorePart scoreBehavior(JSONObject info, DeathScoreContext ctx) {
        ScorePart part = new ScorePart();
        JSONObject scores = new JSONObject();

        int foodAmount = statusToScore(info.getString("foodIntakeAmount"),
                PondHealthScoreConstants.FOOD_INTAKE_AMOUNT_FULL, "摄食量", part.exceptions);
        int foodDegree = statusToScore(info.getString("foodIntakeDegree"),
                PondHealthScoreConstants.FOOD_INTAKE_DEGREE_FULL, "摄食程度", part.exceptions);
        int bodyShape = statusToScore(info.getString("bodyShape"),
                PondHealthScoreConstants.BODY_SHAPE_FULL, "体型", part.exceptions);
        int bodyColor = statusToScore(info.getString("bodyColor"),
                PondHealthScoreConstants.BODY_COLOR_FULL, "体色", part.exceptions);
        int injury = statusToScore(info.getString("injury"),
                PondHealthScoreConstants.INJURY_FULL, "损伤", part.exceptions);
        int swimming = statusToScore(info.getString("swimming"),
                PondHealthScoreConstants.SWIMMING_FULL, "游泳情况", part.exceptions);
        int death = scoreDeath(info.getString("death"), ctx, part.exceptions);

        scores.put("foodIntakeAmount", foodAmount);
        scores.put("foodIntakeDegree", foodDegree);
        scores.put("bodyShape", bodyShape);
        scores.put("bodyColor", bodyColor);
        scores.put("injury", injury);
        scores.put("swimming", swimming);
        scores.put("death", death);

        part.score = BigDecimal.valueOf(foodAmount + foodDegree + bodyShape + bodyColor + injury + swimming + death);
        part.itemScores = scores;
        return part;
    }

    private static ScorePart scoreBody(JSONObject info, BigDecimal legacyRating) {
        ScorePart part = new ScorePart();
        JSONObject scores = new JSONObject();
        // 5 项各 4 分 = 20
        int liver = statusToScore(info.getString("liverAndGallbladder"), 4, "肝脏与胆囊", part.exceptions);
        int gill = statusToScore(info.getString("gillFilaments"), 4, "鳃丝(镜检)", part.exceptions);
        int intestine = statusToScore(info.getString("intestine"), 4, "肠道", part.exceptions);
        int nodule = statusToScore(info.getString("visceralNodule"), 4, "内脏结节", part.exceptions);
        int spec = statusToScore(info.getString("specificationAndBaitCoefficient"), 4, "规格与饵料系数", part.exceptions);

        scores.put("liverAndGallbladder", liver);
        scores.put("gillFilaments", gill);
        scores.put("intestine", intestine);
        scores.put("visceralNodule", nodule);
        scores.put("specificationAndBaitCoefficient", spec);

        boolean hasAny = StrUtil.isNotBlank(info.getString("liverAndGallbladder"))
                || StrUtil.isNotBlank(info.getString("gillFilaments"))
                || StrUtil.isNotBlank(info.getString("intestine"))
                || StrUtil.isNotBlank(info.getString("visceralNodule"))
                || StrUtil.isNotBlank(info.getString("specificationAndBaitCoefficient"));
        if (!hasAny && legacyRating != null) {
            part.score = rescale(legacyRating, PondHealthScoreConstants.LEGACY_BODY_FULL,
                    PondHealthScoreConstants.BODY_FULL);
        } else if (!hasAny) {
            // 无打样数据：机体层暂给满分并提示，避免拖垮综合分；前端可展示「未检测」
            part.score = PondHealthScoreConstants.BODY_FULL;
            part.exceptions.add("内部肌体层：暂无打样数据，按满分占位");
            scores.put("liverAndGallbladder", 4);
            scores.put("gillFilaments", 4);
            scores.put("intestine", 4);
            scores.put("visceralNodule", 4);
            scores.put("specificationAndBaitCoefficient", 4);
            scores.put("placeholder", true);
        } else {
            part.score = BigDecimal.valueOf(liver + gill + intestine + nodule + spec);
        }
        part.itemScores = scores;
        return part;
    }

    private static int scoreDeath(String raw, DeathScoreContext ctx, List<String> exceptions) {
        int warn = ctx.resolvedWarnThreshold();
        // 优先用真实今日死亡数
        if (ctx.getTodayDeathCount() != null) {
            int count = Math.max(0, ctx.getTodayDeathCount());
            if (count <= 0) {
                return PondHealthScoreConstants.DEATH_FULL;
            }
            if (count >= warn) {
                exceptions.add("死亡情况：今日死亡 " + count + " 尾，达到/超过预警阈值 " + warn);
                return 0;
            }
            exceptions.add("死亡情况：今日死亡 " + count + " 尾（预警阈值 " + warn + "，未达阈值酌情扣分）");
            return PondHealthScoreConstants.DEATH_FULL / 2;
        }

        if (StrUtil.isBlank(raw)) {
            return PondHealthScoreConstants.DEATH_FULL;
        }
        String text = raw.trim();
        if (isGoodStatus(text)) {
            return PondHealthScoreConstants.DEATH_FULL;
        }
        Matcher m = NUMBER.matcher(text);
        if (m.find()) {
            double count = Double.parseDouble(m.group(1));
            if (count >= warn) {
                exceptions.add("死亡情况：今日死亡 " + stripTrailingZero(count)
                        + " 尾，达到/超过预警阈值 " + warn);
                return 0;
            }
            if (count > 0) {
                exceptions.add("死亡情况：今日死亡 " + stripTrailingZero(count)
                        + " 尾（预警阈值 " + warn + "，未达阈值酌情扣分）");
                return PondHealthScoreConstants.DEATH_FULL / 2;
            }
            return PondHealthScoreConstants.DEATH_FULL;
        }
        if (isBadStatus(text)) {
            exceptions.add("死亡情况：异常（" + text + "），预警阈值 " + warn);
            return 0;
        }
        return PondHealthScoreConstants.DEATH_FULL / 2;
    }

    private static int statusToScore(String raw, int full, String label, List<String> exceptions) {
        if (StrUtil.isBlank(raw)) {
            return full;
        }
        String text = raw.trim();
        if (isGoodStatus(text)) {
            return full;
        }
        if (isBadStatus(text)) {
            exceptions.add(label + "：异常（" + text + "）");
            return 0;
        }
        // 弱/一般/偏慢等中间态
        if (text.contains("弱") || text.contains("慢") || text.contains("差")
                || text.contains("一般") || text.contains("轻微")) {
            exceptions.add(label + "：偏弱（" + text + "）");
            return Math.max(full / 2, 1);
        }
        // 未知文案：给一半并记异常，便于巡检复核
        exceptions.add(label + "：需复核（" + text + "）");
        return Math.max(full / 2, 1);
    }

    private static boolean isGoodStatus(String text) {
        return "正常".equals(text) || "无".equals(text) || "强".equals(text)
                || "良好".equals(text) || "优秀".equals(text)
                || text.equalsIgnoreCase("ok") || text.equalsIgnoreCase("normal");
    }

    private static boolean isBadStatus(String text) {
        return "异常".equals(text) || "有".equals(text) || text.contains("异常")
                || text.contains("死亡") || text.contains("损伤") || text.contains("充血")
                || text.contains("肿胀");
    }

    private interface WaterRule {
        int apply(double v);
    }

    private static int scoreWaterMetric(String label, Object raw, int full, WaterRule rule, List<String> exceptions) {
        Double v = toDouble(raw);
        if (v == null) {
            return full; // 缺测不扣分
        }
        int score = rule.apply(v);
        if (score < full) {
            exceptions.add(label + "异常/偏弱：" + v);
        }
        return score;
    }

    private static Double toDouble(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number) {
            return ((Number) raw).doubleValue();
        }
        String text = String.valueOf(raw).trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            Matcher m = NUMBER.matcher(text);
            if (m.find()) {
                return Double.parseDouble(m.group(1));
            }
            return null;
        }
    }

    private static BigDecimal rescale(BigDecimal legacy, BigDecimal legacyFull, BigDecimal newFull) {
        if (legacy == null || legacyFull.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return legacy.multiply(newFull).divide(legacyFull, 2, RoundingMode.HALF_UP)
                .min(newFull).max(BigDecimal.ZERO);
    }

    private static String stripTrailingZero(double v) {
        if (v == (long) v) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }

    private static final class ScorePart {
        private BigDecimal score = BigDecimal.ZERO;
        private JSONObject itemScores = new JSONObject();
        private final List<String> exceptions = new ArrayList<>();
    }
}
