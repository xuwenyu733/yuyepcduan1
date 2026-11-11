package cn.iocoder.yudao.module.fish.controller.admin.zkgrowfishspecies.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 鱼类品种分页 Request VO")
@Data
public class ZkGrowFishSpeciesPageReqVO extends PageParam {

    @Schema(description = "品种名称", example = "张三")
    private String name;

    @Schema(description = "俗名", example = "张三")
    private String commonName;

    @Schema(description = "学名", example = "李四")
    private String scientificName;

    @Schema(description = "品种描述", example = "你说的对")
    private String description;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
    private Long deptId;
    private Long tenantId;

}