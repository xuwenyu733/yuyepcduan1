package cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 会议更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkMeetingUpdateReqVO extends ZkMeetingBaseVO {

    @Schema(description = "序号", required = true, example = "30632")
    @NotNull(message = "序号不能为空")
    private Long id;

}
