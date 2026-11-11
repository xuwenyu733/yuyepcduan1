package cn.iocoder.yudao.module.fish.service.zkfeedinglog;

import java.math.BigDecimal;
import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 投喂记录 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkFeedingLogService {

    /**
     * 创建投喂记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    ZkFeedingLogDO createZkFeedingLog(@Valid ZkFeedingLogCreateReqVO createReqVO);

    /**
     * 更新投喂记录
     *
     * @param updateReqVO 更新信息
     */
    List<ZkFeedingLogDO> updateZkFeedingLog(@Valid ZkFeedingLogUpdateReqVO updateReqVO);

    /**
     * 删除投喂记录
     *
     * @param id 编号
     */
    ZkFeedingLogDO deleteZkFeedingLog(Long id);

    /**
     * 获得投喂记录
     *
     * @param id 编号
     * @return 投喂记录
     */
    ZkFeedingLogDO getZkFeedingLog(Long id);

    /**
     * 获得投喂记录列表
     *
     * @param ids 编号
     * @return 投喂记录列表
     */
    List<ZkFeedingLogDO> getZkFeedingLogList(Collection<Long> ids);

    /**
     * 获得投喂记录分页
     *
     * @param pageReqVO 分页查询
     * @return 投喂记录分页
     */
    PageResult<ZkFeedingLogDO> getZkFeedingLogPage(ZkFeedingLogPageReqVO pageReqVO);

    /**
     * 获得投喂记录列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 投喂记录列表
     */
    List<ZkFeedingLogDO> getZkFeedingLogList(ZkFeedingLogExportReqVO exportReqVO);

    BigDecimal getAvg(Long pondId);

    /**
     * 获取最近三天的摄食量
     * @param pondId
     * @param days
     * @return
     */
    List<ZkFeedingLogDO> getByPondId(Long pondId, Integer days);

    List<ZkFeedingLogDO> customUpdate(ZkFeedingLogDO feedingLog);

    /**
     * 更新 投喂记录，将 curr和before对比，然后更新 ，方便后续指标统计
     * zk_biology_capital 总共的、zk_fish_operation_log 每天的
     * @param curr
     * @param before
     */
    void sync_biologyCapital(ZkFeedingLogDO curr, ZkFeedingLogDO before);

    List<ZkFeedingLogDO> getByTaskDetailIds(Set<Long> taskDetailIds);

    Integer customSaveBatch(List<ZkFeedingLogDO> needAdd);

    void customUpdateBatch(List<ZkFeedingLogDO> finalNeedUpdate);

    /**
     * 统计 饲料投喂 信息
     * @return pond_id 、 curr_feed_count 、 accumulate_feed_count
     */
    List<Map<String, Object>> staticFeedInfo();

    /**
     * 获得各鱼苗期成本
     */
    List<VariousFryStagesCostRespVO> getVariousFryStagesCost();

    ZkFeedingLogDO getById(Long id);
}
