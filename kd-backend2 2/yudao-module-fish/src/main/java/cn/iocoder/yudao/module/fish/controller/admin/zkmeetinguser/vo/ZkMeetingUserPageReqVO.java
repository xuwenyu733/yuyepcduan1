package cn.iocoder.yudao.module.fish.controller.admin.zkmeetinguser.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 会议用户分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkMeetingUserPageReqVO extends PageParam {

    @Schema(description = "会议id", example = "11522")
    private Long meetingId;
    private String meetingNum;
    private String meetToDo; // 会议待办
    private String toDoFeedback; // 待办反馈

    @Schema(description = "参会人id", example = "20068")
    private Long userId;

    @Schema(description = "参会人", example = "王五")
    private String userName;

    @Schema(description = "参会人所属部门", example = "2206")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "用户类型(0:参会人;1:记录人;2:主持人;3:列席人员;3:特邀人员;-1:旁听人员;)", example = "1")
    private String userType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "文件id", example = "5532")
    private String fid;

}
