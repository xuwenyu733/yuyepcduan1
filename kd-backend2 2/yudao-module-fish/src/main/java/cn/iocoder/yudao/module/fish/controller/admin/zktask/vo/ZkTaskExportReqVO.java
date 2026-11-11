package cn.iocoder.yudao.module.fish.controller.admin.zktask.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 任务 Excel 导出 Request VO，参数和 ZkTaskPageReqVO 是一致的")
@Data
public class ZkTaskExportReqVO {

    @Schema(description = "业务id", example = "20049")
    private Long busId;
    private Long schedulingId;

    @Schema(description = "任务名称", example = "李四")
    private String taskName;

    @Schema(description = "任务描述")
    private String taskContent;

    @Schema(description = "任务反馈")
    private String taskPoint;

    @Schema(description = "任务结果")
    private String taskSummary;

    @Schema(description = "执行人id", example = "19925")
    private Long userId;

    @Schema(description = "执行人", example = "赵六")
    private String userName;

    @Schema(description = "执行人所属部门", example = "17315")
    private Long deptId;
    private Long tenantId;
    /**
     * 任务类型 参考字典配置：   zk_scheduling_type
     */
    private String taskType;

    @Schema(description = "状态(0:已下发;1:已通知;2:待完成;3:已完成;4:已评估;-1:已删除;)", example = "2")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "文件id", example = "25335")
    private String fid;
    private String fileUrl; // 详情url

    @Schema(description = "流程实例", example = "8760")
    private String processInstanceId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime finishTime; // 任务完成时间
    private String taskConfigIds; // 巡检配置ids
    private String checkTypes; // 巡检类型(多个英文,号分隔)
}
