package cn.iocoder.yudao.module.fish.controller.admin.zkgrowmodelconfig.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 生长模型配置新增/修改 Request VO")
@Data
public class ZkGrowModelConfigSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "18850")
    private Long id;

    @Schema(description = "所属品种ID", example = "19922")
    private Long speciesId;

    @Schema(description = "模型名称", example = "赵六")
    private String modelName;

    @Schema(description = "极限体长 (cm)")
    @JsonProperty("lInf")
    private BigDecimal lInf;

    @Schema(description = "极限体重 (g)")
    @JsonProperty("wInf")
    private BigDecimal wInf;

    @Schema(description = "体长生长系数")
    @JsonProperty("kLength")
    private BigDecimal kLength;

    @Schema(description = "体重生长系数")
    @JsonProperty("kWeight")
    private BigDecimal kWeight;

    @Schema(description = "初始体重 (g)")
    private BigDecimal initialWeight;

    @Schema(description = "初始体长 (cm)")
    private BigDecimal initialLength;

    @Schema(description = "最适水温最小值")
    private BigDecimal optimalTempMin;

    @Schema(description = "最适水温最大值")
    private BigDecimal optimalTempMax;

    @Schema(description = "最适投饵率 (%)")
    private BigDecimal optimalFeedRate;

    private Long deptId;
    private Long tenantId;
}