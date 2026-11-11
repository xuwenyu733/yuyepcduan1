package cn.iocoder.yudao.module.fish.controller.admin.zkdeviceproperties.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 设备属性记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDevicePropertiesPageReqVO extends PageParam {

    @Schema(description = "设备唯一标识", example = "21451")
    private String deviceId;

    @Schema(description = "设备类型ID，关联iot_device_type表", example = "26889")
    private Long deviceTypeId;

    @Schema(description = "记录时间（精确到分钟，按5分钟间隔存储）")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] recordTime;

    @Schema(description = "监测指标ID，关联iot_monitor_metric表", example = "26617")
    private String metricId;

    @Schema(description = "指标值")
    private BigDecimal metricValue;

    @Schema(description = "数据状态（1：正常，2：异常，3：已处理）", example = "2")
    private Byte dataStatus;

    @Schema(description = "原始数据（保留原始上传数据）")
    private String rawData;

    @Schema(description = "设备属性JSON数据")
    private String deviceProperties;

    @Schema(description = "设备IMEI号")
    private String imei;

    @Schema(description = "设备固件版本")
    private String firmwareVersion;

    @Schema(description = "纬度位置")
    private BigDecimal latitude;

    @Schema(description = "经度位置")
    private BigDecimal longitude;

    @Schema(description = "采集方式（auto：自动定时，manual：手动上传）")
    private String collectionMethod;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "32162")
    private Long userId;

    @Schema(description = "所属部门", example = "6491")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "32225")
    private String fid;

}