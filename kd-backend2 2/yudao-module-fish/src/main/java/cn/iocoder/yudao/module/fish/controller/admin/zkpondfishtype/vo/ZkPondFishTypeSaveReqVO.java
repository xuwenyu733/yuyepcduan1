package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishtype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 鱼苗种类新增/修改 Request VO")
@Data
public class ZkPondFishTypeSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "14325")
    private Long id;

    @Schema(description = "存塘id", example = "11782")
    private Long pondId;

    @Schema(description = "批次编号")
    private String batchNumber;

    @Schema(description = "鱼苗数量")
    private Long fishNum;

    @Schema(description = "鱼苗规格")
    private String fishSpec;

    @Schema(description = "统计年")
    private Integer staticYear;

    @Schema(description = "统计月份")
    private Integer staticMonth;

    @Schema(description = "统计日期")
    private Integer staticDay;

    @Schema(description = "鱼苗种类", example = "1")
    private String fishType;

    @Schema(description = "所属部门", example = "24053")
    private Long deptId;

}