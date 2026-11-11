package cn.iocoder.yudao.module.fish.controller.admin.zktask.vo;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 任务更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkTaskUpdateReqVO extends ZkTaskBaseVO {

    @Schema(description = "编号", required = true, example = "21294")
    @NotNull(message = "编号不能为空")
    private Long id;
    private String token;

    private List<TaskDetailDO> taskDetailDOS;

    public List<TaskDetailDO> selectTaskDetailList() {
        if(EasyListUtil.isEmpty(taskDetailDOS)) return new ArrayList<>();
        LocalDateTime finishTime = this.getFinishTime();
        taskDetailDOS.forEach(obj -> obj.setFinishTime(finishTime));
        return taskDetailDOS;
    }
}
