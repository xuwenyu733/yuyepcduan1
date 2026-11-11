package cn.iocoder.yudao.module.fish.service.zkdeviceproperties;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdeviceproperties.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeviceproperties.ZkDevicePropertiesDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 设备属性记录 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkDevicePropertiesService {

    /**
     * 创建设备属性记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkDeviceProperties(@Valid ZkDevicePropertiesCreateReqVO createReqVO);

    /**
     * 更新设备属性记录
     *
     * @param updateReqVO 更新信息
     */
    void updateZkDeviceProperties(@Valid ZkDevicePropertiesUpdateReqVO updateReqVO);

    /**
     * 删除设备属性记录
     *
     * @param id 编号
     */
    void deleteZkDeviceProperties(Long id);

    /**
     * 获得设备属性记录
     *
     * @param id 编号
     * @return 设备属性记录
     */
    ZkDevicePropertiesDO getZkDeviceProperties(Long id);

    /**
     * 获得设备属性记录列表
     *
     * @param ids 编号
     * @return 设备属性记录列表
     */
    List<ZkDevicePropertiesDO> getZkDevicePropertiesList(Collection<Long> ids);

    /**
     * 获得设备属性记录分页
     *
     * @param pageReqVO 分页查询
     * @return 设备属性记录分页
     */
    PageResult<ZkDevicePropertiesDO> getZkDevicePropertiesPage(ZkDevicePropertiesPageReqVO pageReqVO);

    /**
     * 获得设备属性记录列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 设备属性记录列表
     */
    List<ZkDevicePropertiesDO> getZkDevicePropertiesList(ZkDevicePropertiesExportReqVO exportReqVO);

    List<ZkDevicePropertiesDO> selectNeedDel(int recentDays);

    void customeDelete(List<ZkDevicePropertiesDO> propertiesDOS);
}
