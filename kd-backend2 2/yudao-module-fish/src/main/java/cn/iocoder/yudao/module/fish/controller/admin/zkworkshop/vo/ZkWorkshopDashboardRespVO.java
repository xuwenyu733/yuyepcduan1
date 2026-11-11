package cn.iocoder.yudao.module.fish.controller.admin.zkworkshop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 车间 Dashboard Response VO")
@Data
public class ZkWorkshopDashboardRespVO {
    @Schema(description = "车间名称", example = "1号养殖车间")
    private String workshopName;
    @Schema(description = "车间id", example = "1")
    private Long workshopId;
    @Schema(description = "车间下面存塘数量", example = "8")
    private Integer pondCount;
}
