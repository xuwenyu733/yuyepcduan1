package cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitnutrition.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 饵料营养成分 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkGrowBaitNutritionRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13916")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "饵料名称", example = "李四")
    @ExcelProperty("饵料名称")
    private String baitName;

    @Schema(description = "颗粒大小(mm)")
    @ExcelProperty("颗粒大小(mm)")
    private String pelletSize;

    @Schema(description = "粗蛋白(%)")
    @ExcelProperty("粗蛋白(%)")
    private String crudeProtein;

    @Schema(description = "粗脂肪(%)")
    @ExcelProperty("粗脂肪(%)")
    private String crudeFat;

    @Schema(description = "无氮浸出物(%)")
    @ExcelProperty("无氮浸出物(%)")
    private String nitrogenFreeExtract;

    @Schema(description = "灰分(%)")
    @ExcelProperty("灰分(%)")
    private String ashContent;

    @Schema(description = "粗纤维(%)")
    @ExcelProperty("粗纤维(%)")
    private String crudeFiber;

    @Schema(description = "磷(%)")
    @ExcelProperty("磷(%)")
    private String phosphorus;

    @Schema(description = "总能(MJ)")
    @ExcelProperty("总能(MJ)")
    private String grossEnergy;

    @Schema(description = "消化能(MJ)")
    @ExcelProperty("消化能(MJ)")
    private String digestibleEnergy;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "4180")
    @ExcelProperty("所属部门")
    private Long deptId;

    private String manufacturer; // 厂家名称
    private String moisture; // 水分(%)
    private String calcium; // 钙(%)
    private String lysine; // 赖氨酸(%)
    private String methionine; // 蛋氨酸(%)
}