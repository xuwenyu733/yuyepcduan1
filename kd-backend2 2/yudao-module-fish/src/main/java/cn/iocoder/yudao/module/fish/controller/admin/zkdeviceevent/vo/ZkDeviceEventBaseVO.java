package cn.iocoder.yudao.module.fish.controller.admin.zkdeviceevent.vo;

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
* 设备事件记录 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkDeviceEventBaseVO {

    @Schema(description = "设备唯一标识", required = true, example = "16283")
    @NotNull(message = "设备唯一标识不能为空")
    private String deviceId;

    @Schema(description = "设备类型ID，关联iot_device_type表", example = "30272")
    private Long deviceTypeId;

    @Schema(description = "记录时间（精确到分钟，按5分钟间隔存储）", required = true)
    @NotNull(message = "记录时间（精确到分钟，按5分钟间隔存储）不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime recordTime;

    @Schema(description = "监测指标ID，关联iot_monitor_metric表", example = "8861")
    private String metricId;

    @Schema(description = "指标值")
    private BigDecimal metricValue;

    @Schema(description = "数据状态（1：正常，2：异常，3：已处理）", example = "1")
    private Byte dataStatus;

    @Schema(description = "原始数据（保留原始上传数据）")
    private String rawData;

    @Schema(description = "设备事件JSON数据", required = true)
    @NotNull(message = "设备事件JSON数据不能为空")
    private String deviceEvent;

    @Schema(description = "负责人id", example = "25899")
    private Long userId;

    @Schema(description = "所属部门", example = "28583")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "17172")
    private String fid;

}