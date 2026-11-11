package cn.iocoder.yudao.module.fish.service.zktask;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulingtype.ZkSchedulingTypeDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.service.zkschedulingtype.ZkSchedulingTypeService;
import cn.iocoder.yudao.module.fish.service.zktaskdetail.TaskDetailService;
import cn.iocoder.yudao.module.fish.utils.QueryUtil;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zktask.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zktask.ZkTaskConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zktask.ZkTaskMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 任务 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkTaskServiceImpl implements ZkTaskService {

    @Resource
    private ZkTaskMapper zkTaskMapper;
    @Autowired
    private AdminUserApi adminUserApi;

    @Autowired
    private ZkSchedulingTypeService zkSchedulingTypeService;
    @Autowired
    private TaskDetailService taskDetailService;
    @Autowired
    private ZkPondService zkPondService;

    @Override
    public ZkTaskDO createZkTask(ZkTaskCreateReqVO createReqVO) {
        // 插入
        ZkTaskDO zkTask = BeanUtils.toBean(createReqVO, ZkTaskDO.class);
        zkTaskMapper.insert(zkTask);
        // 返回
        return zkTask;
    }

    @Override
    public void updateZkTask(ZkTaskUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkTaskExists(updateReqVO.getId());
        // 更新
        ZkTaskDO updateObj = BeanUtils.toBean(updateReqVO, ZkTaskDO.class);
        zkTaskMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkTask(Long id) {
        // 校验存在
        validateZkTaskExists(id);
        // 删除
        zkTaskMapper.deleteById(id);
    }

    private void validateZkTaskExists(Long id) {
        if (zkTaskMapper.selectById(id) == null) {
            throw exception(ZK_TASK_NOT_EXISTS);
        }
    }

    @Override
    public ZkTaskDO getZkTask(Long id) {
        return zkTaskMapper.selectById(id);
    }

    @Override
    public List<ZkTaskDO> getZkTaskList(Collection<Long> ids) {
        return zkTaskMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkTaskDO> getZkTaskPage(ZkTaskPageReqVO pageReqVO) {
        AdminUserRespDTO adminUserRespDTO = adminUserApi.getUser(SecurityFrameworkUtils.getLoginUserId());
        return zkTaskMapper.selectPage(pageReqVO.build_adminUserRespDTO(adminUserRespDTO));
    }

    @Override
    public List<ZkTaskDO> getZkTaskList(ZkTaskExportReqVO exportReqVO) {
        return zkTaskMapper.selectList(exportReqVO);
    }

    @Override
    public Integer customSaveBatch(List<ZkTaskDO> taskList) {
        if(EasyListUtil.isEmpty(taskList)) return 0;
        // 1. 校验是否有重复添加的数据
        List<ZkTaskDO> newTaskList = filterHasAdd(taskList);
        if(EasyListUtil.isEmpty(newTaskList)) return 0;
        // 2. 执行添加逻辑
        zkTaskMapper.insertBatch(newTaskList);
        // 3. 如果是 巡检任务，则有 taskConfigIds， 需要添加 zk_task_detail 数据
        insertTaskDetails(newTaskList);
        return newTaskList.size();
    }

    public void insertTaskDetails(List<ZkTaskDO> newTaskList) {
        // 1. 添加 zk_task_detail 数据
        List<TaskDetailDO> list = selectTaskDetails(newTaskList);
        if(EasyListUtil.isEmpty(list)) return;
        taskDetailService.customSaveBatch(list);
    }
    public List<TaskDetailDO> selectTaskDetails(List<ZkTaskDO> newTaskList) {
        List<ZkPondDO> pondList = getPondList(newTaskList);
        // 1. 获取 巡检类型
        HashSet<Long> taskConfigIdSet = new HashSet<>();
        for(ZkTaskDO taskItem : newTaskList) {
            if(StringUtils.isNotBlank(taskItem.getTaskConfigIds())) {
                String[] split = taskItem.getTaskConfigIds().split(",");
                Arrays.stream(split).forEach(i -> taskConfigIdSet.add(Long.parseLong(i)));
            }
        }
        // 2. 获取配置数据
        if(taskConfigIdSet.size() == 0) return null;
        List<ZkSchedulingTypeDO> zkSchedulingTypeList = zkSchedulingTypeService.getZkSchedulingTypeList(taskConfigIdSet);
        if(EasyListUtil.isEmpty(zkSchedulingTypeList)) return null;
        // 3. 添加 zk_task_detail 数据
        List<TaskDetailDO> list = new ArrayList();
        for(ZkTaskDO taskItem : newTaskList) {
            if(StringUtils.isBlank(taskItem.getTaskConfigIds())) continue;
            String[] split = taskItem.getTaskConfigIds().split(",");
            for(String configId : split) {
                ZkSchedulingTypeDO remoteSchedulingType = getSchedulingTypeById(zkSchedulingTypeList, Long.parseLong(configId));
                // Long taskId, String checkName, String checkType, String checkContent
                for(ZkPondDO zkPondDO : pondList) {
                    TaskDetailDO temp = new TaskDetailDO(taskItem.getId(), remoteSchedulingType.getCheckName(), remoteSchedulingType.getCheckType(), remoteSchedulingType.getCheckContent());
                    temp.build_pond(zkPondDO).build_taskConfigId(remoteSchedulingType.getId())
                            .build_authInfo(1L, taskItem.getDeptId(), taskItem.getUserId());
                    list.add(temp.build_schedulingId(taskItem.getSchedulingId()));
                }
            }
        }
        return list;
    }
    public List<ZkPondDO> getPondList(List<ZkTaskDO> newTaskList) {
        List<ZkPondDO> res = new ArrayList<>();
        ZkPondDO zkPondDO = zkPondService.getOne();
        res.add(zkPondDO);
        if(EasyListUtil.isEmpty(newTaskList)) return res;
        Set<Long> pondIdSet = new HashSet<>();
        for(ZkTaskDO item : newTaskList) {
            if(EasyListUtil.isEmpty(item.getPondIdArr())) continue;
            for(Long pondId : item.getPondIdArr()) {
                pondIdSet.add(pondId);
            }
        }
        if(CollectionUtils.isAnyEmpty(pondIdSet)) return res;
        List<ZkPondDO> zkPondDOS = zkPondService.selectByPondIds(pondIdSet);
        if(EasyListUtil.isEmpty(zkPondDOS)) return res;
        return zkPondDOS;
    }

    public ZkSchedulingTypeDO getSchedulingTypeById(List<ZkSchedulingTypeDO> zkSchedulingTypeList, Long remoteId) {
        for (ZkSchedulingTypeDO item : zkSchedulingTypeList) {
            if(Objects.equals(item.getId(), remoteId)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 过滤掉已经添加了的数据
     * @return
     */
    public List<ZkTaskDO> filterHasAdd(List<ZkTaskDO> taskList) {
        // 1. 获取历史已经排班的数据
        List<ZkTaskDO> hasAddTasks = getHasAdd(taskList);
        if(EasyListUtil.isEmpty(hasAddTasks)) return taskList;
        // 2. 不为空则需要过滤
        List<ZkTaskDO> new_taskList = new ArrayList<>();
        taskList.forEach(newItem -> {
            boolean judgeIsAdd = false;
            for(ZkTaskDO oldItem : hasAddTasks) {
                judgeIsAdd = Objects.equals(newItem.getUserId(), oldItem.getUserId())
                        && StringUtils.equals(newItem.getTaskType(), oldItem.getTaskType())
                        && Objects.equals(newItem.getSchedulingId(), oldItem.getSchedulingId())
                        && StringUtils.equals(newItem.getStandTime(), oldItem.getStandTime());
                // System.out.println(String.format("是否已经添加:%s,%s=%s,%s=%s,%s=%s", judgeIsAdd, newItem.getUserId(), oldItem.getUserId(),
                //         newItem.getTaskType(), oldItem.getTaskType(), newItem.getStandTime(), oldItem.getStandTime()));
                if(judgeIsAdd) break;
            }
            if(!judgeIsAdd) new_taskList.add(newItem);
        });
        return new_taskList;
    }

    public List<ZkTaskDO> getHasAdd(List<ZkTaskDO> taskList) {
        if(EasyListUtil.isEmpty(taskList)) return new ArrayList<>();
        // 1. 获取历史已经排班的数据
        String curr_taskMonth = taskList.get(0).getTaskMonth();
        Set<Long> userIds = taskList.stream().map(obj -> obj.getUserId()).collect(Collectors.toSet());
        QueryWrapper<ZkTaskDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds)
                .eq("task_month", curr_taskMonth);
        List<ZkTaskDO> zkTaskDOS = zkTaskMapper.selectList(queryWrapper);
        return zkTaskDOS;
    }

    @Override
    public Integer customRemoveBySchedulingId(Long schedulingId) {
        ZkTaskDO zkTaskDO = new ZkTaskDO().build_schedulingId(schedulingId);
        Integer count = zkTaskMapper.delete(new QueryWrapper<>(zkTaskDO));
        return count;
    }

    @Override
    public List<ZkTaskDO> getByScheduleIds(Set<Long> scheduleIds) {
        if(CollectionUtils.isAnyEmpty(scheduleIds)) return new ArrayList<>();
        QueryWrapper<ZkTaskDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("scheduling_id", scheduleIds);
        List<ZkTaskDO> zkTaskDOS = zkTaskMapper.selectList(queryWrapper);
        return zkTaskDOS;
    }

    @Override
    public ZkTaskDO getById(Long taskId) {
        if(taskId == null) return null;
        ZkTaskDO zkTaskDO = zkTaskMapper.selectById(taskId);
        if(zkTaskDO == null) return null;
        List<TaskDetailDO> taskDetailDOS = taskDetailService.getByTaskIds(List.of(taskId));
        zkTaskDO.setTaskDetailDOS(taskDetailDOS);
        return zkTaskDO;
    }

    @Override
    public List<ZkTaskDO> deleteBySchedulingIdAndUserIds(Long schedulingId, List<Long> userIds) {
        // 1. 获取所有任务信息
        QueryWrapper<ZkTaskDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("scheduling_id", schedulingId).in("user_id", userIds);

        return deleteByQueryWrapper(queryWrapper);
    }

    @Override
    public List<ZkTaskDO> deleteBySchedulingIdAndDays(Long schedulingId, List<String> standTimes) {
        // 1. 获取所有任务信息
        QueryWrapper<ZkTaskDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("scheduling_id", schedulingId).in("stand_time", standTimes);

        return deleteByQueryWrapper(queryWrapper);
    }

    @Override
    public void deleteByTaskDOS(List<ZkTaskDO> needDel) {
        if(EasyListUtil.isEmpty(needDel)) return;
        // 1. 删除 task列表
        Set<Long> taskIds = needDel.stream().map(ZkTaskDO::getId).collect(Collectors.toSet());
        zkTaskMapper.deleteBatchIds(taskIds);
        // 2. 删除任务明细
        taskDetailService.deleteByTaskIds(new ArrayList<>(taskIds));
    }

    @Override
    public List<ZkTaskDO> selectByQuery(QueryWrapper<ZkTaskDO> zkTaskDOQueryWrapper) {
        return zkTaskMapper.selectList(zkTaskDOQueryWrapper);
    }

    public List<ZkTaskDO> deleteByQueryWrapper(QueryWrapper<ZkTaskDO> queryWrapper) {
        List<ZkTaskDO> taskDOS = zkTaskMapper.selectList(queryWrapper);
        if(EasyListUtil.isEmpty(taskDOS)) return null; // 需要删除的任务为0
        // 2. 执行明细删除
        List<Long> taskIds = taskDOS.stream().map(ZkTaskDO::getId).collect(Collectors.toList());
        int deleteBatchIds = zkTaskMapper.deleteBatchIds(taskIds);
        // 3. 删除任务明细
        taskDetailService.deleteByTaskIds(taskIds);

        return taskDOS;
    }

}
