package cn.iocoder.yudao.module.fish.controller.admin.zktask.vo;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 任务创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkTaskCreateReqVO extends ZkTaskBaseVO {

    private String token;

    private List<TaskDetailDO> taskDetailDOS;

    public List<TaskDetailDO> selectTaskDetailList(ZkTaskDO zkTask) {
        if(EasyListUtil.isEmpty(taskDetailDOS)) return new ArrayList<>();
        LocalDateTime finishTime = this.getFinishTime();
        taskDetailDOS.forEach(obj -> obj.setFinishTime(finishTime).setTaskId(zkTask.getId()));
        return taskDetailDOS;
    }
}
