package cn.iocoder.yudao.module.fish.service.zktaskdetail;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zktaskdetail.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zktaskdetail.TaskDetailConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zktaskdetail.TaskDetailMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 任务明细 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class TaskDetailServiceImpl implements TaskDetailService {

    @Resource
    private TaskDetailMapper taskDetailMapper;

    @Override
    public Long createTaskDetail(TaskDetailCreateReqVO createReqVO) {
        // 插入
        TaskDetailDO taskDetail = BeanUtils.toBean(createReqVO, TaskDetailDO.class);
        taskDetailMapper.insert(taskDetail);
        // 返回
        return taskDetail.getId();
    }

    @Override
    public void updateTaskDetail(TaskDetailUpdateReqVO updateReqVO) {
        // 校验存在
        validateTaskDetailExists(updateReqVO.getId());
        // 更新
        TaskDetailDO updateObj = BeanUtils.toBean(updateReqVO, TaskDetailDO.class);
        taskDetailMapper.updateById(updateObj);
    }

    @Override
    public void deleteTaskDetail(Long id) {
        // 校验存在
        validateTaskDetailExists(id);
        // 删除
        taskDetailMapper.deleteById(id);
    }

    private void validateTaskDetailExists(Long id) {
        if (taskDetailMapper.selectById(id) == null) {
            throw exception(TASK_DETAIL_NOT_EXISTS);
        }
    }

    @Override
    public TaskDetailDO getTaskDetail(Long id) {
        return taskDetailMapper.selectById(id);
    }

    @Override
    public List<TaskDetailDO> getTaskDetailList(Collection<Long> ids) {
        return taskDetailMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<TaskDetailDO> getTaskDetailPage(TaskDetailPageReqVO pageReqVO) {
        return taskDetailMapper.selectPage(pageReqVO);
    }

    @Override
    public List<TaskDetailDO> getTaskDetailList(TaskDetailExportReqVO exportReqVO) {
        return taskDetailMapper.selectList(exportReqVO);
    }

    @Override
    public void customSaveBatch(List<TaskDetailDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        taskDetailMapper.insertBatch(list);
    }

    @Override
    public Integer customRemoveBySchedulingId(Long schedulingId) {
        TaskDetailDO taskDetailDO = new TaskDetailDO().build_schedulingId(schedulingId);
        Integer count = taskDetailMapper.delete(new QueryWrapper<>(taskDetailDO));
        return count;
    }

    @Override
    public List<TaskDetailDO> getByTaskIds(List<Long> taskIds) {
        if(EasyListUtil.isEmpty(taskIds)) return new ArrayList<>();
        QueryWrapper<TaskDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("task_id", taskIds);
        List<TaskDetailDO> detailDOS = taskDetailMapper.selectList(queryWrapper);

        return detailDOS;
    }

    @Override
    public void customUpdateBatch(List<TaskDetailDO> taskDetailDOS) {
        if(EasyListUtil.isEmpty(taskDetailDOS)) return;
        List<TaskDetailDO> needAdd = taskDetailDOS.stream().filter(obj -> obj.getId() == null).collect(Collectors.toList());
        List<TaskDetailDO> needUpdate = taskDetailDOS.stream().filter(obj -> obj.getId() != null).collect(Collectors.toList());
        if(EasyListUtil.isNotEmpty(needUpdate)) {
            // 1. 删除 历史
            needDel(needUpdate);
            // 2. 同步 最新
            taskDetailMapper.updateBatch(taskDetailDOS, taskDetailDOS.size());
        }
        if(EasyListUtil.isNotEmpty(needAdd))
            taskDetailMapper.insertBatch(needAdd);
    }

    @Override
    public List<TaskDetailDO> getByIds(Set<Long> taskDetailIds) {
        if(CollectionUtils.isAnyEmpty(taskDetailIds)) return new ArrayList<>();
        QueryWrapper<TaskDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", taskDetailIds);
        List<TaskDetailDO> detailDOS = taskDetailMapper.selectList(queryWrapper);
        return detailDOS;
    }

    @Override
    public Integer deleteByTaskIds(List<Long> taskIds) {
        if(EasyListUtil.isEmpty(taskIds)) return 0;
        QueryWrapper<TaskDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("task_id", taskIds);

        return taskDetailMapper.delete(queryWrapper);
    }

    @Override
    public void deleteByTaskDetailList(List<TaskDetailDO> needDel) {
        if(EasyListUtil.isEmpty(needDel)) return;
        Set<Long> detailIdSet = needDel.stream().map(obj -> obj.getId()).collect(Collectors.toSet());
        taskDetailMapper.deleteBatchIds(detailIdSet);
    }

    @Override
    public List<ZkTaskDO> getByTypesAndPondId(List<String> typeList, Long pondId) {
        // 1. 获取 任务列表
        QueryWrapper<ZkTaskDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("ztd.check_type", typeList)
                .eq("ztd.pond_id", pondId);
        List<ZkTaskDO> taskDOS = taskDetailMapper.getByTypesAndPondId(queryWrapper);
        // 2. 获取明细信息
        if(EasyListUtil.isEmpty(taskDOS)) return taskDOS;
        Set<Long> taskIdSet = taskDOS.stream().map(ZkTaskDO::getId).collect(Collectors.toSet());
        LambdaQueryWrapper<TaskDetailDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(TaskDetailDO::getTaskId, taskIdSet);
        List<TaskDetailDO> detailDOS = taskDetailMapper.selectList(lambdaQueryWrapper);
        if(EasyListUtil.isEmpty(detailDOS)) return taskDOS;
        // 3. 分组进行设置
        Map<Long, List<TaskDetailDO>> collect = detailDOS.stream().collect(Collectors.groupingBy(TaskDetailDO::getTaskId));
        for(ZkTaskDO taskDO : taskDOS) {
            List<TaskDetailDO> tempList = collect.get(taskDO.getId());
            taskDO.setTaskDetailDOS(tempList);
        }

        return taskDOS;
    }

    public void needDel(List<TaskDetailDO> needUpdate) {
        // 1. 获取所有
        TaskDetailDO first = needUpdate.get(0);
        List<TaskDetailDO> byTaskIds = getByTaskIds(Arrays.asList(first.getTaskId()));
        if(EasyListUtil.isEmpty(byTaskIds)) return;
        List<Long> needDelIdArr = new ArrayList<>();
        for(TaskDetailDO history : byTaskIds) {
            Boolean flag = false;
            for(TaskDetailDO need : needUpdate) {
                if(Objects.equals(need.getId(), history.getId())) {
                    flag = true;
                    break;
                }
            }
            if(!flag) needDelIdArr.add(history.getId());
        }
        // 2. 执行删除
        if(EasyListUtil.isNotEmpty(needDelIdArr)) {
            QueryWrapper<TaskDetailDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", needDelIdArr);
            taskDetailMapper.delete(queryWrapper);
        }
    }

}
