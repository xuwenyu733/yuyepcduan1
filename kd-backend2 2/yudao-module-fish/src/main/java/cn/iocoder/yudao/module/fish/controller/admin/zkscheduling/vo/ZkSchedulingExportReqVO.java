package cn.iocoder.yudao.module.fish.controller.admin.zkscheduling.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 排班 Excel 导出 Request VO，参数和 ZkSchedulingPageReqVO 是一致的")
@Data
public class ZkSchedulingExportReqVO {

    @Schema(description = "排班名称", example = "王五")
    private String schedulingName;

    @Schema(description = "排班描述")
    private String schedulingContent;

    @Schema(description = "排班人id", example = "19601")
    private Long userId;
    private List<Long> userIds;

    @Schema(description = "排班人", example = "赵六")
    private String userName;

    @Schema(description = "排班人所属部门", example = "15136")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "状态(0:待排班;1:已排班;2:已撤销;-1:已删除;)", example = "1")
    private String status;
    /**
     * 任务类型(1:打卡;2:巡查;3:其他;)
     */
    private String schedulingType;

    @Schema(description = "排班人id")
    private String userIdStr;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "文件id", example = "11720")
    private String fid;
    private String schedulingTime;
    private List<String> selectDays;
    private Integer selectDayType;
    private String taskConfigIds; // 巡检配置ids
    private String pondIds; // 存塘ids(多个英文逗号隔开)

}
