package cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 请假 Excel 导出 Request VO，参数和 ZkAskforLeavePageReqVO 是一致的")
@Data
public class ZkAskforLeaveExportReqVO {

    @Schema(description = "请假名称", example = "李四")
    private String leaveName;

    @Schema(description = "请假备注")
    private String leaveContent;

    @Schema(description = "请假天数")
    private Object leaveDay;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] endTime;

    @Schema(description = "状态(0:待审批;1:已审批;2:已销假;-1:待申请;)", example = "1")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "请假人id", example = "27909")
    private Long userId;
    private String userName;

    @Schema(description = "请假人所属部门", example = "16393")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "1533")
    private String fid;
    private String processInstanceId;
    private Long approveUserId; // 上级审批人
    private Long noticeUserId; // 抄送人

}
