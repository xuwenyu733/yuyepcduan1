package cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitnutrition.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 饵料营养成分新增/修改 Request VO")
@Data
public class ZkGrowBaitNutritionSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13916")
    private Long id;

    @Schema(description = "饵料名称", example = "李四")
    private String baitName;

    @Schema(description = "颗粒大小(mm)")
    private String pelletSize;

    @Schema(description = "粗蛋白(%)")
    private String crudeProtein;

    @Schema(description = "粗脂肪(%)")
    private String crudeFat;

    @Schema(description = "无氮浸出物(%)")
    private String nitrogenFreeExtract;

    @Schema(description = "灰分(%)")
    private String ashContent;

    @Schema(description = "粗纤维(%)")
    private String crudeFiber;

    @Schema(description = "磷(%)")
    private String phosphorus;

    @Schema(description = "总能(MJ)")
    private String grossEnergy;

    @Schema(description = "消化能(MJ)")
    private String digestibleEnergy;

    @Schema(description = "所属部门", example = "4180")
    private Long deptId;

    private String manufacturer; // 厂家名称
    private String moisture; // 水分(%)
    private String calcium; // 钙(%)
    private String lysine; // 赖氨酸(%)
    private String methionine; // 蛋氨酸(%)
}