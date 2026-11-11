package cn.iocoder.yudao.module.fish.controller.admin.zkmeetinguser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 会议用户 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkMeetingUserBaseVO {

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

    @Schema(description = "文件id", required = true, example = "5532")
    // @NotNull(message = "文件id不能为空")
    private String fid;

}
