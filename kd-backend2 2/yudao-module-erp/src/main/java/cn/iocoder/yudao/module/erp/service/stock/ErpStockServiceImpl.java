package cn.iocoder.yudao.module.erp.service.stock;

import cn.iocoder.yudao.framework.common.exception.CommonException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.stock.ErpStockPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockDO;
import cn.iocoder.yudao.module.erp.dal.mysql.stock.ErpStockMapper;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.STOCK_COUNT_NEGATIVE;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.STOCK_COUNT_NEGATIVE2;

/**
 * ERP 产品库存 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ErpStockServiceImpl implements ErpStockService {

    /**
     * 允许库存为负数
     *
     * TODO 芋艿：后续做成 db 配置
     */
    private static final Boolean NEGATIVE_STOCK_COUNT_ENABLE = false;

    @Resource
    private ErpProductService productService;
    @Resource
    private ErpWarehouseService warehouseService;

    @Resource
    private ErpStockMapper stockMapper;

    @Override
    public ErpStockDO getStock(Long id) {
        return stockMapper.selectById(id);
    }

    @Override
    public ErpStockDO getStock(Long productId, Long warehouseId) {
        return stockMapper.selectByProductIdAndWarehouseId(productId, warehouseId);
    }

    @Override
    public BigDecimal getStockCount(Long productId) {
        BigDecimal count = stockMapper.selectSumByProductId(productId);
        return count != null ? count : BigDecimal.ZERO;
    }

    @Override
    public PageResult<ErpStockDO> getStockPage(ErpStockPageReqVO pageReqVO) {
        return stockMapper.selectPage(pageReqVO);
    }

    @Override
    public BigDecimal updateStockCountIncrement(Long productId, Long warehouseId, BigDecimal count) {
        long[] aligned = resolveProductAndWarehouse(productId, warehouseId);
        productId = aligned[0];
        warehouseId = aligned[1];
        // 1.1 查询当前库存
        ErpStockDO stock = stockMapper.selectByProductIdAndWarehouseId(productId, warehouseId);
        if (stock == null) {
            ErpProductDO product = productService.getById(productId);
            if(product == null) throw new CommonException(403, String.format("商品id错误:%s", productId));
            stock = new ErpStockDO().setProductId(productId).setWarehouseId(warehouseId).setCount(BigDecimal.ZERO)
                    .setDeptId(product.getDeptId()).setUserId(product.getUserId());
            stockMapper.insert(stock);
        }
        // 1.2 校验库存是否充足
        if (!NEGATIVE_STOCK_COUNT_ENABLE && stock.getCount().add(count).compareTo(BigDecimal.ZERO) < 0) {
            throw exception(STOCK_COUNT_NEGATIVE, productService.getProduct(productId).getName(),
                    warehouseService.getWarehouse(warehouseId).getName(), stock.getCount(), count);
        }

        // 2. 库存变更
        int updateCount = stockMapper.updateCountIncrement(stock.getId(), count, NEGATIVE_STOCK_COUNT_ENABLE);
        // if (updateCount == 0) {
            // 此时不好去查询最新库存，所以直接抛出该提示，不提供具体库存数字
        //     throw exception(STOCK_COUNT_NEGATIVE2, productService.getProduct(productId).getName(),
        //             warehouseService.getWarehouse(warehouseId).getName());
        // }

        // 3. 返回最新库存
        return stock.getCount().add(count);
    }

    /**
     * 库存写入前校正产品/仓库编号。
     * 产品列必须是 erp_product.id，仓库列必须是 erp_warehouse.id。
     * 若两者写反（产品列是仓库号、仓库列是产品号）则对调；产品号在产品表不存在则拒绝写入。
     */
    @Override
    public long[] resolveProductAndWarehouse(Long productId, Long warehouseId) {
        if (productId == null || warehouseId == null) {
            throw new CommonException(403, "产品编号和仓库编号不能为空");
        }
        ErpProductDO product = productService.getById(productId);
        boolean warehouseOk = warehouseService.getWarehouse(warehouseId) != null;
        if (product != null && warehouseOk) {
            return new long[]{productId, warehouseId};
        }
        ErpProductDO swappedProduct = productService.getById(warehouseId);
        boolean swappedWarehouseOk = warehouseService.getWarehouse(productId) != null;
        if (product == null && swappedProduct != null && swappedWarehouseOk) {
            return new long[]{warehouseId, productId};
        }
        if (product == null) {
            throw new CommonException(403, String.format(
                    "产品编号不存在：%s。请从产品信息选择真实产品，不能把仓库编号写入产品列", productId));
        }
        throw new CommonException(403, String.format("仓库编号不存在：%s", warehouseId));
    }

    @Override
    public List<ErpStockDO> getByIds(Set<Long> idSet) {
        if(CollectionUtils.isAnyEmpty(idSet)) return new ArrayList<>();
        QueryWrapper<ErpStockDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("product_id", idSet);
        List<ErpStockDO> erpStockDOS = stockMapper.selectList(queryWrapper);
        return erpStockDOS;
    }

}