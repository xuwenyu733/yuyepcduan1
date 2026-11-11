package cn.iocoder.yudao.module.fish.controller.admin.zktaskdetail.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 任务明细 Excel 导出 Request VO，参数和 TaskDetailPageReqVO 是一致的")
@Data
public class TaskDetailExportReqVO {

    @Schema(description = "任务id(zk_task)", example = "20531")
    private Long taskId;
    private Long schedulingId; // 排班表id

    @Schema(description = "检测名称", example = "赵六")
    private String checkName;

    @Schema(description = "检测类型(1:养殖区;2:设备区;3:定期巡检_养殖;4:定期巡检_设备区;5:定期巡检_仓库区;6:;)", example = "1")
    private String checkType;

    @Schema(description = "检测config")
    private String checkContent;

    @Schema(description = "检测res")
    private String checkRes;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    private Long workshopId; // 车间id
    private String workshopName; // 车间名称
    private String pondSystemName; // 系统名称
    private Long pondSystemId; // 系统id
    private String pondNum; // 系统编号
    private String pondName; // 存塘名称
    private Long pondId; // 存塘id
    private String pondDetailNum; // 存塘编号
    private String batchNumber; // 批次编号

    private Long userId; // 用户id
    private Long deptId; // 部门id
    private Long tenantId; // 租户id
}
