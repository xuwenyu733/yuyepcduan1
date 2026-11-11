package cn.iocoder.yudao.module.fish.controller.admin.zkdocument.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "管理后台 - 文档统计 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkDocumentStatisticsVO {

    @Schema(description = "文档总数", example = "6")
    private Long total;

    @Schema(description = "共享文档数", example = "4")
    private Long shared;

    @Schema(description = "我的文档数", example = "2")
    private Long myDocuments;

    @Schema(description = "本月新增文档数", example = "4")
    private Long thisMonth;
}



