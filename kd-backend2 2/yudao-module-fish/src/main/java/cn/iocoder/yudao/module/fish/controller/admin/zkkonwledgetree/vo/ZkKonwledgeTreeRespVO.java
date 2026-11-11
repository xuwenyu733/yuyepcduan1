package cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 知识树 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkKonwledgeTreeRespVO extends ZkKonwledgeTreeBaseVO {

    @Schema(description = "编号", required = true, example = "10295")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
