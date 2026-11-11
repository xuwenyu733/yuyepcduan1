package cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;


@Schema(description = "管理后台 - fish系统日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FishSystemLogPageReqVO extends PageParam {

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "日志标题")
    private String logTitle;

    @Schema(description = "日志类型(0:fish;1:其他;)", example = "2")
    private String logType;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "原始数据")
    private String originData;

    @Schema(description = "目标数据")
    private String targetData;

    @Schema(description = "原始结果数据")
    private String originRes;

    @Schema(description = "目标结果数据")
    private String targetRes;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "记录人", example = "16725")
    private Long userId;

    @Schema(description = "所属部门", example = "23676")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "7828")
    private String fid;

}
