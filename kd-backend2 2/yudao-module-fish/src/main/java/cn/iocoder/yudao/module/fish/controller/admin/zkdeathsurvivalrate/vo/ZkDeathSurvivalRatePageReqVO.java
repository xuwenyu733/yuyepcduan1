package cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 死亡和存活率分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDeathSurvivalRatePageReqVO extends PageParam {

    @Schema(description = "所属车间id", example = "28179")
    private Long workshopId;

    @Schema(description = "车间名称", example = "芋艿")
    private String workshopName;

    @Schema(description = "所属系统id", example = "20775")
    private Long pondSystemId;

    @Schema(description = "存塘系统编号")
    private String pondNum;

    @Schema(description = "存塘系统名称", example = "张三")
    private String pondSystemName;

    @Schema(description = "所属存塘id", example = "19253")
    private Long pondId;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "存塘名称", example = "张三")
    private String pondName;

    @Schema(description = "批次编号")
    private String batchNumber;

    @Schema(description = "鱼苗数量")
    private Integer fryQuantity;

    @Schema(description = "死亡数量")
    private Integer deathQuantity;

    @Schema(description = "销售数量")
    private Integer salesQuantity;

    @Schema(description = "分塘数量")
    private Integer pondTransferQuantity;

    @Schema(description = "存塘数量")
    private Integer stockedQuantity;

    @Schema(description = "存活率")
    private Object survivalRate;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "13751")
    private Long userId;

    @Schema(description = "所属部门", example = "27086")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "26133")
    private String fid;

}
