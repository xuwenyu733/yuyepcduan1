package cn.iocoder.yudao.module.fish.service.zkpondfishdetail;

import java.util.*;

import cn.iocoder.yudao.module.fish.dal.mysql.zkfishoperationlog.StaticDeadInfoVo3;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog.ZkFishOperationLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
/**
 * 存塘鱼苗明细 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkPondFishDetailService {

    /**
     * 创建存塘鱼苗明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    ZkPondFishDetailDO createZkPondFishDetail(@Valid ZkPondFishDetailCreateReqVO createReqVO);

    /**
     * 更新存塘鱼苗明细
     *
     * @param updateReqVO 更新信息
     */
    void updateZkPondFishDetail(@Valid ZkPondFishDetailUpdateReqVO updateReqVO);

    /**
     * 删除存塘鱼苗明细
     *
     * @param id 编号
     */
    ZkPondFishDetailDO deleteZkPondFishDetail(Long id);

    /**
     * 获得存塘鱼苗明细
     *
     * @param id 编号
     * @return 存塘鱼苗明细
     */
    ZkPondFishDetailDO getZkPondFishDetail(Long id);

    /**
     * 获得存塘鱼苗明细列表
     *
     * @param ids 编号
     * @return 存塘鱼苗明细列表
     */
    List<ZkPondFishDetailDO> getZkPondFishDetailList(Collection<Long> ids);

    /**
     * 获得存塘鱼苗明细分页
     *
     * @param pageReqVO 分页查询
     * @return 存塘鱼苗明细分页
     */
    PageResult<ZkPondFishDetailDO> getZkPondFishDetailPage(ZkPondFishDetailPageReqVO pageReqVO);

    /**
     * 获得存塘鱼苗明细列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 存塘鱼苗明细列表
     */
    List<ZkPondFishDetailDO> getZkPondFishDetailList(ZkPondFishDetailExportReqVO exportReqVO);

    List<ZkPondFishDetailDO> list(com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ZkPondFishDetailDO> queryWrapper);

    void customUpdate(ZkPondFishDetailDO build_zkPondFishId);

    List<ZkPondFishDetailDO> getByPondId(Long pondId);
    List<ZkPondFishDetailDO> getByPondId(Long pondId, String addType);

    List<ZkPondFishDetailDO> getListByPondIdsAndStatisticsType(Set<Long> pondIds, String addType, String statisticsType);
    List<ZkPondFishDetailDO> getListByPondIds(Set<Long> pondIds, String addType);
    List<ZkPondFishDetailDO> byPondIdsExcludeTargetPondIds(Set<Long> pondIds, String addType, Set<Long> excludePondIds);

    List<ZkPondFishDetailDO> getRecentDayList(String addType, String dayStr);
    List<ZkPondFishDetailDO> getRecentDayList(String addType);

    /**
     * 部分存塘没有死亡记录，但是其他的指标值需要往该表里面录入，则此时需要生成一个空的死亡日志
     * @return
     */
    List<ZkFishOperationLogDO> genByEmptyOperationLog();

    List<ZkPondFishDetailDO> countByWorkshopIdSet(String addType, Set<Long> workshopIdSet);

    List<ZkPondFishDetailDO> countByPondIdSet(String addType, Set<Long> pondIdSet);
    List<ZkPondFishDetailDO> countByTargetPondIdSet(String addType, Set<Long> targetPondIdSet);

    List<ZkPondFishDetailDO> countByPondNumSet(String addType, Set<String> pondNumSet);

    ZkPondFishDetailDO getById(Long id);
    ZkPondFishDetailDO updateSplitRadio(ZkPondFishDetailDO zkPondFishDetailDO);

    Integer customBatchUpdate(List<ZkPondFishDetailDO> list);

    /**
     * 通过 创建时间区间set 来获取对应类型的记录
     * @param createStrSet
     * @param addType
     * @return
     */
    List<ZkPondFishDetailDO> selectByCreateStrSet(Set<String> createStrSet, String addType);

    /**
     * 统计 投放、分池的 7日或3日死亡量，这个除了可以通过 zk_fish_operation_log来实现，也可以通过 zk_pond_fish_detail 来实现
     * 这里目前用的就是 zk_pond_fish_detail 来实现
     * @param list
     * @param days
     * @return
     */
    List<ZkPondFishDetailDO> selectDaysMortalityRate(List<ZkPondFishDetailDO> list, Integer days, String addType);
    default Map<Long, ZkPondFishDetailDO> selectMapByDaysMortalityRate(List<ZkPondFishDetailDO> list, Integer days, String addType){
        return CollectionUtils.convertMap(selectDaysMortalityRate(list, days, addType), ZkPondFishDetailDO::getId);
    }

    Map<Long, List<ZkPondFishDetailDO>> selectMapByTypeAndTargetPondId(String addType, Set<Long> pondIdSet);

    List<ZkPondFishDetailDO> getByTaskDetailIds(Set<Long> taskDetailIds);


    /**
     * 统计 死亡 信息
     * @return pond_id 、 day_fish_num 、 accumulate_fish_num 、 pond_detail_num
     */
    List<Map<String, Object>> staticDeadInfo();
    List<StaticDeadInfoVo3> staticDeadInfo3();

    List<ZkPondFishDetailDO> getZkPondFishDetailListByParentId(Long parentId);
}
