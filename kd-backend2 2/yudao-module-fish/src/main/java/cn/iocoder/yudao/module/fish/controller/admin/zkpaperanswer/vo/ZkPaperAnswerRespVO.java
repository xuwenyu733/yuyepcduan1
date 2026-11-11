package cn.iocoder.yudao.module.fish.controller.admin.zkpaperanswer.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 答题记录 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkPaperAnswerRespVO extends ZkPaperAnswerBaseVO {

    @Schema(description = "编号", required = true, example = "16491")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
