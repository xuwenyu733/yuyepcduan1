package cn.iocoder.yudao.module.fish.service.zkscheduling;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkscheduling.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkscheduling.ZkSchedulingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 排班 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkSchedulingService {

    /**
     * 创建排班
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    ZkSchedulingDO createZkScheduling(@Valid ZkSchedulingCreateReqVO createReqVO);

    /**
     * 更新排班
     * 第一个是 原始的， 第二个是 更新的
     * @param updateReqVO 更新信息
     */
    List<ZkSchedulingDO> updateZkScheduling(@Valid ZkSchedulingUpdateReqVO updateReqVO);

    /**
     * 删除排班
     *
     * @param id 编号
     */
    void deleteZkScheduling(Long id);

    /**
     * 获得排班
     *
     * @param id 编号
     * @return 排班
     */
    ZkSchedulingDO getZkScheduling(Long id);

    /**
     * 获得排班列表
     *
     * @param ids 编号
     * @return 排班列表
     */
    List<ZkSchedulingDO> getZkSchedulingList(Collection<Long> ids);

    /**
     * 获得排班分页
     *
     * @param pageReqVO 分页查询
     * @return 排班分页
     */
    PageResult<ZkSchedulingDO> getZkSchedulingPage(ZkSchedulingPageReqVO pageReqVO);

    /**
     * 获得排班列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 排班列表
     */
    List<ZkSchedulingDO> getZkSchedulingList(ZkSchedulingExportReqVO exportReqVO);

}
