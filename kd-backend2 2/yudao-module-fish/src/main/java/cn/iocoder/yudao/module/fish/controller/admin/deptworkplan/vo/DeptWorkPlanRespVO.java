package cn.iocoder.yudao.module.fish.controller.admin.deptworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 部门工作计划响应 VO
 */
@Schema(description = "管理后台 - 部门工作计划响应 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DeptWorkPlanRespVO extends DeptWorkPlanBaseVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}



