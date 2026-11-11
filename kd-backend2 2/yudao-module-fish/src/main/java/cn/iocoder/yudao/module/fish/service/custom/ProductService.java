package cn.iocoder.yudao.module.fish.service.custom;

import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;

import java.util.List;

/**
 *
 */
public interface ProductService {
    void updateStoreByProductNo(String productNo, Long fishNum);

    List<ErpProductDO> getproductByNos(List<String> productNos);
}
