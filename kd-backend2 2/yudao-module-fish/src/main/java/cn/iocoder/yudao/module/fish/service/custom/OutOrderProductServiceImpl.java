package cn.iocoder.yudao.module.fish.service.custom;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockOutItemDO;
import cn.iocoder.yudao.module.erp.dal.mysql.purchase.ErpPurchaseInItemMapper;
import cn.iocoder.yudao.module.erp.service.purchase.ErpPurchaseInService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class OutOrderProductServiceImpl implements OutOrderProductService{

    @Resource
    private ErpPurchaseInService purchaseInService;
    @Resource
    private ErpPurchaseInItemMapper erpPurchaseInItemMapper;
    @Resource
    private OutOrderService outOrderService;

    @Override
    public void update_remainingInventory(String relationOrderNo, Integer remainingInventory, Long fishNum) {
        UpdateWrapper<ErpPurchaseInItemDO> updateWrapper = new UpdateWrapper<>();
        if(fishNum < 0 || remainingInventory == null) {
            // 为删除 冲红数据
            updateWrapper.setSql("remaining_inventory = remaining_inventory - " + fishNum).eq("relation_order_no", relationOrderNo);
        } else {
            updateWrapper.set("remaining_inventory", remainingInventory).eq("relation_order_no", relationOrderNo);
        }
        erpPurchaseInItemMapper.update(null, updateWrapper);
    }

    @Override
    public List<ErpPurchaseInItemDO> getListByRelationOrderNoSet(Set<String> batchNumberSet) {
        QueryWrapper<ErpPurchaseInItemDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("relation_order_no", batchNumberSet);
        List<ErpPurchaseInItemDO> erpPurchaseInItemDOS = erpPurchaseInItemMapper.selectList(queryWrapper);
        outOrderService.needSyncToCost_enhancer(erpPurchaseInItemDOS);
        return erpPurchaseInItemDOS;
    }

    @Override
    public void batchUpdate(List<ErpPurchaseInItemDO> needSyncToCost) {
        if(EasyListUtil.isEmpty(needSyncToCost)) return;
        erpPurchaseInItemMapper.updateBatch(needSyncToCost);
    }
}
