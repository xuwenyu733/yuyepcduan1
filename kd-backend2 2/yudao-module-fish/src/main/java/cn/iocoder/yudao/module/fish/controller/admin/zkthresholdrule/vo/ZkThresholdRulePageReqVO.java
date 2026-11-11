package cn.iocoder.yudao.module.fish.controller.admin.zkthresholdrule.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 阈值规则分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkThresholdRulePageReqVO extends PageParam {

    @Schema(description = "规则名称", example = "张三")
    private String ruleName;

    @Schema(description = "设备类型ID，关联iot_device_type表", example = "15415")
    private Long deviceTypeId;

    @Schema(description = "监测指标ID，关联iot_monitor_metric表", example = "9507")
    private Long metricId;

    @Schema(description = "比较运算符（如：>、<、>=、<=、==、!=、between）")
    private String comparisonOperator;

    @Schema(description = "阈值1（用于单值比较）")
    private BigDecimal thresholdValue1;

    @Schema(description = "阈值2（用于范围比较，如between）")
    private BigDecimal thresholdValue2;

    @Schema(description = "是否启用（1：启用，0：禁用）")
    private Byte isEnabled;

    @Schema(description = "触发级别（1：提示，2：警告，3：严重，4：紧急）")
    private Byte triggerLevel;

    @Schema(description = "触发动作（如：发送通知、启动设备、关闭设备）")
    private String triggerAction;

    @Schema(description = "规则描述", example = "随便")
    private String description;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "19641")
    private Long userId;

    @Schema(description = "所属部门", example = "12083")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "7223")
    private String fid;

}
