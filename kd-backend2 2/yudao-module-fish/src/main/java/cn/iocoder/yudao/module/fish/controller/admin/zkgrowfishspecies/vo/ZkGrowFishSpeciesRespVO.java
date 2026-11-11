package cn.iocoder.yudao.module.fish.controller.admin.zkgrowfishspecies.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 鱼类品种 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkGrowFishSpeciesRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "9858")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "品种名称", example = "张三")
    @ExcelProperty("品种名称")
    private String name;

    @Schema(description = "俗名", example = "张三")
    @ExcelProperty("俗名")
    private String commonName;

    @Schema(description = "学名", example = "李四")
    @ExcelProperty("学名")
    private String scientificName;

    @Schema(description = "品种描述", example = "你说的对")
    @ExcelProperty("品种描述")
    private String description;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    private Long deptId;
    private Long tenantId;

}
