package cn.iocoder.yudao.module.fish.service.zkpondfish;

import java.util.*;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.erp.controller.admin.product.vo.product.ErpProductRespVO;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfish.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;


/**
 * 存塘鱼苗 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkPondFishService {

    /**
     * 创建存塘鱼苗
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkPondFish(@Valid ZkPondFishCreateReqVO createReqVO);

    /**
     * 更新存塘鱼苗
     *
     * @param updateReqVO 更新信息
     */
    void updateZkPondFish(@Valid ZkPondFishUpdateReqVO updateReqVO);

    /**
     * 删除存塘鱼苗
     *
     * @param id 编号
     */
    void deleteZkPondFish(Long id);

    /**
     * 获得存塘鱼苗
     *
     * @param id 编号
     * @return 存塘鱼苗
     */
    ZkPondFishDO getZkPondFish(Long id);

    /**
     * 获得存塘鱼苗列表
     *
     * @param ids 编号
     * @return 存塘鱼苗列表
     */
    List<ZkPondFishDO> getZkPondFishList(Collection<Long> ids);

    /**
     * 获得存塘鱼苗分页
     *
     * @param pageReqVO 分页查询
     * @return 存塘鱼苗分页
     */
    PageResult<ZkPondFishDO> getZkPondFishPage(ZkPondFishPageReqVO pageReqVO);

    /**
     * 获得存塘鱼苗列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 存塘鱼苗列表
     */
    List<ZkPondFishDO> getZkPondFishList(ZkPondFishExportReqVO exportReqVO);

    ZkPondFishDO getByPondId(Long pondId);

    void customInsert(ZkPondFishDO zkPondFishDO);
    void customBatchInsert(List<ZkPondFishDO> zkPondFishDOList);

    void customUpdate(ZkPondFishDO zkPondFishDO);

    List<ZkPondFishDO> getListByPondIds(Set<Long> pondIds);

    ZkPondFishDO getByBatchNumber(String batchNumber);

    List<ZkPondFishDO> getList();

    /**
     * 这里面是 从 zk_biology_capital ==>
     * @return
     */
    List<ZkPondFishDO> getListWithSpec();

    /**
     * 修正鱼塘鱼数
     * @param differenceData
     */
    void updatePush(ZkPondFishDetailDO zkPondFishDetailDO, Long differenceData);

    void updateDead(ZkPondFishDetailDO zkPondFishDetailDO, Long differenceData);

    void updateSplit(ZkPondFishDetailDO zkPondFishDetailDO, Long differenceData);


    List<ZkPondFishDO> selectByPondIdSet(Set<Long> pondIdSet);

    Integer customBatchUpdate(List<ZkPondFishDO> needUpdate);

    List<ZkPondDO> getByEmptyPondFish();

    /**
     * 根据 pondId获取zk_pond_fish,并封装成map
     * @param pondSet
     * @return
     */
    default Map<Long, ZkPondFishDO> selectMapByPondIdSet(Set<Long> pondSet) {
        return CollectionUtils.convertMap(selectByPondIdSet(pondSet), ZkPondFishDO::getPondId);
    }
}
