package cn.iocoder.yudao.module.fish.service.custom;

import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.mysql.product.ErpProductMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService{
    @Resource
    private ErpProductMapper erpProductMapper;

    @Override
    public void updateStoreByProductNo(String productNo, Long fishNum) {
        // TODO 框架升级待完成 完善erp模块
    }

    @Override
    public List<ErpProductDO> getproductByNos(List<String> productNos) {
        if(CollectionUtils.isEmpty(productNos))
            return new ArrayList<>();
        QueryWrapper<ErpProductDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("product_no", productNos);
        return erpProductMapper.selectList(queryWrapper);
    }
}
