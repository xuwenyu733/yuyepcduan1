package cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.vo;

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
* 会议 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
@NoArgsConstructor
public class ZkMeetingBaseVO {

    @Schema(description = "会议名称", example = "张三")
    private String meetingName;

    @Schema(description = "会议备注")
    private String meetingContent;

    @Schema(description = "会议号")
    // @NotNull(message = "会议号")
    private String meetingNum;
    private String meetDay; // 会议日期

    @Schema(description = "开始时间", required = true)
    // @NotNull(message = "开始时间不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime startTime;

    @Schema(description = "结束时间", required = true)
    // @NotNull(message = "结束时间不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime endTime;

    @Schema(description = "状态(0:已预定;1:待开始;2:会议中;3:已结束;-1:已撤销;)", example = "2")
    private String status;

    @Schema(description = "主持人id", example = "27558")
    private Long userId;
    private List<Long> userIds;
    private String userNameStr;

    @Schema(description = "主持人", example = "张三")
    private String userName;

    @Schema(description = "主持人所属部门", example = "22127")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "16608")
    // @NotNull(message = "文件id不能为空")
    private String fid = "0";

    public ZkMeetingBaseVO(String meetingNum, String status, Long userId, String userName, Long deptId) {
        this.meetingNum = meetingNum;
        this.status = status;
        this.userId = userId;
        this.userName = userName;
        this.deptId = deptId;
    }
}
