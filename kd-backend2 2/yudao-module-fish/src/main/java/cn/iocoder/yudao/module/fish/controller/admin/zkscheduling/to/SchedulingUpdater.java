package cn.iocoder.yudao.module.fish.controller.admin.zkscheduling.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zktaskdetail.to.TaskDetailUpdater;
import cn.iocoder.yudao.module.fish.controller.admin.zktaskdetail.to.UpdateTaskDetailParam;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkscheduling.ZkSchedulingDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import cn.iocoder.yudao.module.fish.service.zkscheduling.ZkSchedulingService;
import cn.iocoder.yudao.module.fish.service.zktask.ZkTaskService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * @创建人 lr
 * @创建时间 2025-09-28
 * @描述
 */
@Slf4j
@Component
public class SchedulingUpdater {
    @Resource
    private ZkSchedulingService zkSchedulingService;
    @Autowired
    private AdminUserApi adminUserApi;
    @Resource
    private ZkTaskService zkTaskService;
    @Resource
    private TaskDetailUpdater taskDetailUpdater;

    /**
     * 执行更新逻辑
     * @param updateDo
     */
    public void handleUpdate(ZkSchedulingDO originDo, ZkSchedulingDO updateDo) {
        // 1. 获取已经添加的任务明细
        Set<Long> schedulingIdSet = new HashSet<>();
        schedulingIdSet.add(updateDo.getId());
        List<ZkTaskDO> taskDOS = zkTaskService.getByScheduleIds(schedulingIdSet);
        // 2. 新的 创建任务 列表
        List<ZkTaskDO> newTaskDOS = getTaskListBySchedule(updateDo);
        // 3. 获取 需要改变的
        List<UpdateTaskDetailParam> needUpdate = handleNeedUpdateOrDel(taskDOS, newTaskDOS);
        List<ZkTaskDO> needAdd = handleNeedAdd(taskDOS, newTaskDOS);
        // 4. 执行添加
        Integer count = zkTaskService.customSaveBatch(needAdd);
        taskDetailUpdater.handleNeedUpdate(needUpdate);
    }


    /**
     * 获取 需要删除的任务列表
     * @param originTaskList
     * @param updateTaskList
     * @return
     */
    public List<ZkTaskDO> handleNeedAdd(List<ZkTaskDO> originTaskList, List<ZkTaskDO> updateTaskList) {
        if(EasyListUtil.isEmpty(updateTaskList)) return null;
        List<ZkTaskDO> needAdd = new ArrayList<>();
        if(EasyListUtil.isEmpty(originTaskList) && EasyListUtil.isNotEmpty(updateTaskList)) {
            // 需要新增
            needAdd.addAll(updateTaskList);
        } else {
            for(ZkTaskDO updateTask : updateTaskList) {
                ZkTaskDO oldHas = getByTaskIdAndTaskList(updateTask, originTaskList);
                if(oldHas != null) continue; // 原始任务列表 里面有 则不需要 新增， 处理逻辑在更新里面
                needAdd.add(updateTask);
            }
        }

        return needAdd;
    }

    /**
     * 获取 需要更新的任务列表
     * @param originTaskList
     * @param updateTaskList
     * @return
     */
    public List<UpdateTaskDetailParam> handleNeedUpdateOrDel(List<ZkTaskDO> originTaskList, List<ZkTaskDO> updateTaskList) {
        if(EasyListUtil.isEmpty(updateTaskList) && EasyListUtil.isEmpty(originTaskList)) return null;
        // 1. 获取需要更新和删除的
        List<ZkTaskDO> needDel = new ArrayList<>();
        List<UpdateTaskDetailParam> needUpdate = new ArrayList<>();
        if(EasyListUtil.isEmpty(originTaskList) && EasyListUtil.isNotEmpty(updateTaskList)) {
            // 需要新增
            return null;
        } else {
            for(ZkTaskDO originTask : originTaskList) {
                ZkTaskDO newHas = getByTaskIdAndTaskList(originTask, updateTaskList);
                if(newHas == null) {
                    // 新增加的里面没有，则需要删除
                    needDel.add(originTask);
                } else {
                    // 新增加的里面有，则需要更新
                    UpdateTaskDetailParam updateTaskDetailParam = new UpdateTaskDetailParam(originTask, newHas);
                    needUpdate.add(updateTaskDetailParam);
                }
            }
        }
        // 2. 删除的删除掉
        zkTaskService.deleteByTaskDOS(needDel);

        return needUpdate;
    }

    public ZkTaskDO getByTaskIdAndTaskList(ZkTaskDO template, List<ZkTaskDO> list) {
        if(EasyListUtil.isEmpty(list)) return null;
        for(ZkTaskDO item : list) {
            Boolean userIdFlag = Objects.equals(item.getUserId(), template.getUserId());
            Boolean standTimeFlag = StringUtils.equals(item.getStandTime(), template.getStandTime());
            if(userIdFlag && standTimeFlag) {
                return item;
            }
        }
        return null;
    }

    public List<ZkTaskDO> getTaskListBySchedule(ZkSchedulingDO updateDo) {
        // 1. 获取用户列表
        List<Long> updateUserIds = updateDo.select_userIds();
        if(EasyListUtil.isEmpty(updateUserIds)) return null;
        List<AdminUserRespDTO> userList = adminUserApi.getUserList(updateUserIds);
        if(EasyListUtil.isEmpty(userList)) return null;
        // 2. 创建列表
        updateDo.build_pondIds();
        List<ZkTaskDO> taskList = new ArrayList<>();
        userList.stream().forEach(obj -> {
            List<ZkTaskDO> tempList = ZkTaskDO.buildXunCha_all(obj, updateDo);
            tempList.forEach(ti -> ti.build_pondIdArr(updateDo.getPondIdArr()));
            taskList.addAll(tempList);
        });
        return taskList;
    }


}
