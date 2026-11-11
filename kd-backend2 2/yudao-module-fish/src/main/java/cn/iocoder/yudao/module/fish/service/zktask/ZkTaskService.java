package cn.iocoder.yudao.module.fish.service.zktask;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;

/**
 * 任务 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkTaskService {

    /**
     * 创建任务
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    ZkTaskDO createZkTask(@Valid ZkTaskCreateReqVO createReqVO);

    /**
     * 更新任务
     *
     * @param updateReqVO 更新信息
     */
    void updateZkTask(@Valid ZkTaskUpdateReqVO updateReqVO);

    /**
     * 删除任务
     *
     * @param id 编号
     */
    void deleteZkTask(Long id);

    /**
     * 获得任务
     *
     * @param id 编号
     * @return 任务
     */
    ZkTaskDO getZkTask(Long id);

    /**
     * 获得任务列表
     *
     * @param ids 编号
     * @return 任务列表
     */
    List<ZkTaskDO> getZkTaskList(Collection<Long> ids);

    /**
     * 获得任务分页
     *
     * @param pageReqVO 分页查询
     * @return 任务分页
     */
    PageResult<ZkTaskDO> getZkTaskPage(ZkTaskPageReqVO pageReqVO);

    /**
     * 获得任务列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 任务列表
     */
    List<ZkTaskDO> getZkTaskList(ZkTaskExportReqVO exportReqVO);

    Integer customSaveBatch(List<ZkTaskDO> taskList);
    List<TaskDetailDO> selectTaskDetails(List<ZkTaskDO> newTaskList);

    Integer customRemoveBySchedulingId(Long schedulingId);

    List<ZkTaskDO> getByScheduleIds(Set<Long> scheduleIds);

    ZkTaskDO getById(Long taskId);

    List<ZkTaskDO> deleteBySchedulingIdAndUserIds(Long schedulingId, List<Long> userIds);

    List<ZkTaskDO> deleteBySchedulingIdAndDays(Long schedulingId, List<String> standTimes);

    void deleteByTaskDOS(List<ZkTaskDO> needDel);

    /**
     * @param zkTaskDOQueryWrapper 通用查询条件构造器
     * @return 需要查询的任务列表
     */
    List<ZkTaskDO> selectByQuery(QueryWrapper<ZkTaskDO> zkTaskDOQueryWrapper);
}
