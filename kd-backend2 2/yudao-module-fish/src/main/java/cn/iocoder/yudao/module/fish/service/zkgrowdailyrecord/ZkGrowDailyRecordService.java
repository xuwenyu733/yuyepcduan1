package cn.iocoder.yudao.module.fish.service.zkgrowdailyrecord;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowdailyrecord.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowDailyRecordDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 每日生长与环境记录 Service 接口
 *
 * @author admin2
 */
public interface ZkGrowDailyRecordService {

    /**
     * 创建每日生长与环境记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkGrowDailyRecord(@Valid ZkGrowDailyRecordSaveReqVO createReqVO);

    /**
     * 更新每日生长与环境记录
     *
     * @param updateReqVO 更新信息
     */
    void updateZkGrowDailyRecord(@Valid ZkGrowDailyRecordSaveReqVO updateReqVO);

    /**
     * 删除每日生长与环境记录
     *
     * @param id 编号
     */
    void deleteZkGrowDailyRecord(Long id);

    /**
    * 批量删除每日生长与环境记录
    *
    * @param ids 编号
    */
    void deleteZkGrowDailyRecordListByIds(List<Long> ids);

    /**
     * 获得每日生长与环境记录
     *
     * @param id 编号
     * @return 每日生长与环境记录
     */
    ZkGrowDailyRecordDO getZkGrowDailyRecord(Long id);

    /**
     * 获得每日生长与环境记录分页
     *
     * @param pageReqVO 分页查询
     * @return 每日生长与环境记录分页
     */
    PageResult<ZkGrowDailyRecordDO> getZkGrowDailyRecordPage(ZkGrowDailyRecordPageReqVO pageReqVO);

    /**
     * 获得养殖批次摘要列表
     *
     * @return 养殖批次摘要列表
     */
    List<ZkGrowBatchSummaryRespVO> getBatchSummaryList();

    /**
     * 获得养殖鱼池摘要列表
     *
     * @return 养殖鱼池摘要列表
     */
    List<ZkGrowPondSummaryRespVO> getPondSummaryList();

    List<cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowRealStatsDO> getRealStats(String batchNo, String pondNum, String pondDetailNum);

    List<cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowManualMeasureDO> getManualMeasurements(List<Long> pondIds);

}