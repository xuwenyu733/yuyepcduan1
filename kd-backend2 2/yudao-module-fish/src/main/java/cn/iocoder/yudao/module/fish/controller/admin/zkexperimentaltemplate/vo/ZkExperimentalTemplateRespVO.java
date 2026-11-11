package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaltemplate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 实验模板 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkExperimentalTemplateRespVO {

    @Schema(description = "编号")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "模板名称")
    @ExcelProperty("模板名称")
    private String templateName;

    @Schema(description = "模板类型")
    @ExcelProperty("模板类型")
    private String templateType;

    @Schema(description = "模板编码")
    @ExcelProperty("模板编码")
    private String templateCode;

    @Schema(description = "品种")
    @ExcelProperty("品种")
    private String variety;

    @Schema(description = "实验目标")
    @ExcelProperty("实验目标")
    private String experimentalObjective;

    @Schema(description = "实验内容")
    @ExcelProperty("实验内容")
    private String experimentalContent;

    @Schema(description = "实验方法")
    @ExcelProperty("实验方法")
    private String experimentalMethod;

    @Schema(description = "描述")
    @ExcelProperty("描述")
    private String description;

    @Schema(description = "状态 0启用 1禁用")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "所属部门")
    private Long deptId;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
