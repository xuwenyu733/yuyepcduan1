package cn.iocoder.yudao.module.fish.controller.admin.zkmeetingrecord.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 会议记录 Excel 导出 Request VO，参数和 ZkMeetingRecordPageReqVO 是一致的")
@Data
public class ZkMeetingRecordExportReqVO {

    @Schema(description = "会议id", example = "17384")
    private Long meetingId;
    private String meetingName;
    private String meetingNum;
    private String meetDay; // 会议日期

    @Schema(description = "记录名称", example = "张三")
    private String meetingRecordName;

    @Schema(description = "记录描述")
    private String meetingRecordContent;

    @Schema(description = "记录概要")
    private String meetingPoint;

    @Schema(description = "记录总结")
    private String meetingSummary;
    private String meetToDo; // 会议待办

    @Schema(description = "记录人id", example = "1641")
    private Long userId;

    @Schema(description = "记录人", example = "王五")
    private String userName;

    @Schema(description = "记录人所属部门", example = "11495")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "状态(0:已记录;1:待入库;2:已入库;3:待评价;3:已评价;-1:已删除;)", example = "2")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "文件id", example = "14734")
    private String fid;
    private String localVideoUrls; // 会议录屏url
    private String remoteVideoUrls; // 会议录屏阿里云网盘url
    private Integer videoGetCount; // 录屏获取次数

}
