package cn.iocoder.yudao.module.fish.controller.admin.zkgrowfishspecies.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 鱼类品种新增/修改 Request VO")
@Data
public class ZkGrowFishSpeciesSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "9858")
    private Long id;

    @Schema(description = "品种名称", example = "张三")
    private String name;

    @Schema(description = "俗名", example = "张三")
    private String commonName;

    @Schema(description = "学名", example = "李四")
    private String scientificName;

    @Schema(description = "品种描述", example = "你说的对")
    private String description;
    private Long deptId;
    private Long tenantId;

}