package cn.iocoder.yudao.module.fish.service.zkdevicemetric;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevicemetric.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicemetric.ZkDeviceMetricDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 设备指标 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkDeviceMetricService {

    /**
     * 创建设备指标
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkDeviceMetric(@Valid ZkDeviceMetricCreateReqVO createReqVO);

    /**
     * 更新设备指标
     *
     * @param updateReqVO 更新信息
     */
    void updateZkDeviceMetric(@Valid ZkDeviceMetricUpdateReqVO updateReqVO);

    /**
     * 删除设备指标
     *
     * @param id 编号
     */
    void deleteZkDeviceMetric(Long id);

    /**
     * 获得设备指标
     *
     * @param id 编号
     * @return 设备指标
     */
    ZkDeviceMetricDO getZkDeviceMetric(Long id);

    /**
     * 获得设备指标列表
     *
     * @param ids 编号
     * @return 设备指标列表
     */
    List<ZkDeviceMetricDO> getZkDeviceMetricList(Collection<Long> ids);

    /**
     * 获得设备指标分页
     *
     * @param pageReqVO 分页查询
     * @return 设备指标分页
     */
    PageResult<ZkDeviceMetricDO> getZkDeviceMetricPage(ZkDeviceMetricPageReqVO pageReqVO);

    /**
     * 获得设备指标列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 设备指标列表
     */
    List<ZkDeviceMetricDO> getZkDeviceMetricList(ZkDeviceMetricExportReqVO exportReqVO);

}
