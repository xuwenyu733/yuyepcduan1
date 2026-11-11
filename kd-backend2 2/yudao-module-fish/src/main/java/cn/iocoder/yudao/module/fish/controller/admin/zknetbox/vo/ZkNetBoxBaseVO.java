package cn.iocoder.yudao.module.fish.controller.admin.zknetbox.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 网盘资源 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkNetBoxBaseVO {

    @Schema(description = "资源类型(1:入职培训;2:运营手册;3:规章制度;4:工作任务及流程;)", required = true, example = "1")
    @NotNull(message = "资源类型(1:入职培训;2:运营手册;3:规章制度;4:工作任务及流程;)不能为空")
    private String fileType;

    @Schema(description = "资源类型名称", required = true, example = "芋艿")
    @NotNull(message = "资源类型名称不能为空")
    private String fileTypeName;

    @Schema(description = "资源前缀", required = true)
    @NotNull(message = "资源前缀不能为空")
    private String prefixPath;

    @Schema(description = "备份", required = true, example = "你说的对")
    @NotNull(message = "备份不能为空")
    private String remark;

    @Schema(description = "记录人", example = "14049")
    private Long userId;

    @Schema(description = "所属部门", example = "5910")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "1790")
    @NotNull(message = "文件id不能为空")
    private String fid;

}
