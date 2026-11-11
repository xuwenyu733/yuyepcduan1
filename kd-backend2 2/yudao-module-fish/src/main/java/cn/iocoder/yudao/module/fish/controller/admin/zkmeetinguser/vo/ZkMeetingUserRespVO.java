package cn.iocoder.yudao.module.fish.controller.admin.zkmeetinguser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 会议用户 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkMeetingUserRespVO extends ZkMeetingUserBaseVO {

    @Schema(description = "序号", required = true, example = "7684")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
