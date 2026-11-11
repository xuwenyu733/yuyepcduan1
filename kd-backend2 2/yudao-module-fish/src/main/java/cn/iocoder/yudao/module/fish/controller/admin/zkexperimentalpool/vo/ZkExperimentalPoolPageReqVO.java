package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpool.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 实验鱼池分页 Request VO")
@Data
public class ZkExperimentalPoolPageReqVO extends PageParam {

    @Schema(description = "池号")
    private String poolNumber;

    @Schema(description = "大小(m³)")
    private BigDecimal size;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "负责人")
    private String manager;

    @Schema(description = "状态(available:空闲, in_use:使用中, maintenance:维护中)", example = "1")
    private String status;

    @Schema(description = "备注")
    private String notes;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "28692")
    private Long deptId;

}