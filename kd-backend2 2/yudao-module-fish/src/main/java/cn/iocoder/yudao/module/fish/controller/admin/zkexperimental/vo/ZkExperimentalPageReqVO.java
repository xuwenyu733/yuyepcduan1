package cn.iocoder.yudao.module.fish.controller.admin.zkexperimental.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 实验分页 Request VO")
@Data
public class ZkExperimentalPageReqVO extends PageParam {

    @Schema(description = "实验编号")
    private String experimentNumber;

    @Schema(description = "实验标题")
    private String experimentTitle;

    @Schema(description = "池号")
    private String pondDetailNum;

    @Schema(description = "品种")
    private String variety;

    @Schema(description = "负责人")
    private String personInCharge;

    @Schema(description = "负责人id", example = "13568")
    private Long personInChargeId;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] endTime;

    @Schema(description = "实验状态(0:待开始;1:计划中;2:进行中;3:已完成;4:已总结;)", example = "1")
    private Integer experimentalStatus;

    @Schema(description = "实验目标")
    private String experimentalObjective;

    @Schema(description = "实验内容")
    private String experimentalContent;

    @Schema(description = "实验方法")
    private String experimentalMethod;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "28906")
    private Long deptId;

}