package cn.iocoder.yudao.module.fish.controller.admin.zktrainingknowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 培训知识 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TrainingKnowledgeRespVO extends TrainingKnowledgeBaseVO {

    @Schema(description = "编号", required = true, example = "19728")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
