package cn.iocoder.yudao.module.fish.controller.admin.oaworkplan.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - OA工作计划分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OaWorkPlanPageReqVO extends PageParam {

    @Schema(description = "计划类型：weekly-周计划, monthly-月计划, quarterly-季度计划, annual-年度计划")
    private String type;

    @Schema(description = "计划标题（模糊查询）")
    private String title;

    @Schema(description = "状态：pending-待审核, approved-已批准, completed-已完成, draft-草稿, rejected-已拒绝")
    private String status;

    @Schema(description = "创建者ID", example = "6144")
    private Long userId;

    @Schema(description = "所属部门ID", example = "13840")
    private Long deptId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "提交时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] submitDate;

}



