package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaldata.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 实验数据采集分页 Request VO")
@Data
public class ZkExperimentalDataPageReqVO extends PageParam {

    @Schema(description = "实验id", example = "21349")
    private Long experimentId;

    @Schema(description = "实验id（前端别名 experimentalId）", example = "21349")
    private Long experimentalId;

    /** 兼容前端 experimentalId 与后端 experimentId */
    public Long resolveExperimentId() {
        return experimentId != null ? experimentId : experimentalId;
    }

    @Schema(description = "实验编号")
    private String experimentNumber;

    @Schema(description = "采集类型", example = "1")
    private String collectionType;

    @Schema(description = "采集数据")
    private String collectData;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "21551")
    private Long deptId;

}