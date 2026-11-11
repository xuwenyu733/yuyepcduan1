package cn.iocoder.yudao.module.fish.controller.admin.zkbreedspecies.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 养殖品种 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkBreedSpeciesRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15607")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "品种名称 (如:大西洋鲑鱼)", example = "李四")
    @ExcelProperty("品种名称 (如:大西洋鲑鱼)")
    private String speciesName;

    @Schema(description = "质量等级标签 (如:生食级、优质级)")
    @ExcelProperty("质量等级标签 (如:生食级、优质级)")
    private String qualityGrade;

    @Schema(description = "学名 (如:Salmo salar)", example = "王五")
    @ExcelProperty("学名 (如:Salmo salar)")
    private String scientificName;

    @Schema(description = "适宜水温 (如:10-16°C)")
    @ExcelProperty("适宜水温 (如:10-16°C)")
    private String suitableTemp;

    @Schema(description = "养殖周期 (如:18-24月)")
    @ExcelProperty("养殖周期 (如:18-24月)")
    private String breedingCycle;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "23187")
    @ExcelProperty("所属部门")
    private Long deptId;

}