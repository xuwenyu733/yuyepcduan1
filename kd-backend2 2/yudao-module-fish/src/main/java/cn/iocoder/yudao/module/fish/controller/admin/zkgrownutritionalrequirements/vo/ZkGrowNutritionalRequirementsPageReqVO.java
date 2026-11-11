package cn.iocoder.yudao.module.fish.controller.admin.zkgrownutritionalrequirements.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 各阶段营养需求分页 Request VO")
@Data
public class ZkGrowNutritionalRequirementsPageReqVO extends PageParam {

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "23604")
    private Long deptId;
    private Long tenantId;

}