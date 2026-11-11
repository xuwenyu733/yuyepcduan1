package cn.iocoder.yudao.module.fish.service.zkproductcost;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockOutItemDO;
import cn.iocoder.yudao.module.fish.service.custom.OutOrderProductService;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkproductcost.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkproductcost.ZkProductCostDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
// import cn.iocoder.yudao.module.fish.convert.zkproductcost.ZkProductCostConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkproductcost.ZkProductCostMapper;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 成本 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkProductCostServiceImpl implements ZkProductCostService {

    @Resource
    private ZkProductCostMapper zkProductCostMapper;

    @Resource
    private OutOrderProductService outOrderProductService;

    @Override
    public Long createZkProductCost(ZkProductCostCreateReqVO createReqVO) {
        // 插入
        ZkProductCostDO zkProductCost = BeanUtils.toBean(createReqVO, ZkProductCostDO.class);
        zkProductCostMapper.insert(zkProductCost);
        // 返回
        return zkProductCost.getId();
    }

    @Override
    public void updateZkProductCost(ZkProductCostUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkProductCostExists(updateReqVO.getId());
        // 更新
        ZkProductCostDO updateObj = BeanUtils.toBean(updateReqVO, ZkProductCostDO.class);
        zkProductCostMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkProductCost(Long id) {
        // 校验存在
        validateZkProductCostExists(id);
        // 删除
        zkProductCostMapper.deleteById(id);
    }

    private void validateZkProductCostExists(Long id) {
        if (zkProductCostMapper.selectById(id) == null) {
            throw exception(ZK_PRODUCT_COST_NOT_EXISTS);
        }
    }

    @Override
    public ZkProductCostDO getZkProductCost(Long id) {
        return zkProductCostMapper.selectById(id);
    }

    @Override
    public List<ZkProductCostDO> getZkProductCostList(Collection<Long> ids) {
        return zkProductCostMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkProductCostDO> getZkProductCostPage(ZkProductCostPageReqVO pageReqVO) {
        return zkProductCostMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkProductCostDO> getZkProductCostList(ZkProductCostExportReqVO exportReqVO) {
        return zkProductCostMapper.selectList(exportReqVO);
    }

    @Override
    public Integer syncDataByOutOrderProduct(List<ErpPurchaseInItemDO> needSyncToCost) {
        if(EasyListUtil.isEmpty(needSyncToCost)) return 0;
        // 1. 数据转换
        List<ZkProductCostDO> collect = needSyncToCost.stream().map(obj -> new ZkProductCostDO().buildeByOrderProduct(obj)).collect(Collectors.toList());
        // 2. 添加进入数据库
        zkProductCostMapper.insertBatch(collect);
        // 3. 同步数据 到 erp_purchase_in_items
        needSyncToCost.forEach(obj -> obj.setSyncToCostStatus("1"));
        outOrderProductService.batchUpdate(needSyncToCost);
        return collect.size();
    }

}
