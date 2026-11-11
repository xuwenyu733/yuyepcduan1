package cn.iocoder.yudao.module.fish.controller.admin.zkillcontrol.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 疾病诊断与防治记录 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkIllControlBaseVO {

    @Schema(description = "病害简称", required = true)
    @NotNull(message = "病害简称不能为空")
    private String illTitle;

    @Schema(description = "病害描述")
    private String illContent;

    @Schema(description = "诊断标题")
    private String controlTitle;

    @Schema(description = "诊断描述")
    private String controlContent;

    @Schema(description = "状态(0:正常;-1:已删除;)", example = "1")
    private String status;

    @Schema(description = "附件")
    private String fids;

    @Schema(description = "流程实例", example = "12194")
    private String processInstanceId;

    @Schema(description = "记录人", example = "12600")
    private Long userId;

    @Schema(description = "所属部门", example = "13883")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "26450")
    @NotNull(message = "文件id不能为空")
    private String fid;
    private String agentId;

}
