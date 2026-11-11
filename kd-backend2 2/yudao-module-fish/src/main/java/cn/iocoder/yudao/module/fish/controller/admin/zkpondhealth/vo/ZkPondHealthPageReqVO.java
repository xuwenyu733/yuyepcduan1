package cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 鱼池健康分页 Request VO")
@Data
public class ZkPondHealthPageReqVO extends PageParam {

    @Schema(description = "存塘id", example = "16889")
    private Long pondId;

    @Schema(description = "环境层评分")
    private BigDecimal environmentalLayerRating;

    @Schema(description = "行为层评分")
    private BigDecimal behavioralLayerRating;

    @Schema(description = "机体层评分")
    private BigDecimal bodyLayerRating;

    @Schema(description = "综合评分")
    private BigDecimal overallRating;

    @Schema(description = "环境层信息")
    private String environmentalLayerInformation;

    @Schema(description = "行为层信息")
    private String behavioralLayerInformation;

    @Schema(description = "机体层信息")
    private String bodyLayerInformation;

    @Schema(description = "异常信息")
    private String exceptionMessage;

    @Schema(description = "异常个数")
    private Integer numberOfExceptions;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "31107")
    private Long deptId;

}