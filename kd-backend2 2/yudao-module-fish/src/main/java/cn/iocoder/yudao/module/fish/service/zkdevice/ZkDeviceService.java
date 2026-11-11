package cn.iocoder.yudao.module.fish.service.zkdevice;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevice.ZkDeviceDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 设备 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkDeviceService {

    /**
     * 创建设备
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkDevice(@Valid ZkDeviceCreateReqVO createReqVO);

    /**
     * 更新设备
     *
     * @param updateReqVO 更新信息
     */
    void updateZkDevice(@Valid ZkDeviceUpdateReqVO updateReqVO);

    /**
     * 删除设备
     *
     * @param id 编号
     */
    void deleteZkDevice(Long id);

    /**
     * 获得设备
     *
     * @param id 编号
     * @return 设备
     */
    ZkDeviceDO getZkDevice(Long id);

    /**
     * 获得设备列表
     *
     * @param ids 编号
     * @return 设备列表
     */
    List<ZkDeviceDO> getZkDeviceList(Collection<Long> ids);

    /**
     * 获得设备分页
     *
     * @param pageReqVO 分页查询
     * @return 设备分页
     */
    PageResult<ZkDeviceDO> getZkDevicePage(ZkDevicePageReqVO pageReqVO);

    /**
     * 获得设备列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 设备列表
     */
    List<ZkDeviceDO> getZkDeviceList(ZkDeviceExportReqVO exportReqVO);

    String getDeviceInfo();

}
