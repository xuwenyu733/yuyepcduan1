package cn.iocoder.yudao.module.fish.controller.admin.zkpondsystem.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 存塘系统 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkPondSystemBaseVO {

    @Schema(description = "系统编号")
    private String systemNum;

    @Schema(description = "系统名称", example = "赵六")
    private String systemName;

    @Schema(description = "所在车间", example = "芋艿")
    private String workshopName;

    @Schema(description = "所在车间id", required = true, example = "8085")
    @NotNull(message = "所在车间id不能为空")
    private Long workshopId;

    @Schema(description = "负责人", example = "李四")
    private String userName;

    @Schema(description = "负责人id", example = "28230")
    private Long userId;

    @Schema(description = "所属部门", example = "22698")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "26295")
    @NotNull(message = "文件id不能为空")
    private String fid;
    private String tenantName; // 租户名称
    private String deptName; // 部门名称

}
