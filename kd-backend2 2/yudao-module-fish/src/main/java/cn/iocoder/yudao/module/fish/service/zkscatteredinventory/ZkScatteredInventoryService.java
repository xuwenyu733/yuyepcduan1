package cn.iocoder.yudao.module.fish.service.zkscatteredinventory;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkscatteredinventory.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkscatteredinventory.ZkScatteredInventoryDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 零星库存 Service 接口
 *
 * @author admin2
 */
public interface ZkScatteredInventoryService {

    /**
     * 创建零星库存
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkScatteredInventory(@Valid ZkScatteredInventorySaveReqVO createReqVO);

    /**
     * 更新零星库存
     *
     * @param updateReqVO 更新信息
     */
    void updateZkScatteredInventory(@Valid ZkScatteredInventorySaveReqVO updateReqVO);

    /**
     * 删除零星库存
     *
     * @param id 编号
     */
    void deleteZkScatteredInventory(Long id);

    /**
    * 批量删除零星库存
    *
    * @param ids 编号
    */
    void deleteZkScatteredInventoryListByIds(List<Long> ids);

    /**
     * 获得零星库存
     *
     * @param id 编号
     * @return 零星库存
     */
    ZkScatteredInventoryDO getZkScatteredInventory(Long id);

    /**
     * 获得零星库存分页
     *
     * @param pageReqVO 分页查询
     * @return 零星库存分页
     */
    PageResult<ZkScatteredInventoryDO> getZkScatteredInventoryPage(ZkScatteredInventoryPageReqVO pageReqVO);

    List<ZkScatteredInventoryDO> getByQuery(QueryWrapper<ZkScatteredInventoryDO> zkScatteredInventoryDOQueryWrapper);
}