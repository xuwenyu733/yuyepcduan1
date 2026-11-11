package cn.iocoder.yudao.module.fish.service.zktaskdetail;

import java.util.*;

import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zktaskdetail.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 任务明细 Service 接口
 *
 * @author 系统管理员
 */
public interface TaskDetailService {

    /**
     * 创建任务明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTaskDetail(@Valid TaskDetailCreateReqVO createReqVO);

    /**
     * 更新任务明细
     *
     * @param updateReqVO 更新信息
     */
    void updateTaskDetail(@Valid TaskDetailUpdateReqVO updateReqVO);

    /**
     * 删除任务明细
     *
     * @param id 编号
     */
    void deleteTaskDetail(Long id);

    /**
     * 获得任务明细
     *
     * @param id 编号
     * @return 任务明细
     */
    TaskDetailDO getTaskDetail(Long id);

    /**
     * 获得任务明细列表
     *
     * @param ids 编号
     * @return 任务明细列表
     */
    List<TaskDetailDO> getTaskDetailList(Collection<Long> ids);

    /**
     * 获得任务明细分页
     *
     * @param pageReqVO 分页查询
     * @return 任务明细分页
     */
    PageResult<TaskDetailDO> getTaskDetailPage(TaskDetailPageReqVO pageReqVO);

    /**
     * 获得任务明细列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 任务明细列表
     */
    List<TaskDetailDO> getTaskDetailList(TaskDetailExportReqVO exportReqVO);

    void customSaveBatch(List<TaskDetailDO> list);

    Integer customRemoveBySchedulingId(Long schedulingId);

    List<TaskDetailDO> getByTaskIds(List<Long> taskIds);

    void customUpdateBatch(List<TaskDetailDO> taskDetailDOS);

    List<TaskDetailDO> getByIds(Set<Long> taskDetailIds);

    Integer deleteByTaskIds(List<Long> taskIds);

    void deleteByTaskDetailList(List<TaskDetailDO> needDel);

    /**
     * 根据 巡检类型列表、存塘id 查询 巡检数据，方便使用
     * @param typeList cn.iocoder.yudao.module.fish.controller.admin.zktask.to.TaskDetailCheckTypeEnum
     * @param pondId 存塘id
     * @return 任务数据
     */
    List<ZkTaskDO> getByTypesAndPondId(List<String> typeList, Long pondId);
}
