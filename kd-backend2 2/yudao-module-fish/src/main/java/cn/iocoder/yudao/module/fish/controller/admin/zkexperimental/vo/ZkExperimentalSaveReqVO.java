package cn.iocoder.yudao.module.fish.controller.admin.zkexperimental.vo;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpoolmapping.ZkExperimentalPoolMappingDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 实验新增/修改 Request VO")
@Data
public class ZkExperimentalSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "19393")
    private Long id;

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

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @Schema(description = "实验状态(0:待开始;1:计划中;2:进行中;3:已完成;4:已总结;)", example = "1")
    private Integer experimentalStatus;

    @Schema(description = "实验目标")
    private String experimentalObjective;

    @Schema(description = "实验内容")
    private String experimentalContent;

    @Schema(description = "实验方法")
    private String experimentalMethod;

    @Schema(description = "主要结果")
    private String mainResults;

    @Schema(description = "效果评估")
    private String effectEvaluation;

    @Schema(description = "完成人")
    private String completedBy;

    @Schema(description = "完成时间")
    private LocalDateTime completionTime;

    @Schema(description = "所属部门", example = "28906")
    private Long deptId;

    @Schema(description = "关联池子ID列表")
    private List<Long> poolIds;

    @Schema(description = "关联池子详情列表")
    private List<ZkExperimentalPoolMappingDO> poolDetails;

}