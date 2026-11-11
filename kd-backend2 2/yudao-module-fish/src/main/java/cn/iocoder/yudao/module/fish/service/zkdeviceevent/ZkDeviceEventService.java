package cn.iocoder.yudao.module.fish.service.zkdeviceevent;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdeviceevent.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeviceevent.ZkDeviceEventDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 设备事件记录 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkDeviceEventService {

    /**
     * 创建设备事件记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkDeviceEvent(@Valid ZkDeviceEventCreateReqVO createReqVO);

    /**
     * 更新设备事件记录
     *
     * @param updateReqVO 更新信息
     */
    void updateZkDeviceEvent(@Valid ZkDeviceEventUpdateReqVO updateReqVO);

    /**
     * 删除设备事件记录
     *
     * @param id 编号
     */
    void deleteZkDeviceEvent(Long id);

    /**
     * 获得设备事件记录
     *
     * @param id 编号
     * @return 设备事件记录
     */
    ZkDeviceEventDO getZkDeviceEvent(Long id);

    /**
     * 获得设备事件记录列表
     *
     * @param ids 编号
     * @return 设备事件记录列表
     */
    List<ZkDeviceEventDO> getZkDeviceEventList(Collection<Long> ids);

    /**
     * 获得设备事件记录分页
     *
     * @param pageReqVO 分页查询
     * @return 设备事件记录分页
     */
    PageResult<ZkDeviceEventDO> getZkDeviceEventPage(ZkDeviceEventPageReqVO pageReqVO);

    /**
     * 获得设备事件记录列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 设备事件记录列表
     */
    List<ZkDeviceEventDO> getZkDeviceEventList(ZkDeviceEventExportReqVO exportReqVO);

}
