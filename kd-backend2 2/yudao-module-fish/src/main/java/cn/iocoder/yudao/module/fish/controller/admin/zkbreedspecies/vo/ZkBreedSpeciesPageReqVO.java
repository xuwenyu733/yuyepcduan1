package cn.iocoder.yudao.module.fish.controller.admin.zkbreedspecies.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 养殖品种分页 Request VO")
@Data
public class ZkBreedSpeciesPageReqVO extends PageParam {

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "23187")
    private Long deptId;

}