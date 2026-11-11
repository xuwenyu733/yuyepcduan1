package cn.iocoder.yudao.module.fish.controller.admin.zkdevicemetric.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 设备指标 Excel 导出 Request VO，参数和 ZkDeviceMetricPageReqVO 是一致的")
@Data
public class ZkDeviceMetricExportReqVO {

    @Schema(description = "指标编码（如：temperature、humidity、voltage）")
    private String metricCode;

    @Schema(description = "指标名称（如：温度、湿度、电压）", example = "张三")
    private String metricName;

    @Schema(description = "指标单位（如：℃、%、V）")
    private String unit;

    @Schema(description = "数据类型（如：int、float、double）", example = "2")
    private String dataType;

    @Schema(description = "指标描述", example = "你说的对")
    private String description;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "28249")
    private Long userId;

    @Schema(description = "所属部门", example = "10957")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "524")
    private String fid;

}
