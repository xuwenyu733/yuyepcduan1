package cn.iocoder.yudao.module.fish.controller.admin.zkdevice.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

/**
 * @创建人 lr
 * @创建时间 2025-05-21
 * @描述
 */
@Data
@NoArgsConstructor
public class DeviceEnvInfoVo {

    private String deviceValue; // 当前设备的值
    private String waterLevel; // 水位
    private String waterTemperature; // 水温
    private String phValue; // 酸碱度
    private String moltenOxygenConcentration; // 熔氧浓度
    private String ammoniaNitrogen; // 氨氮

    public DeviceEnvInfoVo(String deviceValue, String waterLevel, String waterTemperature, String phValue, String moltenOxygenConcentration, String ammoniaNitrogen) {
        this.deviceValue = deviceValue;
        this.waterLevel = waterLevel;
        this.waterTemperature = waterTemperature;
        this.phValue = phValue;
        this.moltenOxygenConcentration = moltenOxygenConcentration;
        this.ammoniaNitrogen = ammoniaNitrogen;
    }

    public static DeviceEnvInfoVo gen() {

        Random random = new Random();
        random.nextInt(8);
        String waterLevel = new StringBuilder("3.8").append(random.nextInt(8)).append(" m").toString();
        String waterTemperature = new StringBuilder("2").append(random.nextInt(8)).append(" ℃").toString();
        String phValue = new StringBuilder("7.").append(random.nextInt(8)).append(" PH").toString();
        String moltenOxygenConcentration = new StringBuilder("").append(random.nextInt(8)).append(" mg/l").toString();
        String ammoniaNitrogen = new StringBuilder("1.").append(random.nextInt(8)).append(" mg/l").toString();
        String deviceValue = new StringBuilder("2.").append(random.nextInt(8)).append("").toString();
        DeviceEnvInfoVo vo = new DeviceEnvInfoVo(deviceValue, waterLevel, waterTemperature, phValue, moltenOxygenConcentration, ammoniaNitrogen);
        return vo;
    }

    /** 从 zk_device_record 最新水质指标构建设备环境信息 */
    public static DeviceEnvInfoVo fromWaterMetrics(Map<String, BigDecimal> latest, String deviceValue) {
        String dv = deviceValue != null ? deviceValue : formatMetric(latest != null ? latest.get("temp") : null, "");
        Map<String, BigDecimal> m = latest != null ? latest : java.util.Collections.emptyMap();
        return new DeviceEnvInfoVo(
                dv,
                formatMetric(m.get("level"), " m"),
                formatMetric(m.get("temp"), " ℃"),
                formatMetric(m.get("ph"), " PH"),
                formatMetric(m.get("oxygen"), " mg/l"),
                formatMetric(m.get("ammonia"), " mg/l")
        );
    }

    private static String formatMetric(BigDecimal value, String suffix) {
        if (value == null) {
            return "0" + (suffix != null ? suffix : "");
        }
        return value.stripTrailingZeros().toPlainString() + (suffix != null ? suffix : "");
    }

}
