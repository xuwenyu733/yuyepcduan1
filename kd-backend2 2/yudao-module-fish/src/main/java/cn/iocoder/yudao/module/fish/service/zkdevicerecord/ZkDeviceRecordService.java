package cn.iocoder.yudao.module.fish.service.zkdevicerecord;

import java.util.*;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkintelligentdiagnosis.ZkIntelligentDiagnosisDO;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevicerecord.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicerecord.ZkDeviceRecordDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 设备数据记录 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkDeviceRecordService {

    /**
     * 创建设备数据记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkDeviceRecord(@Valid ZkDeviceRecordCreateReqVO createReqVO);

    /**
     * 更新设备数据记录
     *
     * @param updateReqVO 更新信息
     */
    void updateZkDeviceRecord(@Valid ZkDeviceRecordUpdateReqVO updateReqVO);

    /**
     * 删除设备数据记录
     *
     * @param id 编号
     */
    void deleteZkDeviceRecord(Long id);

    /**
     * 获得设备数据记录
     *
     * @param id 编号
     * @return 设备数据记录
     */
    ZkDeviceRecordDO getZkDeviceRecord(Long id);

    /**
     * 获得设备数据记录列表
     *
     * @param ids 编号
     * @return 设备数据记录列表
     */
    List<ZkDeviceRecordDO> getZkDeviceRecordList(Collection<Long> ids);

    /**
     * 获得设备数据记录分页
     *
     * @param pageReqVO 分页查询
     * @return 设备数据记录分页
     */
    PageResult<ZkDeviceRecordDO> getZkDeviceRecordPage(ZkDeviceRecordPageReqVO pageReqVO);

    /**
     * 获得设备数据记录列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 设备数据记录列表
     */
    List<ZkDeviceRecordDO> getZkDeviceRecordList(ZkDeviceRecordExportReqVO exportReqVO);

    List<ZkDeviceRecordDO> selectNeedDel(int recentDays);

    void customeDelete(List<ZkDeviceRecordDO> recordDOS);

    List<ZkDeviceRecordDO> selectRecent();
    List<ZkDeviceRecordDO> selectRecentByQuery(Long pondId);

}
