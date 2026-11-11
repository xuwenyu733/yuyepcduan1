package cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 鱼池健康新增/修改 Request VO")
@Data
public class ZkPondHealthSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "17856")
    private Long id;

    @Schema(description = "存塘id", example = "16889")
    private Long pondId;

    @Schema(description = "环境层评分")
    private BigDecimal environmentalLayerRating;

    @Schema(description = "行为层评分")
    private BigDecimal behavioralLayerRating;

    @Schema(description = "机体层评分")
    private BigDecimal bodyLayerRating;

    @Schema(description = "综合评分")
    private BigDecimal overallRating;

    @Schema(description = "环境层信息")
    private String environmentalLayerInformation;

    @Schema(description = "行为层信息")
    private String behavioralLayerInformation;

    @Schema(description = "机体层信息")
    private String bodyLayerInformation;

    @Schema(description = "异常信息")
    private String exceptionMessage;

    @Schema(description = "异常个数")
    private Integer numberOfExceptions;

    @Schema(description = "所属部门", example = "31107")
    private Long deptId;

}