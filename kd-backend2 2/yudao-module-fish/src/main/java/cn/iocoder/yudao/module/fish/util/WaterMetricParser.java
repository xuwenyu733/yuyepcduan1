package cn.iocoder.yudao.module.fish.util;

import cn.iocoder.yudao.module.fish.controller.admin.daping.to.DaPingMonitorRowVO;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 从 zk_device_record 最新水质指标行解析 temp / ph / oxygen / ammonia / 浊度 / ORP 等
 */
public final class WaterMetricParser {

    private WaterMetricParser() {
    }

    public static Map<String, BigDecimal> parseLatest(List<DaPingMonitorRowVO.WaterMetricRow> rows) {
        Map<String, BigDecimal> latest = new LinkedHashMap<>();
        if (rows == null) {
            return latest;
        }
        for (DaPingMonitorRowVO.WaterMetricRow row : rows) {
            if (row == null || row.getMetricId() == null || row.getMetricValue() == null) {
                continue;
            }
            String mid = row.getMetricId().toLowerCase(Locale.ROOT);
            BigDecimal val = row.getMetricValue();
            String family;
            if (mid.contains("do_temp") || mid.contains("water_temp")) {
                family = "temp";
            } else if (mid.contains("do_oxygen") || mid.contains("do_oxy") || mid.endsWith("_oxygen_1")) {
                family = "oxygen";
            } else if (mid.contains("do_nh4") || mid.contains("nh3") || mid.contains("ammonia")) {
                family = "ammonia";
            } else if (mid.contains("ph_ph") || mid.startsWith("ph_")) {
                family = "ph";
            } else if (mid.contains("ntu") || mid.contains("turbid")) {
                family = "turbidity";
            } else if (mid.contains("orp")) {
                family = "orp";
            } else if (mid.contains("tss")) {
                family = "tss";
            } else if (mid.contains("wq_alk") || mid.startsWith("alk_") || mid.contains("alkalin")) {
                family = "alkalinity";
            } else if (mid.contains("wq_hard") || mid.startsWith("hard_") || mid.contains("hardness")) {
                family = "hardness";
            } else if (mid.contains("nitrite") || mid.contains("no2")) {
                family = "nitrite";
            } else if (mid.contains("nitrate") || mid.contains("no3") || mid.startsWith("wq_no3")) {
                family = "nitrate";
            } else if (mid.contains("wq_co2") || mid.startsWith("co2_co2") || mid.equals("co2_1")) {
                family = "co2";
            } else if (mid.contains("wq_tp") || mid.startsWith("tp_tp") || mid.startsWith("tp_") || mid.contains("total_p")) {
                family = "tp";
            } else if (mid.contains("wq_tn") || mid.startsWith("tn_tn") || mid.startsWith("tn_") || mid.contains("total_n")) {
                family = "tn";
            } else if (mid.contains("water_level") || mid.contains("lel_level")) {
                family = "level";
            } else {
                continue;
            }
            if (!latest.containsKey(family) || mid.endsWith("_1") || mid.contains("_1")) {
                latest.put(family, val);
            }
        }
        return latest;
    }

    public static String formatPlain(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }
}
