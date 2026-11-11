package cn.iocoder.yudao.module.fish.controller.admin.zktaskdetail.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import cn.iocoder.yudao.module.fish.service.zktask.ZkTaskService;
import cn.iocoder.yudao.module.fish.service.zktaskdetail.TaskDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @创建人 lr
 * @创建时间 2025-09-28
 * @描述
 */
@Slf4j
@Component
public class TaskDetailUpdater {
    @Resource
    private TaskDetailService taskDetailService;
    @Resource
    private ZkTaskService zkTaskService;

    /**
     * 更新 排班对应的任务列表
     * @param taskDOList
     */
    public void handleNeedUpdate(List<UpdateTaskDetailParam> taskDOList) {
        if(EasyListUtil.isEmpty(taskDOList)) return;
        List<ZkTaskDO> updateTaskList = taskDOList.stream().map(obj -> obj.getUpdateTask()).collect(Collectors.toList());
        Set<Long> taskIdSet = taskDOList.stream().map(obj -> obj.getOriginTask().getId()).collect(Collectors.toSet());
        // 1. 获取 新的明细
        List<TaskDetailDO> updateDetailList = zkTaskService.selectTaskDetails(updateTaskList);
        // 2. 获取原来的明细
        List<TaskDetailDO> originDetailList = taskDetailService.getByTaskIds(new ArrayList<>(taskIdSet));
        // 3. 寻找需要删除、更新、添加
        handleNeedUpdateOrDel(originDetailList, updateDetailList);
        handleNeedAdd(originDetailList, updateDetailList);
    }


    /**
     * 获取 需要添加的任务列表
     * @param originList
     * @param updateList
     * @return
     */
    public List<TaskDetailDO> handleNeedAdd(List<TaskDetailDO> originList, List<TaskDetailDO> updateList) {
        if(EasyListUtil.isEmpty(updateList)) return null;
        // 1. 获取 需要 添加的任务明细
        List<TaskDetailDO> needAdd = new ArrayList<>();
        if(EasyListUtil.isEmpty(originList) && EasyListUtil.isNotEmpty(updateList)) {
            // 需要新增
            needAdd.addAll(updateList);
        } else {
            for(TaskDetailDO updateTask : updateList) {
                TaskDetailDO oldHas = getByTaskIdAndTaskList(updateTask, originList);
                if(oldHas != null) continue; // 原始任务列表 里面有 则不需要 新增， 处理逻辑在更新里面
                needAdd.add(updateTask);
            }
        }
        // 2. 执行 任务更新
        taskDetailService.customSaveBatch(needAdd);

        return needAdd;
    }

    /**
     * 获取 需要更新的任务列表
     * @param originList
     * @param updateList
     * @return
     */
    public void handleNeedUpdateOrDel(List<TaskDetailDO> originList, List<TaskDetailDO> updateList) {
        if(EasyListUtil.isEmpty(updateList) && EasyListUtil.isEmpty(originList)) return;
        // 1. 获取需要更新和删除的
        List<TaskDetailDO> needDel = new ArrayList<>();
        List<TaskDetailDO> needUpdate = new ArrayList<>();
        if(EasyListUtil.isEmpty(originList) && EasyListUtil.isNotEmpty(updateList)) {
            // 需要新增
            return ;
        } else {
            for(TaskDetailDO originTask : originList) {
                TaskDetailDO newHas = getByTaskIdAndTaskList(originTask, updateList);
                if(newHas == null) {
                    // 新增加的里面没有，则需要删除
                    needDel.add(originTask);
                } else {
                    // 新增加的里面有，则需要更新
                    CommonUtil.copyPropertiesIgnoreNull(newHas, originTask);
                    needUpdate.add(originTask);
                }
            }
        }
        // 2. 删除的删除掉
        taskDetailService.deleteByTaskDetailList(needDel);
        // 3. 更新需要更新的
        taskDetailService.customUpdateBatch(needUpdate);
    }

    public TaskDetailDO getByTaskIdAndTaskList(TaskDetailDO template, List<TaskDetailDO> list) {
        if(EasyListUtil.isEmpty(list)) return null;
        for(TaskDetailDO item : list) {
            Boolean taskIdFlag = Objects.equals(item.getTaskId(), template.getTaskId());
            Boolean taskConfigIdFlag = Objects.equals(item.getTaskConfigId(), template.getTaskConfigId());
            Boolean pondIdFlag = Objects.equals(item.getPondId(), template.getPondId());
            if(taskIdFlag && taskConfigIdFlag && pondIdFlag) {
                return item;
            }
        }
        return null;
    }
}
