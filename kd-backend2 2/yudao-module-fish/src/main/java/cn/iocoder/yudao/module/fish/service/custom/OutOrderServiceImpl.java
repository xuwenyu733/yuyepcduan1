package cn.iocoder.yudao.module.fish.service.custom;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockOutItemDO;
import cn.iocoder.yudao.module.erp.dal.mysql.purchase.ErpPurchaseInItemMapper;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.purchase.ErpPurchaseInService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OutOrderServiceImpl implements OutOrderService{

    @Resource
    private ErpPurchaseInService purchaseInService;
    @Resource
    private ErpPurchaseInItemMapper erpPurchaseInItemMapper;
    @Resource
    private ErpProductService erpProductService;

    @Override
    public List<ErpPurchaseInItemDO> getNeedSyncToCost() {
        // 1. 查询需要获取同步的数据
        QueryWrapper<ErpPurchaseInItemDO> queryWrapper = new QueryWrapper<>();
        // queryWrapper.eq("poo.o_type", "1"); // 订单类型(0:出库单;1:入库单;)
        queryWrapper.eq("poop.sync_to_cost_status", "0"); // 数据同步成本状态(0:待同步;1:已同步;)
        List<ErpPurchaseInItemDO> needSyncToCost = erpPurchaseInItemMapper.getNeedSyncToCost(queryWrapper);
        // 2. 查询对应商品的 pl_product.category_id == pl_product_category.category_no
        needSyncToCost_enhancer(needSyncToCost);
        return needSyncToCost;
    }

    public void needSyncToCost_enhancer(List<ErpPurchaseInItemDO> needSyncToCost) {
        // 1. 获取商品数据
        if(EasyListUtil.isEmpty(needSyncToCost)) return;
        Set<String> productNos = needSyncToCost.stream().map(obj -> obj.getProductNo()).collect(Collectors.toSet());
        List<ErpProductDO> productDOS = erpProductService.getproductByNos(new ArrayList<>(productNos));
        if(EasyListUtil.isEmpty(productDOS)) return;
        // 2. 执行数据同步
        for(ErpPurchaseInItemDO orderItem : needSyncToCost) {
            for(ErpProductDO product : productDOS) {
                if(StringUtils.equals(product.getProductNo(), orderItem.getProductNo())) {
                    orderItem.setCategoryId(product.getCategoryId());
                    orderItem.setErpProductDO(product);
                    break;
                }
            }
        }
    }
}
