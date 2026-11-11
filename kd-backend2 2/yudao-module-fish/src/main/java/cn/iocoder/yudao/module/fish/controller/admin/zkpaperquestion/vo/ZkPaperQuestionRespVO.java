package cn.iocoder.yudao.module.fish.controller.admin.zkpaperquestion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "管理后台 - 试题 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkPaperQuestionRespVO extends ZkPaperQuestionBaseVO {

    @Schema(description = "编号", required = true, example = "18024")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;

    private List<String> myAnswer2 = new ArrayList<>();
}
