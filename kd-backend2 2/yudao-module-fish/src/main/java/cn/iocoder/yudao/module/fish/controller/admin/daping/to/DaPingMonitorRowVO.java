package cn.iocoder.yudao.module.fish.controller.admin.daping.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 大屏监控 Mapper 行（避免 Map&lt;String,Object&gt; 触发 JacksonTypeHandler 误解析字符串列）
 */
public final class DaPingMonitorRowVO {

    private DaPingMonitorRowVO() {
    }

    @Data
    public static class WaterMetricRow {
        private String metricId;
        private BigDecimal metricValue;
    }

    @Data
    public static class WaterTrendRow {
        private String timeBucket;
        private BigDecimal temp;
        private BigDecimal oxygen;
        private BigDecimal ph;
    }

    @Data
    public static class DeviceStatusRow {
        private String name;
        private String status;
        private String workshop;
        private BigDecimal currValue;
        private String unit;
        private String pondDetailNum;
    }

    @Data
    public static class NameCountRow {
        private String name;
        private Long cnt;
    }

    @Data
    public static class EnergyRow {
        private String name;
        private BigDecimal value;
        private String color;
    }

    @Data
    public static class BackwashRow {
        private String pool;
        private BigDecimal count;
        private String status;
        private String device;
        private String workshop;
        private String system;
    }

    @Data
    public static class AeratorRow {
        private String pool;
        private BigDecimal share;
        private String workshop;
        private String system;
    }

    @Data
    public static class Co2SegRow {
        private String device;
        /** 避免 SQL 别名 start/end 与保留字冲突（MyBatis-Plus 解析失败） */
        private String startHm;
        private String endHm;
        private String workshop;
        private String system;
        private String module;
    }

    @Data
    public static class WaterFlowRow {
        private String name;
        private BigDecimal flow;
        private String workshop;
        private String system;
    }

    @Data
    public static class EnergyTrendRow {
        private String timeBucket;
        private String deviceType;
        private BigDecimal value;
    }

    @Data
    public static class ExtendedAlarmRow {
        private Long id;
        private String category;
        private String title;
        private String message;
        private String suggestion;
        private String level;
        private String status;
        private String location;
        private String owner;
        private String alarmTime;
    }

    @Data
    public static class ScheduleEventRow {
        private String category;
        private Integer hourOfDay;
        private String eventText;
        private Integer completed;
    }
}
