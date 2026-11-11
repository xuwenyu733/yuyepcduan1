package cn.iocoder.yudao.module.fish.service.zkgrowfarmingbatch;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowfarmingbatch.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfarmingbatch.ZkGrowFarmingBatchDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 养殖批次 Service 接口
 *
 * @author admin2
 */
public interface ZkGrowFarmingBatchService {

    /**
     * 创建养殖批次
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkGrowFarmingBatch(@Valid ZkGrowFarmingBatchSaveReqVO createReqVO);

    /**
     * 更新养殖批次
     *
     * @param updateReqVO 更新信息
     */
    void updateZkGrowFarmingBatch(@Valid ZkGrowFarmingBatchSaveReqVO updateReqVO);

    /**
     * 删除养殖批次
     *
     * @param id 编号
     */
    void deleteZkGrowFarmingBatch(Long id);

    /**
    * 批量删除养殖批次
    *
    * @param ids 编号
    */
    void deleteZkGrowFarmingBatchListByIds(List<Long> ids);

    /**
     * 获得养殖批次
     *
     * @param id 编号
     * @return 养殖批次
     */
    ZkGrowFarmingBatchDO getZkGrowFarmingBatch(Long id);

    /**
     * 获得养殖批次分页
     *
     * @param pageReqVO 分页查询
     * @return 养殖批次分页
     */
    PageResult<ZkGrowFarmingBatchDO> getZkGrowFarmingBatchPage(ZkGrowFarmingBatchPageReqVO pageReqVO);

    List<ZkGrowFarmingBatchDO> getByIds(Set<Long> batchIds);
}