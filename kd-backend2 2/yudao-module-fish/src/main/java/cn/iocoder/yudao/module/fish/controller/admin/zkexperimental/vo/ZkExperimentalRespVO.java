package cn.iocoder.yudao.module.fish.controller.admin.zkexperimental.vo;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimental.ZkExperimentalDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaldata.ZkExperimentalDataDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalmedia.ZkExperimentalMediaDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpoolmapping.ZkExperimentalPoolMappingDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 实验 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkExperimentalRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "19393")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "实验编号")
    @ExcelProperty("实验编号")
    private String experimentNumber;

    @Schema(description = "实验标题")
    @ExcelProperty("实验标题")
    private String experimentTitle;

    @Schema(description = "池号")
    @ExcelProperty("池号")
    private String pondDetailNum;

    @Schema(description = "品种")
    @ExcelProperty("品种")
    private String variety;

    @Schema(description = "负责人")
    @ExcelProperty("负责人")
    private String personInCharge;

    @Schema(description = "负责人id", example = "13568")
    @ExcelProperty("负责人id")
    private Long personInChargeId;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("结束时间")
    private LocalDateTime endTime;

    @Schema(description = "实验状态(0:待开始;1:计划中;2:进行中;3:已完成;4:已总结;)", example = "1")
    @ExcelProperty("实验状态(0:待开始;1:计划中;2:进行中;3:已完成;4:已总结;)")
    private Integer experimentalStatus;

    @Schema(description = "实验目标")
    @ExcelProperty("实验目标")
    private String experimentalObjective;

    @Schema(description = "实验内容")
    @ExcelProperty("实验内容")
    private String experimentalContent;

    @Schema(description = "实验方法")
    @ExcelProperty("实验方法")
    private String experimentalMethod;

    @Schema(description = "主要结果")
    @ExcelProperty("主要结果")
    private String mainResults;

    @Schema(description = "效果评估")
    @ExcelProperty("效果评估")
    private String effectEvaluation;

    @Schema(description = "完成人")
    @ExcelProperty("完成人")
    private String completedBy;

    @Schema(description = "完成时间")
    @ExcelProperty("完成时间")
    private LocalDateTime completionTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "28906")
    @ExcelProperty("所属部门")
    private Long deptId;
    private String creator;

    @Schema(description = "关联池子ID列表")
    private List<Long> poolIds;

    @Schema(description = "关联池子详情列表")
    private List<ZkExperimentalPoolMappingDO> poolDetails;

    @Schema(description = "关联池号列表")
    private List<String> poolNumbers;

    // 数据采集map
    private Map<String, ZkExperimentalDataDO> dataMap;
    // 多媒体列表
    private List<ZkExperimentalMediaDO> fileList;

}