package cn.iocoder.yudao.module.fish.service.zkdevicealarm;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevicealarm.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicealarm.ZkDeviceAlarmDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 设备报警 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkDeviceAlarmService {

    /**
     * 创建设备报警
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkDeviceAlarm(@Valid ZkDeviceAlarmCreateReqVO createReqVO);

    /**
     * 更新设备报警
     *
     * @param updateReqVO 更新信息
     */
    void updateZkDeviceAlarm(@Valid ZkDeviceAlarmUpdateReqVO updateReqVO);

//    ZkDeviceAlarmDO updateZkDeviceAlarmHandle(@Valid ZkDeviceAlarmUpdateReqVO updateReqVO);

    /**
     * 删除设备报警
     *
     * @param id 编号
     */
    void deleteZkDeviceAlarm(Long id);

    /**
     * 获得设备报警
     *
     * @param id 编号
     * @return 设备报警
     */
    ZkDeviceAlarmDO getZkDeviceAlarm(Long id);

    /**
     * 获得设备报警列表
     *
     * @param ids 编号
     * @return 设备报警列表
     */
    List<ZkDeviceAlarmDO> getZkDeviceAlarmList(Collection<Long> ids);

    /**
     * 获得设备报警分页
     *
     * @param pageReqVO 分页查询
     * @return 设备报警分页
     */
    PageResult<ZkDeviceAlarmDO> getZkDeviceAlarmPage(ZkDeviceAlarmPageReqVO pageReqVO);

    /**
     * 获得设备报警列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 设备报警列表
     */
    List<ZkDeviceAlarmDO> getZkDeviceAlarmList(ZkDeviceAlarmExportReqVO exportReqVO);

    /**
     * 获得设备报警列表，用于首页大屏
     *
     * @param deptId 归属部门（可选，不传则取登录用户部门）
     * @return 设备报警列表
     */
    List<ZkDeviceAlarmDashboardRespVO> getZkDeviceAlarmDashboardList(Long deptId);

}
