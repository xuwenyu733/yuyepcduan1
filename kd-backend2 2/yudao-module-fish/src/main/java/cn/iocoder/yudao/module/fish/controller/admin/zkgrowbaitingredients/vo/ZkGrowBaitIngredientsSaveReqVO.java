package cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitingredients.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 饵料成分对比新增/修改 Request VO")
@Data
public class ZkGrowBaitIngredientsSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "4072")
    private Long id;

    @Schema(description = "饵料名称", example = "芋艿")
    private String baitName;

    @Schema(description = "饵料类型", example = "1")
    private String feedType;

    @Schema(description = "粗蛋白")
    private String crudeProtein;

    @Schema(description = "粗脂肪")
    private String crudeFat;

    @Schema(description = "碳水化合物")
    private String carbohydrate;

    @Schema(description = "粗纤维")
    private String crudeFiber;

    @Schema(description = "水分")
    private String moisture;

    @Schema(description = "灰分")
    private String ashContent;

    @Schema(description = "参考价格", example = "15063")
    private String referencePrice;

    @Schema(description = "所属部门", example = "2491")
    private Long deptId;
    private Long tenantId;

}