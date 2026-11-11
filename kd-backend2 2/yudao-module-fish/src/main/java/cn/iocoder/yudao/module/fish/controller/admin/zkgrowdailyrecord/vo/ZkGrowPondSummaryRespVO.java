package cn.iocoder.yudao.module.fish.controller.admin.zkgrowdailyrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 养殖鱼池简要信息 Response VO")
@Data
public class ZkGrowPondSummaryRespVO {

    @Schema(description = "鱼苗种类", example = "1")
    private String fishType;

    @Schema(description = "鱼苗种类名称", example = "大西洋鲑")
    private String label;

    @Schema(description = "存塘id", example = "1")
    private Long pondId;

    @Schema(description = "系统编号", example = "SYS001")
    private String pondNum;

    @Schema(description = "系统名称", example = "系统A")
    private String pondSystemName;

    @Schema(description = "存塘编号", example = "POND001")
    private String pondDetailNum;

    @Schema(description = "存塘名称", example = "鱼池1")
    private String pondName;

    @Schema(description = "车间名称", example = "鲈鱼车间")
    private String workshopName;

    @Schema(description = "车间ID", example = "1")
    private Long workshopId;

    @Schema(description = "规格/均重", example = "5")
    private String fishSpec;

    @Schema(description = "批次号", example = "CGRK2026041404")
    private String relationOrderNo;

    @Schema(description = "存塘鱼数", example = "500")
    private Integer fishNum;

}
