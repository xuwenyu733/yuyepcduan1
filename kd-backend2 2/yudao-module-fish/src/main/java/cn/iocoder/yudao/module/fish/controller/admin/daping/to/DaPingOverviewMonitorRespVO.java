package cn.iocoder.yudao.module.fish.controller.admin.daping.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 大屏监控面板（水质 / 设备 / 故障 / 能耗）——来自库表，非演示数据。
 */
@Data
public class DaPingOverviewMonitorRespVO {

    /** 水质最新采样时间（传感器记录） */
    private String waterAsOf;

    private List<WaterMetricItem> waterRealtime = new ArrayList<>();

    private List<WaterTrendItem> waterTrend = new ArrayList<>();

    /** 近 7 天按日水质趋势 */
    private List<WaterTrendItem> waterTrend7d = new ArrayList<>();

    /** 近 30 天按日水质趋势 */
    private List<WaterTrendItem> waterTrend30d = new ArrayList<>();

    private List<DeviceStatusItem> devices = new ArrayList<>();

    private List<NameCountItem> faultMonth = new ArrayList<>();

    /**
     * 能耗分项：优先 zk_energy_meter_reading（kWh）；
     * 无电度数据时回退经营成本/净化成本（元），energyNote 说明单位。
     */
    private List<EnergyItem> energy = new ArrayList<>();

    private String energyNote;

    /** 电度按日趋势（来自 zk_energy_meter_reading） */
    private List<EnergyTrendItem> energyTrend = new ArrayList<>();

    private List<BackwashItem> backwash = new ArrayList<>();
    private List<AeratorItem> aerator = new ArrayList<>();
    private List<Co2SegItem> co2 = new ArrayList<>();
    private List<WaterFlowItem> waterFlow = new ArrayList<>();

    @Data
    public static class WaterMetricItem {
        private String name;
        private String unit;
        private BigDecimal value;
        private String range;
        private String status;
        private String key;
    }

    @Data
    public static class WaterTrendItem {
        private String time;
        private BigDecimal temp;
        private BigDecimal oxygen;
        private BigDecimal ph;
    }

    @Data
    public static class DeviceStatusItem {
        private String name;
        private String status;
        private String workshop;
        private BigDecimal voltage;
        private BigDecimal current;
        private BigDecimal frequency;
        private BigDecimal currValue;
        private String unit;
        private String pondDetailNum;
    }

    @Data
    public static class NameCountItem {
        private String name;
        private Long count;
    }

    @Data
    public static class EnergyItem {
        private String name;
        private BigDecimal value;
        private String color;
    }

    @Data
    public static class EnergyTrendItem {
        private String time;
        private String deviceType;
        private BigDecimal value;
    }

    @Data
    public static class BackwashItem {
        private String pool;
        private BigDecimal count;
        private String status;
        private String device;
        private String workshop;
        private String system;
    }

    @Data
    public static class AeratorItem {
        private String pool;
        private BigDecimal share;
        private String workshop;
        private String system;
    }

    @Data
    public static class Co2SegItem {
        private String device;
        private String start;
        private String end;
        private String workshop;
        private String system;
        private String module;
    }

    @Data
    public static class WaterFlowItem {
        private String name;
        private BigDecimal flow;
        private String workshop;
        private String system;
    }
}
