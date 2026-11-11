package cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 精准投喂不同规格饲料统计 VO")
@Data
public class PrecisionFeedingSpecStatVO {
    @Schema(description = "鱼池ID")
    private Long pondId;

    @Schema(description = "饲料规格")
    private String feedSpec;

    @Schema(description = "投喂量")
    private BigDecimal feedCount;
}
