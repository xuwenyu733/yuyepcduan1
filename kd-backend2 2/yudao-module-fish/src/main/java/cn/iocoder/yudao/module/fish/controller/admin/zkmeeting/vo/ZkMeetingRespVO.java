package cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 会议 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkMeetingRespVO extends ZkMeetingBaseVO {

    @Schema(description = "序号", required = true, example = "30632")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
