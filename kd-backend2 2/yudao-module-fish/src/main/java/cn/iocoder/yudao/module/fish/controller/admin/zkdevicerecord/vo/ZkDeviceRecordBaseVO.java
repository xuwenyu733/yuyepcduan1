package cn.iocoder.yudao.module.fish.controller.admin.zkdevicerecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
* 设备数据记录 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkDeviceRecordBaseVO {

    @Schema(description = "设备唯一标识", required = true, example = "10267")
    @NotNull(message = "设备唯一标识不能为空")
    private String deviceId;

    @Schema(description = "设备类型ID，关联iot_device_type表", required = true, example = "8877")
    @NotNull(message = "设备类型ID，关联iot_device_type表不能为空")
    private Long deviceTypeId;

    @Schema(description = "记录时间（精确到分钟，按5分钟间隔存储）", required = true)
    @NotNull(message = "记录时间（精确到分钟，按5分钟间隔存储）不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime recordTime;

    @Schema(description = "监测指标ID，关联iot_monitor_metric表", required = true, example = "27362")
    @NotNull(message = "监测指标ID，关联iot_monitor_metric表不能为空")
    private String metricId;

    @Schema(description = "指标值", required = true)
    @NotNull(message = "指标值不能为空")
    private BigDecimal metricValue;

    @Schema(description = "数据状态（1：正常，2：异常，3：已处理）", required = true, example = "2")
    @NotNull(message = "数据状态（1：正常，2：异常，3：已处理）不能为空")
    private Byte dataStatus;

    @Schema(description = "原始数据（保留原始上传数据）")
    private String rawData;

    @Schema(description = "采集方式（auto：自动定时，manual：手动上传）")
    private String collectionMethod;

    @Schema(description = "负责人id", example = "8487")
    private Long userId;

    @Schema(description = "所属部门", example = "13484")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "948")
    private String fid;

}