package cn.iocoder.yudao.module.fish.service.zkusemedicinelog;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicinelog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicine.ZkUseMedicineDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicinelog.ZkUseMedicineLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 用药明细 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkUseMedicineLogService {

    /**
     * 创建用药明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    ZkUseMedicineLogDO createZkUseMedicineLog(@Valid ZkUseMedicineLogCreateReqVO createReqVO);

    /**
     * 更新用药明细
     *
     * @param updateReqVO 更新信息
     */
    List<ZkUseMedicineLogDO> updateZkUseMedicineLog(@Valid ZkUseMedicineLogUpdateReqVO updateReqVO);
    List<ZkUseMedicineLogDO> customUpdate(ZkUseMedicineLogDO zkUseMedicineLogDO);

    /**
     * 删除用药明细
     *
     * @param id 编号
     */
    ZkUseMedicineLogDO deleteZkUseMedicineLog(Long id);

    /**
     * 获得用药明细
     *
     * @param id 编号
     * @return 用药明细
     */
    ZkUseMedicineLogDO getZkUseMedicineLog(Long id);

    /**
     * 获得用药明细列表
     *
     * @param ids 编号
     * @return 用药明细列表
     */
    List<ZkUseMedicineLogDO> getZkUseMedicineLogList(Collection<Long> ids);

    /**
     * 获得用药明细分页
     *
     * @param pageReqVO 分页查询
     * @return 用药明细分页
     */
    PageResult<ZkUseMedicineLogDO> getZkUseMedicineLogPage(ZkUseMedicineLogPageReqVO pageReqVO);

    /**
     * 获得用药明细列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 用药明细列表
     */
    List<ZkUseMedicineLogDO> getZkUseMedicineLogList(ZkUseMedicineLogExportReqVO exportReqVO);

    List<ZkUseMedicineLogDO> getByUseMedicineIds(Set<Long> useMedicineIds);
}
