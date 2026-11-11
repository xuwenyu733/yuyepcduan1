package cn.iocoder.yudao.module.erp.datapermission.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import cn.iocoder.yudao.module.erp.dal.dataobject.finance.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductCategoryDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductUnitDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * fish 模块的数据权限 Configuration
 *
 * @author 东东
 */
@Configuration(proxyBeanMethods = false)
public class ErpDataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer erpDeptDataPermissionRuleCustomizer() {
        return rule -> {
            // dept
            // rule.addDeptColumn(ZkAppLogDO.class);
            rule.addDeptColumn(ErpAccountDO.class);
            rule.addDeptColumn(ErpCustomerDO.class);
            rule.addDeptColumn(ErpFinancePaymentDO.class);
            rule.addDeptColumn(ErpFinancePaymentItemDO.class);
            rule.addDeptColumn(ErpFinanceReceiptDO.class);
            rule.addDeptColumn(ErpFinanceReceiptItemDO.class);
            rule.addDeptColumn(ErpProductDO.class);
            // rule.addDeptColumn(ErpProductCategoryDO.class);
            // rule.addDeptColumn(ErpProductUnitDO.class);
            rule.addDeptColumn(ErpPurchaseInDO.class);
            rule.addDeptColumn(ErpPurchaseInItemDO.class);
            rule.addDeptColumn(ErpPurchaseOrderDO.class);
            rule.addDeptColumn(ErpPurchaseOrderItemDO.class);
            rule.addDeptColumn(ErpPurchaseReturnDO.class);
            rule.addDeptColumn(ErpPurchaseReturnItemDO.class);
            rule.addDeptColumn(ErpSaleOrderDO.class);
            rule.addDeptColumn(ErpSaleOrderItemDO.class);
            rule.addDeptColumn(ErpSaleOutDO.class);
            rule.addDeptColumn(ErpSaleOutItemDO.class);
            rule.addDeptColumn(ErpSaleReturnDO.class);
            rule.addDeptColumn(ErpSaleReturnItemDO.class);
            rule.addDeptColumn(ErpStockDO.class);
            rule.addDeptColumn(ErpStockCheckDO.class);
            rule.addDeptColumn(ErpStockCheckItemDO.class);
            rule.addDeptColumn(ErpStockInDO.class);
            rule.addDeptColumn(ErpStockInItemDO.class);
            rule.addDeptColumn(ErpStockMoveDO.class);
            rule.addDeptColumn(ErpStockMoveItemDO.class);
            rule.addDeptColumn(ErpStockOutDO.class);
            rule.addDeptColumn(ErpStockOutItemDO.class);
            rule.addDeptColumn(ErpStockRecordDO.class);
            rule.addDeptColumn(ErpSupplierDO.class);
            rule.addDeptColumn(ErpWarehouseDO.class);

        };
    }

}
