package cn.iocoder.yudao.module.fish.controller.admin.zkmanualinspection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 人工巡检 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkManualInspectionBaseVO {

    @Schema(description = "所属车间id", example = "11251")
    private Long workshopId;

    @Schema(description = "车间名称", example = "张三")
    private String workshopName;
    private Long pondId;
    private String pondName;
    private String pondNum;

    @Schema(description = "巡检名称", example = "张三")
    private String inspectionName;

    @Schema(description = "巡检描述")
    private String inspectionContent;

    @Schema(description = "巡检状态(-2:有事故待处理;-1:有事故已处理;0:一切正常;)", example = "2")
    private String status;

    @Schema(description = "负责人id", example = "11395")
    private Long userId;
    private String userName;

    @Schema(description = "所属部门", example = "21719")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "17083")
    @NotNull(message = "文件id不能为空")
    private String fid;

}
