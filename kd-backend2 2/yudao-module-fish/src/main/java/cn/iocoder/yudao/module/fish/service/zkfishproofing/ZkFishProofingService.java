package cn.iocoder.yudao.module.fish.service.zkfishproofing;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishproofing.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishproofing.ZkFishProofingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 打样记录 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkFishProofingService {

    /**
     * 创建打样记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkFishProofing(@Valid ZkFishProofingCreateReqVO createReqVO);

    /**
     * 更新打样记录
     *
     * @param updateReqVO 更新信息
     */
    void updateZkFishProofing(@Valid ZkFishProofingUpdateReqVO updateReqVO);

    /**
     * 删除打样记录
     *
     * @param id 编号
     */
    void deleteZkFishProofing(Long id);

    /**
     * 获得打样记录
     *
     * @param id 编号
     * @return 打样记录
     */
    ZkFishProofingDO getZkFishProofing(Long id);

    /**
     * 获得打样记录列表
     *
     * @param ids 编号
     * @return 打样记录列表
     */
    List<ZkFishProofingDO> getZkFishProofingList(Collection<Long> ids);

    /**
     * 获得打样记录分页
     *
     * @param pageReqVO 分页查询
     * @return 打样记录分页
     */
    PageResult<ZkFishProofingDO> getZkFishProofingPage(ZkFishProofingPageReqVO pageReqVO);

    /**
     * 获得打样记录列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 打样记录列表
     */
    List<ZkFishProofingDO> getZkFishProofingList(ZkFishProofingExportReqVO exportReqVO);

    List<ZkFishProofingDO> getByTaskDetailIds(Set<Long> taskDetailIds);

    void customSaveBatch(List<ZkFishProofingDO> newProofingList);

    void customUpdateBatch(List<ZkFishProofingDO> needUpdate);

    List<ZkFishProofingDO> getCustomList(String statisticsType);

    ZkFishProofingDO getLastProofing(Long pondId);

    /**
     * 获取 打样 列表
     * @param pondIdSet
     * @return
     */
    List<ZkFishProofingDO> getLastByPondIdSet(Set<Long> pondIdSet);
    default Map<Long, List<ZkFishProofingDO>> getLastMapByPondIdSet(Set<Long> pondIdSet){
        List<ZkFishProofingDO> lastByPondIdSet = getLastByPondIdSet(pondIdSet);
        if(EasyListUtil.isEmpty(lastByPondIdSet)) return new HashMap<>();
        return lastByPondIdSet.stream().collect(Collectors.groupingBy(ZkFishProofingDO::getPondId));
    }

    void onlyFillObjByList(List<ZkFishProofingDO> list);
}
