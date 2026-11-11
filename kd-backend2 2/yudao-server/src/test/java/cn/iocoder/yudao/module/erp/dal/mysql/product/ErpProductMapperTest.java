package cn.iocoder.yudao.module.erp.dal.mysql.product;


import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseOrderDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseOrderItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockRecordDO;
import cn.iocoder.yudao.module.erp.dal.mysql.purchase.ErpPurchaseInItemMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.purchase.ErpPurchaseInMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.purchase.ErpPurchaseOrderItemMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.purchase.ErpPurchaseOrderMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.sale.ErpSaleOrderItemMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.sale.ErpSaleOrderMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.sale.ErpSaleOutItemMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.sale.ErpSaleOutMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.stock.ErpStockMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.stock.ErpStockRecordMapper;
import cn.iocoder.yudao.server.YudaoServerApplication;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = YudaoServerApplication.class)
@RunWith(SpringRunner.class)
public class ErpProductMapperTest {
    @Resource
    private ErpProductMapper erpProductMapper;
    @Resource
    private ErpPurchaseInMapper erpPurchaseInMapper;
    @Resource
    private ErpPurchaseInItemMapper erpPurchaseInItemMapper;

    @Resource
    private ErpPurchaseOrderMapper erpPurchaseOrderMapper;
    @Resource
    private ErpPurchaseOrderItemMapper erpPurchaseOrderItemMapper;
    @Resource
    private ErpSaleOrderMapper erpSaleOrderMapper;
    @Resource
    private ErpSaleOrderItemMapper erpSaleOrderItemMapper;

    @Resource
    private ErpSaleOutMapper erpSaleOutMapper;
    @Resource
    private ErpSaleOutItemMapper erpSaleOutItemMapper;

    @Resource
    private ErpStockMapper erpStockMapper;
    @Resource
    private ErpStockRecordMapper erpStockRecordMapper;

    @Test
    public void test_selectStockInfo() {
        QueryWrapper<ErpProductDO> queryWrapper = new QueryWrapper<>();
        List<Map<String, Object>> maps = erpProductMapper.selectStockInfo(queryWrapper);
        System.out.println("============>"+JSON.toJSONString(maps));
    }

    @Test
    public void testErpMapper() {
        List<BaseMapperX> list = new ArrayList<>();
        // list.add(erpProductMapper);
//        list.add(erpPurchaseInMapper);
//        list.add(erpPurchaseInItemMapper);

//        list.add(erpPurchaseOrderMapper);
//        list.add(erpPurchaseOrderItemMapper);
//        list.add(erpSaleOrderMapper);
//        list.add(erpSaleOrderItemMapper);
//        list.add(erpSaleOutMapper);
//        list.add(erpSaleOutItemMapper);
//        list.add(erpStockMapper);
//        list.add(erpStockRecordMapper);

        for(BaseMapperX mapper : list) {
            List list1 = mapper.selectList();
            list1.forEach(obj -> {
                CommonUtil.setValByProperty(obj, "id", null);
                CommonUtil.setValByProperty(obj, "deptId", 104L);
            });
            mapper.insertBatch(list1);
            list1.forEach(obj -> {
                CommonUtil.setValByProperty(obj, "id", null);
                CommonUtil.setValByProperty(obj, "deptId", 105L);
            });
            mapper.insertBatch(list1);
            // System.out.println("list1=============>"+ JSON.toJSONString(list1));
        }
        System.out.println("list1=============>ok");
    }


    @Test
    public void testStockRecord() {
        Long split = 200L;
        Long deptId = 115L;
        String perfix = "_3";
        ErpStockRecordDO temp = new ErpStockRecordDO().setDeptId(103L);
        List<ErpStockRecordDO> erpStockDOS = erpStockRecordMapper.selectList(new QueryWrapper<>(temp));
        for (ErpStockRecordDO item : erpStockDOS) {
            item.setId(null);
            item.setDeptId(deptId);
            item.setWarehouseId(item.getWarehouseId()+split);
            item.setProductId(item.getProductId()+split);
        }
        erpStockRecordMapper.insertBatch(erpStockDOS);
    }

    @Test
    public void testStock() {
        Long split = 200L;
        Long deptId = 115L;
        String perfix = "_1";
        ErpStockDO temp = new ErpStockDO().setDeptId(103L);
        List<ErpStockDO> erpStockDOS = erpStockMapper.selectList(new QueryWrapper<>(temp));
        for (ErpStockDO item : erpStockDOS) {
            item.setId(null);
            item.setDeptId(deptId);
            item.setProductId(item.getProductId()+split);
        }
        erpStockMapper.insertBatch(erpStockDOS);
    }


    @Test
    public void testProduct() {
        // 1. 获取数据
        List<ErpProductDO> erpProductDOS = erpProductMapper.selectList();
        for (ErpProductDO item : erpProductDOS) {
            item.setId(item.getId()+100L);
            item.setDeptId(104L);
        }
        erpProductMapper.insertBatch(erpProductDOS);
        for (ErpProductDO item : erpProductDOS) {
            item.setId(item.getId()+100L);
            item.setDeptId(115L);
        }
        erpProductMapper.insertBatch(erpProductDOS);
        // 2. 执行添加
    }

    @Test
    public void testPurchaseInOrder() {
        Long split = 200L;
        Long deptId = 115L;
        String perfix = "_3";
        // 1. 获取数据
        ErpPurchaseInDO temp = new ErpPurchaseInDO().setDeptId(103L);
        List<ErpPurchaseInDO> list = erpPurchaseInMapper.selectList(new QueryWrapper<>(temp));
        List<ErpPurchaseInItemDO> sonList = erpPurchaseInItemMapper.selectList();
        Map<Long, List<ErpPurchaseInItemDO>> collect = sonList.stream().collect(Collectors.groupingBy(ErpPurchaseInItemDO::getInId));
        for (ErpPurchaseInDO item : list) {
            List<ErpPurchaseInItemDO> erpPurchaseInItemDOS = collect.get(item.getId());
            if(EasyListUtil.isEmpty(erpPurchaseInItemDOS)) continue;
            item.setInItems(erpPurchaseInItemDOS);
            item.setId(null);
            item.setNo(item.getNo()+perfix);
            item.setOrderNo(item.getOrderNo()+perfix);
            item.setDeptId(deptId);
            item.setSupplierId(item.getSupplierId()+split);
            item.setAccountId(item.getAccountId()+split);
        }
        // 2. 执行添加
        erpPurchaseInMapper.insertBatch(list);
        for (ErpPurchaseInDO item : list) {
            List<ErpPurchaseInItemDO> sonItems = item.getInItems();
            if(EasyListUtil.isEmpty(sonItems)) continue;
            for(ErpPurchaseInItemDO i : sonItems) {
                i.setInId(item.getId());
                i.setId(null);
                i.setDeptId(deptId);
                i.setProductId(i.getProductId()+split);
                i.setPurchaseInNo(item.getNo());
                i.setRelationOrderNo(item.getOrderNo());
            }
            erpPurchaseInItemMapper.insertBatch(sonItems);
        }
        System.out.println("====================>ok");
    }


    @Test
    public void testPurchaseOrder() {
        Long split = 200L;
        Long deptId = 115L;
        // 1. 获取数据
        ErpPurchaseOrderDO temp = new ErpPurchaseOrderDO().setDeptId(103L);
        List<ErpPurchaseOrderDO> list = erpPurchaseOrderMapper.selectList(new QueryWrapper<>(temp));
        List<ErpPurchaseOrderItemDO> sonList = erpPurchaseOrderItemMapper.selectList();
        Map<Long, List<ErpPurchaseOrderItemDO>> collect = sonList.stream().collect(Collectors.groupingBy(ErpPurchaseOrderItemDO::getOrderId));
        for (ErpPurchaseOrderDO item : list) {
            List<ErpPurchaseOrderItemDO> erpPurchaseInItemDOS = collect.get(item.getId());
            if(EasyListUtil.isEmpty(erpPurchaseInItemDOS)) continue;
            item.setOrderItems(erpPurchaseInItemDOS);
            item.setId(null);
            item.setNo(item.getNo()+"_3");
            item.setDeptId(deptId);
            item.setSupplierId(item.getSupplierId()+split);
            item.setAccountId(item.getAccountId()+split);
        }
        // 2. 执行添加
        erpPurchaseOrderMapper.insertBatch(list);
        for (ErpPurchaseOrderDO item : list) {
            List<ErpPurchaseOrderItemDO> erpPurchaseInItemDOS = item.getOrderItems();
            if(EasyListUtil.isEmpty(erpPurchaseInItemDOS)) continue;
            for(ErpPurchaseOrderItemDO i : erpPurchaseInItemDOS) {
                i.setOrderId(item.getId());
                i.setId(null);
                i.setDeptId(deptId);
                i.setProductId(i.getProductId()+split);
            }
            erpPurchaseOrderItemMapper.insertBatch(erpPurchaseInItemDOS);
        }
        System.out.println("====================>ok");
    }



}
