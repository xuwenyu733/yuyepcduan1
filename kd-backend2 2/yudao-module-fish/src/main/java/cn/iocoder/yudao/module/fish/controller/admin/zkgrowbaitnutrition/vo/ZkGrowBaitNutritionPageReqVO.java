package cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitnutrition.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 饵料营养成分分页 Request VO")
@Data
public class ZkGrowBaitNutritionPageReqVO extends PageParam {

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "4180")
    private Long deptId;

    private String manufacturer; // 厂家名称
    private String moisture; // 水分(%)
    private String calcium; // 钙(%)
    private String lysine; // 赖氨酸(%)
    private String methionine; // 蛋氨酸(%)
}