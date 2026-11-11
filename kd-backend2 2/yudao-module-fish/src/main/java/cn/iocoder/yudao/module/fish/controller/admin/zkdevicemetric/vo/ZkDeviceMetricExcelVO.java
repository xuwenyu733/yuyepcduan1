package cn.iocoder.yudao.module.fish.controller.admin.zkdevicemetric.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 设备指标 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkDeviceMetricExcelVO {

    @ExcelProperty("指标ID")
    private Long id;

    @ExcelProperty("指标编码（如：temperature、humidity、voltage）")
    private String metricCode;

    @ExcelProperty("指标名称（如：温度、湿度、电压）")
    private String metricName;

    @ExcelProperty("指标单位（如：℃、%、V）")
    private String unit;

    @ExcelProperty("数据类型（如：int、float、double）")
    private String dataType;

    @ExcelProperty("指标描述")
    private String description;

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
