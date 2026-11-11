package cn.iocoder.yudao.module.fish.controller.admin.zktaskdetail.to;

import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @创建人 lr
 * @创建时间 2025-09-28
 * @描述
 */
@Data
@NoArgsConstructor
public class UpdateTaskDetailParam {

    private ZkTaskDO originTask;
    private ZkTaskDO updateTask;

    public UpdateTaskDetailParam(ZkTaskDO originTask, ZkTaskDO updateTask) {
        updateTask.setId(originTask.getId());
        this.originTask = originTask;
        this.updateTask = updateTask;
    }
}
