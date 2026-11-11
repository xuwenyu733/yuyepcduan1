package cn.iocoder.yudao.module.fish.controller.admin.zkdevicemetric.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 设备指标 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkDeviceMetricBaseVO {

    @Schema(description = "指标编码（如：temperature、humidity、voltage）", required = true)
    @NotNull(message = "指标编码（如：temperature、humidity、voltage）不能为空")
    private String metricCode;

    @Schema(description = "指标名称（如：温度、湿度、电压）", required = true, example = "张三")
    @NotNull(message = "指标名称（如：温度、湿度、电压）不能为空")
    private String metricName;

    @Schema(description = "指标单位（如：℃、%、V）")
    private String unit;

    @Schema(description = "数据类型（如：int、float、double）", required = true, example = "2")
    @NotNull(message = "数据类型（如：int、float、double）不能为空")
    private String dataType;

    @Schema(description = "指标描述", example = "你说的对")
    private String description;

    @Schema(description = "负责人id", example = "28249")
    private Long userId;

    @Schema(description = "所属部门", example = "10957")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "524")
    private String fid;

}
