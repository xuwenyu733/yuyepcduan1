package cn.iocoder.yudao.module.erp.service.stock;

import cn.iocoder.yudao.framework.common.exception.CommonException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.record.ErpStockRecordPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockRecordDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpWarehouseDO;
import cn.iocoder.yudao.module.erp.dal.mysql.stock.ErpStockRecordMapper;
import cn.iocoder.yudao.module.erp.service.stock.bo.ErpStockRecordCreateReqBO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ERP 产品库存明细 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ErpStockRecordServiceImpl implements ErpStockRecordService {

    @Resource
    private ErpStockRecordMapper stockRecordMapper;

    @Resource
    private ErpStockService stockService;

    @Override
    public ErpStockRecordDO getStockRecord(Long id) {
        return stockRecordMapper.selectById(id);
    }

    @Override
    public PageResult<ErpStockRecordDO> getStockRecordPage(ErpStockRecordPageReqVO pageReqVO) {
        return stockRecordMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ErpStockRecordDO createStockRecord(ErpStockRecordCreateReqBO createReqBO) {
        long[] aligned = stockService.resolveProductAndWarehouse(createReqBO.getProductId(), createReqBO.getWarehouseId());
        createReqBO.setProductId(aligned[0]);
        createReqBO.setWarehouseId(aligned[1]);
        // 1. 更新库存
        BigDecimal totalCount = stockService.updateStockCountIncrement(
                createReqBO.getProductId(), createReqBO.getWarehouseId(), createReqBO.getCount());
        // 2. 创建库存明细
        ErpStockRecordDO stockRecord = BeanUtils.toBean(createReqBO, ErpStockRecordDO.class)
                .setTotalCount(totalCount);
        stockRecordMapper.insert(stockRecord);
        return stockRecord;
    }

    @Override
    public void updateStockRecord(ErpStockRecordCreateReqBO createReqBO) {
        long[] aligned = stockService.resolveProductAndWarehouse(createReqBO.getProductId(), createReqBO.getWarehouseId());
        createReqBO.setProductId(aligned[0]);
        createReqBO.setWarehouseId(aligned[1]);
        // 0. 获取历史
        ErpStockDO stock = stockService.getStock(createReqBO.getId());
        if(stock == null) throw new CommonException(403, "错误id："+createReqBO.getId());
        BigDecimal needAdd = EasyBigDecimalUtil.subtract(createReqBO.getCount(), stock.getCount());
        // 1. 更新库存 ==> 数量需要参照之前，多退少补
        BigDecimal totalCount = stockService.updateStockCountIncrement(
                createReqBO.getProductId(), createReqBO.getWarehouseId(), needAdd);
        // 2. 更新库存记录
        ErpStockRecordDO stockRecord = BeanUtils.toBean(createReqBO, ErpStockRecordDO.class)
                .setTotalCount(totalCount);
        stockRecordMapper.updateById(stockRecord);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        // 1. 获取历史
        if(EasyListUtil.isEmpty(ids)) return;
        List<ErpStockRecordDO> recordDOS = stockRecordMapper.selectByIds(ids);
        if(EasyListUtil.isEmpty(recordDOS)) throw new CommonException(403, "错误id："+ JSON.toJSONString(ids));
        // 2. 执行补偿逻辑 erp_stock 减去 现在 count就好了
        for (ErpStockRecordDO item : recordDOS) {
            // 2.1 获取 仓库
            stockService.updateStockCountIncrement(
                    item.getProductId(), item.getWarehouseId(), EasyBigDecimalUtil.subtract(new BigDecimal(0), item.getCount()));
        }
        // 3. 执行删除逻辑
        stockRecordMapper.deleteByIds(ids);
    }

    @Override
    public void removeByBizTypeAndBusId(int bizType, Long busId) {
        LambdaQueryWrapper<ErpStockRecordDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ErpStockRecordDO::getBusId, busId).eq(ErpStockRecordDO::getBizType, bizType);
        List<ErpStockRecordDO> recordDOS = stockRecordMapper.selectList(queryWrapper);
        if(EasyListUtil.isEmpty(recordDOS)) return;
        List<Long> collect = recordDOS.stream().map(ErpStockRecordDO::getId).collect(Collectors.toList());
        deleteByIds(collect);
    }

}