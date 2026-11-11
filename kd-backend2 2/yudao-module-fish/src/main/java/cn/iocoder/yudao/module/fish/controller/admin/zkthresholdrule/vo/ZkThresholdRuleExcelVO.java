package cn.iocoder.yudao.module.fish.controller.admin.zkthresholdrule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 阈值规则 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkThresholdRuleExcelVO {

    @ExcelProperty("规则ID")
    private Long id;

    @ExcelProperty("规则名称")
    private String ruleName;

    @ExcelProperty("设备类型ID，关联iot_device_type表")
    private Long deviceTypeId;

    @ExcelProperty("监测指标ID，关联iot_monitor_metric表")
    private Long metricId;

    @ExcelProperty("比较运算符（如：>、<、>=、<=、==、!=、between）")
    private String comparisonOperator;

    @ExcelProperty("阈值1（用于单值比较）")
    private BigDecimal thresholdValue1;

    @ExcelProperty("阈值2（用于范围比较，如between）")
    private BigDecimal thresholdValue2;

    @ExcelProperty("是否启用（1：启用，0：禁用）")
    private Byte isEnabled;

    @ExcelProperty("触发级别（1：提示，2：警告，3：严重，4：紧急）")
    private Byte triggerLevel;

    @ExcelProperty("触发动作（如：发送通知、启动设备、关闭设备）")
    private String triggerAction;

    @ExcelProperty("规则描述")
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
