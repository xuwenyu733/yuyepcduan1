package cn.iocoder.yudao.module.fish.controller.admin.zkgrownutritionalrequirements.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 各阶段营养需求新增/修改 Request VO")
@Data
public class ZkGrowNutritionalRequirementsSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "17571")
    private Long id;

    @Schema(description = "生长阶段")
    private String growthStage;

    @Schema(description = "日龄")
    private String dayold;

    @Schema(description = "体重范围")
    private String weightRange;

    @Schema(description = "粗蛋白")
    private String crudeProtein;

    @Schema(description = "粗脂肪")
    private String crudeFat;

    @Schema(description = "碳水化合物")
    private String carbohydrate;

    @Schema(description = "粗纤维")
    private String crudeFiber;

    @Schema(description = "钙")
    private String calcium;

    @Schema(description = "磷")
    private String phosphorus;

    @Schema(description = "所属部门", example = "23604")
    private Long deptId;
    private Long tenantId;

}