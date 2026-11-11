package cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.to;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 加州鲈精准投喂公式：
 * Q日 = BM × R基础 × fT × fDO × fwater × fbehavior
 */
public final class PrecisionFeedCalculator {

    private PrecisionFeedCalculator() {
    }

    public static class Input {
        public int effectiveCount;
        /** 平均尾重 g */
        public double averageWeightG;
        public double temperature;
        public double dissolvedOxygen;
        public double ammonia;
        public double nitrite;
        /** 摄食行为分 0-100，缺省 90 */
        public double feedingBehavior = 90;
        public Integer dailyFrequency;
        public String feedType;
    }

    public static class Result {
        public BigDecimal totalBiomassKg;
        public BigDecimal baseFeedRate;
        public BigDecimal temperatureCoefficient;
        public BigDecimal doCoefficient;
        public BigDecimal waterQualityCoefficient;
        public BigDecimal feedingBehaviorCoefficient;
        public BigDecimal dailyFeedAmountKg;
        public BigDecimal singleFeedAmountKg;
        public int dailyFrequency;
        public String feedType;
        public String recommendation;
        public List<String> warnings = new ArrayList<>();
    }

    public static Result calculate(Input in) {
        Result r = new Result();
        double bm = (in.effectiveCount * in.averageWeightG) / 1000.0;
        double baseRate = baseFeedRate(in.averageWeightG);
        double fT = temperatureCoefficient(in.temperature);
        double fDO = doCoefficient(in.dissolvedOxygen);
        double fWater = waterQualityCoefficient(in.ammonia, in.nitrite);
        double fBehavior = feedingBehaviorCoefficient(in.feedingBehavior);
        int freq = in.dailyFrequency != null ? in.dailyFrequency : recommendFrequency(in.averageWeightG);
        double daily = bm * (baseRate / 100.0) * fT * fDO * fWater * fBehavior;
        double single = freq > 0 ? daily / freq : daily;

        r.totalBiomassKg = bd(bm);
        r.baseFeedRate = bd(baseRate);
        r.temperatureCoefficient = bd(fT);
        r.doCoefficient = bd(fDO);
        r.waterQualityCoefficient = bd(fWater);
        r.feedingBehaviorCoefficient = bd(fBehavior);
        r.dailyFeedAmountKg = bd(daily);
        r.singleFeedAmountKg = bd(single);
        r.dailyFrequency = freq;
        r.feedType = in.feedType != null ? in.feedType : recommendFeedType(in.averageWeightG);
        r.warnings = new ArrayList<>();
        r.recommendation = buildRecommendation(fT, fDO, fWater, fBehavior, in.averageWeightG, r.warnings);
        return r;
    }

    public static int recommendFrequency(double averageWeightG) {
        if (averageWeightG < 50) return 5;
        if (averageWeightG <= 200) return 3;
        return 2;
    }

    public static String recommendFeedType(double averageWeightG) {
        if (averageWeightG < 20) return "开口料/微粒料";
        if (averageWeightG < 50) return "1.0mm 膨化料";
        if (averageWeightG < 150) return "2.0mm 膨化料";
        if (averageWeightG < 300) return "3.0mm 膨化料";
        return "4.0mm+ 成鱼料";
    }

    public static double baseFeedRate(double averageWeight) {
        if (averageWeight < 2.5) return 6.0;
        if (averageWeight <= 3.33) return 9.0;
        if (averageWeight <= 5.0) return 10.0;
        if (averageWeight <= 6.67) return 9.0;
        if (averageWeight <= 10.0) return 7.5;
        if (averageWeight <= 14.29) return 6.0;
        if (averageWeight <= 25.0) return 5.0;
        if (averageWeight <= 50.0) return 3.0;
        if (averageWeight <= 100.0) return 2.7;
        if (averageWeight <= 150.0) return 2.5;
        if (averageWeight <= 200.0) return 2.0;
        if (averageWeight <= 250.0) return 1.6;
        if (averageWeight <= 294.12) return 1.3;
        if (averageWeight <= 357.14) return 1.1;
        if (averageWeight <= 384.62) return 1.0;
        if (averageWeight <= 454.55) return 0.9;
        return 0.8;
    }

    public static double temperatureCoefficient(double temperature) {
        if (temperature < 12) return 0;
        if (temperature < 16) return 0.4;
        if (temperature < 22) return 0.8;
        if (temperature <= 28) return 1.0;
        if (temperature <= 32) return 0.85;
        return 0.25;
    }

    public static double doCoefficient(double dissolvedOxygen) {
        if (dissolvedOxygen >= 5.0) return 1.0;
        if (dissolvedOxygen >= 4.0) return 0.8;
        if (dissolvedOxygen >= 3.0) return 0.5;
        return 0;
    }

    public static double waterQualityCoefficient(double ammonia, double nitrite) {
        boolean ammoniaSafe = ammonia <= 0.2;
        boolean nitriteSafe = nitrite <= 0.2;
        boolean ammoniaWarning = ammonia > 0.2 && ammonia <= 0.4;
        boolean nitriteWarning = nitrite > 0.2 && nitrite <= 0.5;
        boolean ammoniaDanger = ammonia > 0.4;
        boolean nitriteDanger = nitrite > 0.5;
        if (ammoniaSafe && nitriteSafe) return 1.0;
        if ((ammoniaWarning && nitriteSafe) || (ammoniaSafe && nitriteWarning)) return 0.7;
        if (ammoniaDanger || nitriteDanger || (ammoniaWarning && nitriteWarning)) return 0.4;
        return 0;
    }

    public static double feedingBehaviorCoefficient(double feedingBehavior) {
        if (feedingBehavior >= 85) return 1.0;
        if (feedingBehavior >= 60) return 0.8;
        if (feedingBehavior >= 30) return 0.5;
        return 0;
    }

    /** 健康分 → 良好/关注/介入（与现场健康页阈值对齐） */
    public static String healthLevel(BigDecimal overallRating) {
        if (overallRating == null) return "关注";
        double s = overallRating.doubleValue();
        if (s >= 85) return "良好";
        if (s >= 70) return "关注";
        return "介入";
    }

    private static String buildRecommendation(double fT, double fDO, double fWater, double fBehavior,
                                              double avgWeight, List<String> warnings) {
        StringBuilder recommendation = new StringBuilder();
        if (fT < 0.5) {
            warnings.add("水温过低或过高，严重影响摄食");
            recommendation.append("建议调整水温至适宜范围(22-28°C)。");
        } else if (fT < 0.8) {
            warnings.add("水温偏离最佳范围");
            recommendation.append("水温偏低，酌情减少投喂。");
        }
        if (fDO < 1.0) {
            if (fDO == 0) {
                warnings.add("溶氧严重不足，应停止投喂");
                recommendation.append("紧急增氧！停止投喂直至溶氧恢复。");
            } else if (fDO == 0.5) {
                warnings.add("溶氧不足");
                recommendation.append("加强增氧，减少投喂。");
            } else {
                warnings.add("溶氧偏低");
                recommendation.append("注意增氧设备运行。");
            }
        }
        if (fWater < 1.0) {
            if (fWater == 0) {
                warnings.add("水质严重超标");
                recommendation.append("立即停料并处理水质问题。");
            } else if (fWater < 0.5) {
                warnings.add("水质指标危险");
                recommendation.append("大幅减料，紧急改善水质。");
            } else {
                warnings.add("水质指标警戒");
                recommendation.append("减少投喂，加强水质监控。");
            }
        }
        if (fBehavior < 1.0) {
            if (fBehavior == 0) {
                warnings.add("鱼群完全不摄食");
                recommendation.append("停止投喂，检查鱼群健康状况。");
            } else if (fBehavior == 0.5) {
                warnings.add("摄食欲望差");
                recommendation.append("减少投喂量，观察鱼群状态。");
            } else {
                warnings.add("摄食速度偏慢");
                recommendation.append("适当减少投喂，避免残饵。");
            }
        }
        if (warnings.isEmpty()) {
            recommendation.append("各项指标正常，按推荐量投喂。严控残饵，宁少勿多。");
        }
        if (avgWeight > 200) {
            recommendation.append(" 成鱼阶段，注意上市前1-2天停食。");
        }
        return recommendation.toString();
    }

    private static BigDecimal bd(double v) {
        return BigDecimal.valueOf(v).setScale(3, RoundingMode.HALF_UP);
    }
}
