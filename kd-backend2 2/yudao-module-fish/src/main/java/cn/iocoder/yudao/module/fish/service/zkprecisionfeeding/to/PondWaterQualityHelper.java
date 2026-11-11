package cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.to;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicerecord.ZkDeviceRecordDO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 从传感器最近记录解析水质（水温/溶氧/氨氮/亚硝酸盐/pH）。
 */
public final class PondWaterQualityHelper {

    private PondWaterQualityHelper() {
    }

    public static class WaterQuality {
        public BigDecimal temperature = new BigDecimal("25.0");
        public BigDecimal dissolvedOxygen = new BigDecimal("6.0");
        public BigDecimal ammonia = new BigDecimal("0.05");
        public BigDecimal nitrite = new BigDecimal("0.05");
        public BigDecimal ph = new BigDecimal("7.5");
        public boolean fromSensor;
    }

    public static WaterQuality fromRecords(List<ZkDeviceRecordDO> records) {
        WaterQuality wq = new WaterQuality();
        if (records == null || records.isEmpty()) {
            return wq;
        }
        boolean hit = false;
        for (ZkDeviceRecordDO r : records) {
            if (r == null || r.getMetricId() == null || r.getMetricValue() == null) {
                continue;
            }
            String mid = r.getMetricId().toLowerCase();
            BigDecimal val = r.getMetricValue();
            if (mid.contains("do_temp")) {
                wq.temperature = val;
                hit = true;
            } else if (mid.contains("do_oxygen")) {
                wq.dissolvedOxygen = val;
                hit = true;
            } else if (mid.contains("do_nh4") || mid.contains("nh3") || mid.contains("ammonia")) {
                wq.ammonia = val;
                hit = true;
            } else if (mid.contains("no2") || mid.contains("nitrite")) {
                wq.nitrite = val;
                hit = true;
            } else if (mid.contains("ph_ph") || mid.endsWith("_ph") || mid.contains("ph_")) {
                wq.ph = val;
                hit = true;
            }
        }
        wq.fromSensor = hit;
        return wq;
    }
}
