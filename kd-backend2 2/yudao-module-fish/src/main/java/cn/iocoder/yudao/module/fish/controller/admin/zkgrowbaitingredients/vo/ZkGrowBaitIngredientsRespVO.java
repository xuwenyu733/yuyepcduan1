package cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitingredients.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 饵料成分对比 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkGrowBaitIngredientsRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "4072")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "饵料名称", example = "芋艿")
    @ExcelProperty("饵料名称")
    private String baitName;

    @Schema(description = "饵料类型", example = "1")
    @ExcelProperty("饵料类型")
    private String feedType;

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

    @Schema(description = "水分")
    @ExcelProperty("水分")
    private String moisture;

    @Schema(description = "灰分")
    @ExcelProperty("灰分")
    private String ashContent;

    @Schema(description = "参考价格", example = "15063")
    @ExcelProperty("参考价格")
    private String referencePrice;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "2491")
    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

}
