package cn.iocoder.yudao.module.fish.service.zkproductcost;

import java.util.*;

import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockOutItemDO;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkproductcost.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkproductcost.ZkProductCostDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 成本 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkProductCostService {

    /**
     * 创建成本
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkProductCost(@Valid ZkProductCostCreateReqVO createReqVO);

    /**
     * 更新成本
     *
     * @param updateReqVO 更新信息
     */
    void updateZkProductCost(@Valid ZkProductCostUpdateReqVO updateReqVO);

    /**
     * 删除成本
     *
     * @param id 编号
     */
    void deleteZkProductCost(Long id);

    /**
     * 获得成本
     *
     * @param id 编号
     * @return 成本
     */
    ZkProductCostDO getZkProductCost(Long id);

    /**
     * 获得成本列表
     *
     * @param ids 编号
     * @return 成本列表
     */
    List<ZkProductCostDO> getZkProductCostList(Collection<Long> ids);

    /**
     * 获得成本分页
     *
     * @param pageReqVO 分页查询
     * @return 成本分页
     */
    PageResult<ZkProductCostDO> getZkProductCostPage(ZkProductCostPageReqVO pageReqVO);

    /**
     * 获得成本列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 成本列表
     */
    List<ZkProductCostDO> getZkProductCostList(ZkProductCostExportReqVO exportReqVO);

    /**
     * 从入库记录 同步 数据 到 成本统计
     * 这里需要注意， 成本类型 字典数据：zk_product_cost_product_type 类型(1:饲料;2:药品;3:设备;4:能源;5:额外;6:固定资产;7:库存;)
     * 和 表 pl_product_category.category_no == zk_product_cost_product_type.value 且都是数字类型，不要乱设置
     * @param needSyncToCost
     */
    Integer syncDataByOutOrderProduct(List<ErpPurchaseInItemDO> needSyncToCost);
}
