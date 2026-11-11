package cn.iocoder.yudao.module.fish.controller.admin.zkgrownutritionalrequirements.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 各阶段营养需求 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkGrowNutritionalRequirementsRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "17571")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "生长阶段")
    @ExcelProperty("生长阶段")
    private String growthStage;

    @Schema(description = "日龄")
    @ExcelProperty("日龄")
    private String dayold;

    @Schema(description = "体重范围")
    @ExcelProperty("体重范围")
    private String weightRange;

    @Schema(description = "粗蛋白")
    @ExcelProperty("粗蛋白")
    private String crudeProtein;

    @Schema(description = "粗脂肪")
    @ExcelProperty("粗脂肪")
    private String crudeFat;

    @Schema(description = "碳水化合物")
    @ExcelProperty("碳水化合物")
    private String carbohydrate;

    @Schema(description = "粗纤维")
    @ExcelProperty("粗纤维")
    private String crudeFiber;

    @Schema(description = "钙")
    @ExcelProperty("钙")
    private String calcium;

    @Schema(description = "磷")
    @ExcelProperty("磷")
    private String phosphorus;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "23604")
    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

}
