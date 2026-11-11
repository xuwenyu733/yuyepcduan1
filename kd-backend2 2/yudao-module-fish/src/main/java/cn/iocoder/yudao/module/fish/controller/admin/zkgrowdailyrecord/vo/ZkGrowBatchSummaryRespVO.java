package cn.iocoder.yudao.module.fish.controller.admin.zkgrowdailyrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 养殖批次简要信息 Response VO")
@Data
public class ZkGrowBatchSummaryRespVO {

    @Schema(description = "批次编号", example = "BATCH2024001")
    private String relationOrderNo;

    @Schema(description = "鱼苗种类编号", example = "1")
    private String fishTypes;

    @Schema(description = "鱼苗种类名称", example = "大西洋鲑")
    private String label;

    @Schema(description = "鱼苗规格", example = "50g")
    private String standard;

    @Schema(description = "鱼苗数量", example = "1000")
    private BigDecimal productCount;

}
