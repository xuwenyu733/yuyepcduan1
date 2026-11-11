package cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 会议分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkMeetingPageReqVO extends PageParam {

    @Schema(description = "会议名称", example = "张三")
    private String meetingName;

    @Schema(description = "会议备注")
    private String meetingContent;

    @Schema(description = "会议编号")
    private String meetingNum;
    private String meetDay; // 会议日期

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] endTime;

    @Schema(description = "状态(0:已预定;1:待开始;2:会议中;3:已结束;-1:已撤销;)", example = "2")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "主持人id", example = "27558")
    private Long userId;
    private List<Long> userIds;
    private String userNameStr;

    @Schema(description = "主持人", example = "张三")
    private String userName;

    @Schema(description = "主持人所属部门", example = "22127")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "16608")
    private String fid;

}
