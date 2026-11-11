package cn.iocoder.yudao.module.fish.service.zkbiologycapital;

import java.math.BigDecimal;
import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologycapital.ZkBiologyCapitalDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;

/**
 * 生物性资产 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkBiologyCapitalService {

    /**
     * 创建生物性资产
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkBiologyCapital(@Valid ZkBiologyCapitalCreateReqVO createReqVO);

    /**
     * 更新生物性资产
     *
     * @param updateReqVO 更新信息
     */
    void updateZkBiologyCapital(@Valid ZkBiologyCapitalUpdateReqVO updateReqVO);

    /**
     * 删除生物性资产
     *
     * @param id 编号
     */
    void deleteZkBiologyCapital(Long id);

    /**
     * 获得生物性资产
     *
     * @param id 编号
     * @return 生物性资产
     */
    ZkBiologyCapitalDO getZkBiologyCapital(Long id);

    /**
     * 获得生物性资产列表
     *
     * @param ids 编号
     * @return 生物性资产列表
     */
    List<ZkBiologyCapitalDO> getZkBiologyCapitalList(Collection<Long> ids);

    /**
     * 获得生物性资产分页
     *
     * @param pageReqVO 分页查询
     * @return 生物性资产分页
     */
    PageResult<ZkBiologyCapitalDO> getZkBiologyCapitalPage(ZkBiologyCapitalPageReqVO pageReqVO);

    /**
     * 获得生物性资产列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 生物性资产列表
     */
    List<ZkBiologyCapitalDO> getZkBiologyCapitalList(ZkBiologyCapitalExportReqVO exportReqVO);

    List<ZkBiologyCapitalDO> getList();

    Integer saveBatch(List<ZkBiologyCapitalDO> list);

    Integer updateBatch(List<ZkBiologyCapitalDO> list);

    ZkBiologyCapitalDO feedingCostUpdate(ZkFeedingLogDO zkFeedingLog);

    ZkBiologyCapitalDO getByPondId(Long pondId);

    void medicineCostUpdate(Long pondId, BigDecimal medicineCost);

    ZkBiologyCapitalMainHomeRespVO getMainHomeData();

    ZkBiologyCapitalDO sqlStatic();

    PrecisionFeedingStatsRespVO getPrecisionFeedingStats();

    List<PrecisionFeedingPondRespVO> getPrecisionFeedingPonds();

    List<PrecisionFeedingTodayLogRespVO> getPrecisionFeedingTodayLogs();

}
