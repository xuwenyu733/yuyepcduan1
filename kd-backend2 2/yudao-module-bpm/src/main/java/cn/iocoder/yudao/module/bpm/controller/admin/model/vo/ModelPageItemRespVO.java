package cn.iocoder.yudao.module.bpm.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 流程模型的分页的每一项 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ModelPageItemRespVO extends ModelBaseVO {

    @Schema(description = "编号", required = true, example = "1024")
    private String id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    /**
     * 最新部署的流程定义
     */
    private ProcessDefinition processDefinition;

    @Schema(description = "流程定义")
    @Data
    public static class ProcessDefinition {

        @Schema(description = "编号", required = true, example = "1024")
        private String id;

        @Schema(description = "版本", required = true, example = "1")
        private Integer version;

        @Schema(description = "部署时间", required = true)
        private LocalDateTime deploymentTime;

        @Schema(description = "中断状态-参见 SuspensionState 枚举", required = true, example = "1")
        private Integer suspensionState;

    }

}
