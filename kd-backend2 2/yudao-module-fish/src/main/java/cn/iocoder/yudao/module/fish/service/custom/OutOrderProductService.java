package cn.iocoder.yudao.module.fish.service.custom;

import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockOutItemDO;

import java.util.List;
import java.util.Set;

/**
 *  TODO 框架升级待完成
 */
public interface OutOrderProductService {
    void update_remainingInventory(String batchNumber, Integer remainingInventory, Long fishNum);

    List<ErpPurchaseInItemDO> getListByRelationOrderNoSet(Set<String> batchNumberSet);

    void batchUpdate(List<ErpPurchaseInItemDO> needSyncToCost);
}
