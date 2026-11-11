package cn.iocoder.yudao.module.fish.controller.admin.zkpondgainlog.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 鱼生长分页 Request VO")
@Data
public class ZkPondGainLogPageReqVO extends PageParam {

    @Schema(description = "车间id", example = "16632")
    private Long workshopId;

    @Schema(description = "系统id", example = "854")
    private Long systemId;

    @Schema(description = "存塘id", example = "6795")
    private Long pondId;

    @Schema(description = "批次编号")
    private String batchNumber;

    @Schema(description = "统计日期")
    private String staticDay;

    @Schema(description = "鱼苗数量")
    private Long fishNum;

    @Schema(description = "鱼苗规格")
    private String fishSpec;

    @Schema(description = "今日鱼增加重量", example = "10407")
    private BigDecimal fishGainCount;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "32733")
    private Long deptId;

}