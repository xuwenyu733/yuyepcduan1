package cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
* 请假 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkAskforLeaveBaseVO {

    @Schema(description = "请假名称", example = "李四")
    private String leaveName;

    @Schema(description = "请假备注")
    private String leaveContent;

    @Schema(description = "请假天数")
    private Object leaveDay;

    @Schema(description = "开始时间", required = true)
    @NotNull(message = "开始时间不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime startTime;

    @Schema(description = "结束时间", required = true)
    @NotNull(message = "结束时间不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime endTime;

    @Schema(description = "状态(0:待审批;1:已审批;2:已销假;-1:待申请;)", example = "1")
    private String status;

    @Schema(description = "请假人id", example = "27909")
    private Long userId;
    private String userName;

    @Schema(description = "请假人所属部门", example = "16393")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "1533")
    @NotNull(message = "文件id不能为空")
    private String fid;
    private String processInstanceId;
    private String askforType; // 请假类型(1:事假;2:调休;3:病假;4:产假;5:陪产假;6:婚嫁;7:其他;)
    private Long approveUserId; // 上级审批人
    private Long noticeUserId; // 抄送人

}
