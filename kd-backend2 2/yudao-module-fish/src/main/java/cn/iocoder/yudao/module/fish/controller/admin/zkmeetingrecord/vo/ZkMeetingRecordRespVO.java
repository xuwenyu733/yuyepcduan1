package cn.iocoder.yudao.module.fish.controller.admin.zkmeetingrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 会议记录 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkMeetingRecordRespVO extends ZkMeetingRecordBaseVO {

    @Schema(description = "编号", required = true, example = "21586")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
