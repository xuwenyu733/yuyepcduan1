package cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitingredients.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 饵料成分对比分页 Request VO")
@Data
public class ZkGrowBaitIngredientsPageReqVO extends PageParam {

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "2491")
    private Long deptId;
    private Long tenantId;

}