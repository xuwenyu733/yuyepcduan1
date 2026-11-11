package cn.iocoder.yudao.module.fish.service.zkpurifydailymonitoring;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifydailymonitoring.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifydailymonitoring.PurifyDailyMonitoringDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 日常监测记录 Service 接口
 *
 * @author admin2
 */
public interface PurifyDailyMonitoringService {

    /**
     * 创建日常监测记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurifyDailyMonitoring(@Valid PurifyDailyMonitoringSaveReqVO createReqVO);

    /**
     * 更新日常监测记录
     *
     * @param updateReqVO 更新信息
     */
    void updatePurifyDailyMonitoring(@Valid PurifyDailyMonitoringSaveReqVO updateReqVO);

    /**
     * 删除日常监测记录
     *
     * @param id 编号
     */
    void deletePurifyDailyMonitoring(Long id);

    /**
    * 批量删除日常监测记录
    *
    * @param ids 编号
    */
    void deletePurifyDailyMonitoringListByIds(List<Long> ids);

    /**
     * 获得日常监测记录
     *
     * @param id 编号
     * @return 日常监测记录
     */
    PurifyDailyMonitoringDO getPurifyDailyMonitoring(Long id);

    /**
     * 获得日常监测记录分页
     *
     * @param pageReqVO 分页查询
     * @return 日常监测记录分页
     */
    PageResult<PurifyDailyMonitoringDO> getPurifyDailyMonitoringPage(PurifyDailyMonitoringPageReqVO pageReqVO);

}