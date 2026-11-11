package cn.iocoder.yudao.module.fish.controller.admin.zkgrowmodelconfig.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 生长模型配置分页 Request VO")
@Data
public class ZkGrowModelConfigPageReqVO extends PageParam {

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    private Long deptId;
    private Long tenantId;
}