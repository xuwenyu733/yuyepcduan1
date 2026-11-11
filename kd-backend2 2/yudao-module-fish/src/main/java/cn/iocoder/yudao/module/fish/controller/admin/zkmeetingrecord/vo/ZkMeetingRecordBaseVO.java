package cn.iocoder.yudao.module.fish.controller.admin.zkmeetingrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 会议记录 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkMeetingRecordBaseVO {

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

    @Schema(description = "文件id", required = true, example = "14734")
    // @NotNull(message = "文件id不能为空")
    private String fid;
    private String localVideoUrls; // 会议录屏url
    private String remoteVideoUrls; // 会议录屏阿里云网盘url
    private Integer videoGetCount; // 录屏获取次数

}
