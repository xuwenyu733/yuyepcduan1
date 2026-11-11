package cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 精准投喂今日投喂记录 Response VO")
@Data
public class PrecisionFeedingTodayLogRespVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "鱼池ID")
    private Long pondId;

    @Schema(description = "投喂名称")
    private String feedName;

    @Schema(description = "投喂规格")
    private String feedSpec;

    @Schema(description = "投喂量")
    private BigDecimal feedCount;

    @Schema(description = "投喂时间")
    private LocalDateTime feedTime;

}
