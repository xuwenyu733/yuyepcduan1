package cn.iocoder.yudao.module.fish.controller.admin.zkthresholdrule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 阈值规则 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkThresholdRuleBaseVO {

    @Schema(description = "规则名称", required = true, example = "张三")
    @NotNull(message = "规则名称不能为空")
    private String ruleName;

    @Schema(description = "设备类型ID，关联iot_device_type表", required = true, example = "15415")
    @NotNull(message = "设备类型ID，关联iot_device_type表不能为空")
    private Long deviceTypeId;

    @Schema(description = "监测指标ID，关联iot_monitor_metric表", required = true, example = "9507")
    @NotNull(message = "监测指标ID，关联iot_monitor_metric表不能为空")
    private Long metricId;

    @Schema(description = "比较运算符（如：>、<、>=、<=、==、!=、between）", required = true)
    @NotNull(message = "比较运算符（如：>、<、>=、<=、==、!=、between）不能为空")
    private String comparisonOperator;

    @Schema(description = "阈值1（用于单值比较）")
    private BigDecimal thresholdValue1;

    @Schema(description = "阈值2（用于范围比较，如between）")
    private BigDecimal thresholdValue2;

    @Schema(description = "是否启用（1：启用，0：禁用）", required = true)
    @NotNull(message = "是否启用（1：启用，0：禁用）不能为空")
    private Byte isEnabled;

    @Schema(description = "触发级别（1：提示，2：警告，3：严重，4：紧急）", required = true)
    @NotNull(message = "触发级别（1：提示，2：警告，3：严重，4：紧急）不能为空")
    private Byte triggerLevel;

    @Schema(description = "触发动作（如：发送通知、启动设备、关闭设备）")
    private String triggerAction;

    @Schema(description = "规则描述", example = "随便")
    private String description;

    @Schema(description = "负责人id", example = "19641")
    private Long userId;

    @Schema(description = "所属部门", example = "12083")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "7223")
    private String fid;

}
