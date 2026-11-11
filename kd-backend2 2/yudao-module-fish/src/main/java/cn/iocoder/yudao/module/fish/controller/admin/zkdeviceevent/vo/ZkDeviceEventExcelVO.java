package cn.iocoder.yudao.module.fish.controller.admin.zkdeviceevent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 设备事件记录 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkDeviceEventExcelVO {

    @ExcelProperty("记录ID")
    private Long id;

    @ExcelProperty("设备唯一标识")
    private String deviceId;

    @ExcelProperty("设备类型ID，关联iot_device_type表")
    private Long deviceTypeId;

    @ExcelProperty("记录时间（精确到分钟，按5分钟间隔存储）")
    private LocalDateTime recordTime;

    @ExcelProperty("监测指标ID，关联iot_monitor_metric表")
    private String metricId;

    @ExcelProperty("指标值")
    private BigDecimal metricValue;

    @ExcelProperty("数据状态（1：正常，2：异常，3：已处理）")
    private Byte dataStatus;

    @ExcelProperty("原始数据（保留原始上传数据）")
    private String rawData;

    @ExcelProperty("设备事件JSON数据")
    private String deviceEvent;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("负责人id")
    private Long userId;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;

}