package cn.iocoder.yudao.module.fish.service.custom;

import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockOutItemDO;

import java.util.List;

/**
 *  TODO 框架升级待完成
 */
public interface OutOrderService {
    List<ErpPurchaseInItemDO> getNeedSyncToCost();
    void needSyncToCost_enhancer(List<ErpPurchaseInItemDO> needSyncToCost);
}
