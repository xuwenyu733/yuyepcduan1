package cn.iocoder.yudao.module.fish.controller.admin.zkproductcost;

import cn.iocoder.yudao.framework.common.util.easy.EasyDataMapper;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockOutItemDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondsystem.ZkPondSystemDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpondsystem.ZkPondSystemMapper;
import cn.iocoder.yudao.module.fish.service.custom.OutOrderService;
import cn.iocoder.yudao.module.fish.service.custom.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
// import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;
// import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;

import cn.iocoder.yudao.module.fish.controller.admin.zkproductcost.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkproductcost.ZkProductCostDO;
// import cn.iocoder.yudao.module.fish.convert.zkproductcost.ZkProductCostConvert;
import cn.iocoder.yudao.module.fish.service.zkproductcost.ZkProductCostService;

@Tag(name = "管理后台 - 成本")
@RestController
@RequestMapping("/fish/zk-product-cost")
@Validated
@Slf4j
public class ZkProductCostController {

    @Resource
    private ZkProductCostService zkProductCostService;
    @Resource
    private OutOrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ZkPondSystemMapper zkPondSystemMapper;

    @PostMapping("/create")
    @Operation(summary = "创建成本")
    public CommonResult<Long> createZkProductCost(@Valid @RequestBody ZkProductCostCreateReqVO createReqVO) {
        return success(zkProductCostService.createZkProductCost(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新成本")
    public CommonResult<Boolean> updateZkProductCost(@Valid @RequestBody ZkProductCostUpdateReqVO updateReqVO) {
        zkProductCostService.updateZkProductCost(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除成本")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkProductCost(@RequestParam("id") Long id) {
        zkProductCostService.deleteZkProductCost(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得成本")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkProductCostRespVO> getZkProductCost(@RequestParam("id") Long id) {
        ZkProductCostDO zkProductCost = zkProductCostService.getZkProductCost(id);
        enhancer(Arrays.asList(zkProductCost));
        enhancerList(List.of(zkProductCost));
        return success(BeanUtils.toBean(zkProductCost, ZkProductCostRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得成本列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkProductCostRespVO>> getZkProductCostList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkProductCostDO> list = zkProductCostService.getZkProductCostList(ids);
        enhancerList(list);
        return success(BeanUtils.toBean(list, ZkProductCostRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得成本分页")
    public CommonResult<PageResult<ZkProductCostRespVO>> getZkProductCostPage(@Valid ZkProductCostPageReqVO pageVO) {
        // 1. 获取 pl_out_order(o_type 订单类型(0:出库单;1:入库单;))入库记录 还没有同步 到 zk_product_cost 的数据,执行同步
        List<ErpPurchaseInItemDO> needSyncToCost = orderService.getNeedSyncToCost();
        if(EasyListUtil.isNotEmpty(needSyncToCost)) {
            Integer count = zkProductCostService.syncDataByOutOrderProduct(needSyncToCost);
            log.warn("【从入库单同步数据到成本统计-{}】middle,当前同步:{}条", TokenUtil.userInfo(), count);
        }
        // 2. 获取记录数据
        PageResult<ZkProductCostDO> pageResult = zkProductCostService.getZkProductCostPage(pageVO);
        enhancer(pageResult.getList());
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkProductCostRespVO.class));
    }

    @SuppressWarnings("unchecked")
    public void enhancerList(List<ZkProductCostDO> tList) {
        // 1. 获取 关联 值
        Set<Long> oIds = EasyListUtil.listToIds(tList, ZkProductCostDO::getSystemId);
        // 2. 利用 关联值 查询
        List<ZkPondSystemDO> oList = EasyDataMapper.listByValueList(zkPondSystemMapper, ZkPondSystemDO::getId, oIds);
        // 3. 赋值
        CommonUtil.origins2Targets4(oList, ZkPondSystemDO::getId, EasyListUtil.getList(ZkPondSystemDO::getWorkshopName, ZkPondSystemDO::getSystemNum),
                tList, ZkProductCostDO::getSystemId, EasyListUtil.setList(ZkProductCostDO::setWorkshopName, ZkProductCostDO::setPondNum));
    }

    public void enhancer(List<ZkProductCostDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        // 1. 获取商品信息
        List<String> productNos = list.stream().map(obj -> obj.getProductNo()).collect(Collectors.toList());
        List<ErpProductDO> ErpProductDOS = productService.getproductByNos(productNos);
        if(EasyListUtil.isEmpty(ErpProductDOS)) return;
        // 2. 赋值
        for(ZkProductCostDO local : list) {
            for(ErpProductDO remote : ErpProductDOS) {
                if(StringUtils.equals(local.getProductNo(), remote.getProductNo())) {
                    local.setProductName(remote.getProductName());
                    break;
                }
            }
        }
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出成本 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkProductCostExcel(@Valid ZkProductCostExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkProductCostDO> list = zkProductCostService.getZkProductCostList(exportReqVO);
        // 导出 Excel
        List<ZkProductCostExcelVO> datas = BeanUtils.toBean(list, ZkProductCostExcelVO.class);
        ExcelUtils.write(response, "成本.xls", "数据", ZkProductCostExcelVO.class, datas);
    }

}
