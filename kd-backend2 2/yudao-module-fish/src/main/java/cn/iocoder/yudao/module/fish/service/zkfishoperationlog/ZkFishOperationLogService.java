package cn.iocoder.yudao.module.fish.service.zkfishoperationlog;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog.ZkFishOperationLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;

/**
 * 鱼苗统计 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkFishOperationLogService {

    /**
     * 创建鱼苗统计
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkFishOperationLog(@Valid ZkFishOperationLogCreateReqVO createReqVO);

    /**
     * 更新鱼苗统计
     *
     * @param updateReqVO 更新信息
     */
    void updateZkFishOperationLog(@Valid ZkFishOperationLogUpdateReqVO updateReqVO);

    /**
     * 删除鱼苗统计
     *
     * @param id 编号
     */
    void deleteZkFishOperationLog(Long id);

    /**
     * 获得鱼苗统计
     *
     * @param id 编号
     * @return 鱼苗统计
     */
    ZkFishOperationLogDO getZkFishOperationLog(Long id);

    /**
     * 获得鱼苗统计列表
     *
     * @param ids 编号
     * @return 鱼苗统计列表
     */
    List<ZkFishOperationLogDO> getZkFishOperationLogList(Collection<Long> ids);

    /**
     * 获得鱼苗统计分页
     *
     * @param pageReqVO 分页查询
     * @return 鱼苗统计分页
     */
    PageResult<ZkFishOperationLogDO> getZkFishOperationLogPage(ZkFishOperationLogPageReqVO pageReqVO);

    /**
     * 获得鱼苗统计列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 鱼苗统计列表
     */
    List<ZkFishOperationLogDO> getZkFishOperationLogList(ZkFishOperationLogExportReqVO exportReqVO);

    List<ZkFishOperationLogDO> getByLogDay(String dayStr);

    void customSaveBatch(List<ZkFishOperationLogDO> res);

    List<String> getBatchNumberList();

    List<ZkFishOperationLogDO> getByPondIds(Set<Long> pondIdSet);

    List<ZkFishOperationLogDO> getByBatchNumbers(Set<String> batchNumbers, String logType);

    /**
     * 获取最新的 统计记录信息，将其他指标信息放到该表里面来
     * @param logType
     * @return
     */
    List<ZkFishOperationLogDO> getByLogType(String logType);

    Integer customUpdateBatch(List<ZkFishOperationLogDO> needUpdate);

    ZkFishOperationLogDO sync_currFeedCount(ZkFeedingLogDO zkFeedingLog);

    /**
     * 统计 投放、分池的 7日或3日死亡量，这个除了可以通过 zk_fish_operation_log来实现，也可以通过 zk_pond_fish_detail 来实现
     * 这里目前用的就是 zk_fish_operation_log来实现
     * 已废弃
     * @param list
     * @param days
     * @return
     */
    List<ZkFishOperationLogDO> selectDaysMortalityRate(List<ZkPondFishDetailDO> list, Integer days);

    /**
     * 删除死亡记录，需要同步更新 zk_fish_operation_log 数值
     * @param zkPondFishDetailDO
     * @return
     */
    Map<String, List<ZkFishOperationLogDO>> handleFishDetailReverse(ZkPondFishDetailDO zkPondFishDetailDO);


    void fillInfo();
    void fillInfoByList(List<ZkFishOperationLogDO> list);


}
