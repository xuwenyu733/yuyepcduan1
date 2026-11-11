package cn.iocoder.yudao.module.fish.controller.admin.zkbreedspecies.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 养殖品种新增/修改 Request VO")
@Data
public class ZkBreedSpeciesSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15607")
    private Long id;

    @Schema(description = "品种名称 (如:大西洋鲑鱼)", example = "李四")
    private String speciesName;

    @Schema(description = "质量等级标签 (如:生食级、优质级)")
    private String qualityGrade;

    @Schema(description = "学名 (如:Salmo salar)", example = "王五")
    private String scientificName;

    @Schema(description = "适宜水温 (如:10-16°C)")
    private String suitableTemp;

    @Schema(description = "养殖周期 (如:18-24月)")
    private String breedingCycle;

    @Schema(description = "所属部门", example = "23187")
    private Long deptId;

}