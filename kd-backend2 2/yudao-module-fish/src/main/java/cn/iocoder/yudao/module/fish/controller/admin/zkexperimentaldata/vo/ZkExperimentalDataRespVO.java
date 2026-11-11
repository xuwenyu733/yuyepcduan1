package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaldata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 实验数据采集 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkExperimentalDataRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "29718")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "实验id", example = "21349")
    @ExcelProperty("实验id")
    private Long experimentId;

    @Schema(description = "实验编号")
    @ExcelProperty("实验编号")
    private String experimentNumber;

    @Schema(description = "采集类型", example = "1")
    @ExcelProperty("采集类型")
    private String collectionType;

    @Schema(description = "采集数据")
    @ExcelProperty("采集数据")
    private String collectData;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "21551")
    @ExcelProperty("所属部门")
    private Long deptId;

    @Schema(description = "鱼池ID", example = "1024")
    @ExcelProperty("鱼池ID")
    private Long poolId;

    @Schema(description = "池号")
    @ExcelProperty("池号")
    private String poolNumber;

}