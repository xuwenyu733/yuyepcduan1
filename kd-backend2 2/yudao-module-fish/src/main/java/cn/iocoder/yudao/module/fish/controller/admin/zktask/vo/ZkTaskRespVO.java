package cn.iocoder.yudao.module.fish.controller.admin.zktask.vo;

import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 任务 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkTaskRespVO extends ZkTaskBaseVO {

    @Schema(description = "编号", required = true, example = "21294")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    private String creator;
    private String updater;
}
