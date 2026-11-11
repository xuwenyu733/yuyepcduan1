package cn.iocoder.yudao.module.fish.controller.admin.zkworkshop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 车间 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkWorkshopBaseVO {

    @Schema(description = "车间名称", example = "张三")
    private String workshopName;

    @Schema(description = "具体位置")
    private String workshopPosition;

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "维度")
    private String dimension;

    @Schema(description = "负责人", example = "赵六")
    private String userName;

    @Schema(description = "状态(0:待规划;1:规划中;2:养殖中;-1:已废弃;)", example = "2")
    private String status;

    @Schema(description = "负责人id", example = "16495")
    private Long userId;

    @Schema(description = "所属部门", example = "10584")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "16178")
    @NotNull(message = "文件id不能为空")
    private String fid;
    private String tenantName; // 租户名称
    private String deptName; // 部门名称

}
