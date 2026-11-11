package cn.iocoder.yudao.module.fish.controller.admin.zkfishproofing.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 打样记录 Excel 导出 Request VO，参数和 ZkFishProofingPageReqVO 是一致的")
@Data
public class ZkFishProofingExportReqVO {

    @Schema(description = "批次编号")
    private String batchNumber;

    @Schema(description = "车间id", example = "30219")
    private Long workshopId;

    @Schema(description = "车间名称", example = "王五")
    private String workshopName;

    @Schema(description = "系统名称", example = "王五")
    private String pondSystemName;

    @Schema(description = "系统id", example = "19926")
    private Long pondSystemId;

    @Schema(description = "系统编号")
    private String pondNum;

    @Schema(description = "存塘名称", example = "赵六")
    private String pondName;

    @Schema(description = "存塘id", example = "13440")
    private Long pondId;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "初始规格")
    private BigDecimal initSpecs;

    @Schema(description = "平均打样体长(cm)")
    private BigDecimal avgBodyLength;

    @Schema(description = "平均打样规格(g)")
    private BigDecimal avgSpecs;

    @Schema(description = "打样数量(尾)")
    private Long currNum;

    @Schema(description = "任务明细id", example = "2661")
    private Long taskDetailId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "记录人", example = "27902")
    private Long userId;

    @Schema(description = "所属部门", example = "22929")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "25230")
    private String fid;
    private String statisticsType; // 统计类型(0:未统计;1:已统计;)

}
