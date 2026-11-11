package cn.iocoder.yudao.module.erp.controller.admin.stock;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.base_api.fish.ZkScatteredInventoryControllerApi;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyMap3;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.erp.controller.admin.product.vo.product.ErpProductRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.out.ErpStockOutSaveReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.record.ErpStockRecordPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.record.ErpStockRecordRespVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockRecordDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpWarehouseDO;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockRecordService;
import cn.iocoder.yudao.module.erp.service.stock.ErpWarehouseService;
import cn.iocoder.yudao.module.erp.service.stock.bo.ErpStockRecordCreateReqBO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - ERP 产品库存明细")
@RestController
@RequestMapping("/erp/stock-record")
@Validated
@Slf4j
public class ErpStockRecordController {

    @Resource
    private ErpStockRecordService stockRecordService;
    @Resource
    private ErpProductService productService;
    @Resource
    private ErpWarehouseService warehouseService;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private ZkScatteredInventoryControllerApi zkScatteredInventoryControllerApi;

    @PostMapping("/create")
    @Operation(summary = "创建出库记录")
    // @PreAuthorize("@ss.hasPermission('erp:stock-out:create')") @Valid
    public CommonResult<Long> createStockRecord(@RequestBody ErpStockRecordCreateReqBO createReqVO) {
        // 1. 创建出库记录
        ErpStockRecordDO stockRecord = stockRecordService.createStockRecord(createReqVO);
        // 2. 如果有零星入库记录，则创建零星入库记录
        if(EasyBigDecimalUtil.gt0(createReqVO.getRemainingQuantity()) && createReqVO.getRemainingUnitId() != null) {
            insertScatteredInventory(createReqVO, stockRecord.getId());
        }
        return success(stockRecord.getId());
    }

    public void insertScatteredInventory(ErpStockRecordCreateReqBO createReqVO, Long busId) {
        AdminUserRespDTO adminUserRespDTO = adminUserApi.getUser(SecurityFrameworkUtils.getLoginUserId());
        Map<String, Object> param = new EasyMap3<Object>().put("productId", createReqVO.getProductId()).put("warehouseId", createReqVO.getWarehouseId())
                .put("unitId", createReqVO.getRemainingUnitId()).put("remainingQuantity", createReqVO.getRemainingQuantity())
                .put("busId", busId).put("remark", "消耗与出库自动生成").put("busType", "2")
                .put("userId", adminUserRespDTO.getId()).put("deptId", adminUserRespDTO.getDeptId()).value();
        CommonResult<Long> zkScatteredInventory = zkScatteredInventoryControllerApi.createZkScatteredInventory(param);
        log.warn("【创建出库记录-自动生成零星入库】middle,零星入库信息:{},返回结果:{}", JSON.toJSONString(param), JSON.toJSONString(zkScatteredInventory));
    }

    @PutMapping("/update")
    @Operation(summary = "更新出库记录")
    // @PreAuthorize("@ss.hasPermission('erp:stock-out:update')") @Valid
    public CommonResult<Boolean> updateStockRecord(@RequestBody ErpStockRecordCreateReqBO updateReqVO) {
        stockRecordService.updateStockRecord(updateReqVO);
        // 2. 如果有零星入库记录，则创建零星入库记录
        if(EasyBigDecimalUtil.gt0(updateReqVO.getRemainingQuantity()) && updateReqVO.getRemainingUnitId() != null) {
            // 2.1 删除 原来的零星入库记录
            CommonResult<Boolean> booleanCommonResult = zkScatteredInventoryControllerApi.deleteZkScatteredInventoryByBusId(updateReqVO.getId());
            log.warn("【更新出库记录-先删除零星入库】middle,出库信息:{},返回结果:{}", JSON.toJSONString(updateReqVO), JSON.toJSONString(booleanCommonResult));
            // 2.2 重新添加零星入库记录
            insertScatteredInventory(updateReqVO, updateReqVO.getId());
        }

        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得产品库存明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:stock-record:query')")
    public CommonResult<ErpStockRecordRespVO> getStockRecord(@RequestParam("id") Long id) {
        ErpStockRecordDO stockRecord = stockRecordService.getStockRecord(id);
        return success(BeanUtils.toBean(stockRecord, ErpStockRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得产品库存明细分页")
    @PreAuthorize("@ss.hasPermission('erp:stock-record:query')")
    public CommonResult<PageResult<ErpStockRecordRespVO>> getStockRecordPage(@Valid ErpStockRecordPageReqVO pageReqVO) {
        PageResult<ErpStockRecordDO> pageResult = stockRecordService.getStockRecordPage(pageReqVO);
        return success(buildStockRecrodVOPageResult(pageResult));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除出库单")
    @Parameter(name = "ids", description = "编号数组", required = true)
    // @PreAuthorize("@ss.hasPermission('erp:stock-out:delete')")
    public CommonResult<Boolean> delete(@RequestParam("ids") List<Long> ids) {
        stockRecordService.deleteByIds(ids);
        return success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出产品库存明细 Excel")
    @PreAuthorize("@ss.hasPermission('erp:stock-record:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportStockRecordExcel(@Valid ErpStockRecordPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ErpStockRecordRespVO> list = buildStockRecrodVOPageResult(stockRecordService.getStockRecordPage(pageReqVO)).getList();
        // 导出 Excel
        ExcelUtils.write(response, "产品库存明细.xls", "数据", ErpStockRecordRespVO.class, list);
    }

    private PageResult<ErpStockRecordRespVO> buildStockRecrodVOPageResult(PageResult<ErpStockRecordDO> pageResult) {
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        Map<Long, ErpProductRespVO> productMap = productService.getProductVOMap(
                convertSet(pageResult.getList(), ErpStockRecordDO::getProductId));
        Map<Long, ErpWarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                convertSet(pageResult.getList(), ErpStockRecordDO::getWarehouseId));
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(
                convertSet(pageResult.getList(), record -> Long.parseLong(record.getCreator())));
        return BeanUtils.toBean(pageResult, ErpStockRecordRespVO.class, stock -> {
            MapUtils.findAndThen(productMap, stock.getProductId(), product -> stock.setProductName(product.getName())
                    .setCategoryName(product.getCategoryName()).setUnitName(product.getUnitName()));
            MapUtils.findAndThen(warehouseMap, stock.getWarehouseId(), warehouse -> stock.setWarehouseName(warehouse.getName()));
            MapUtils.findAndThen(userMap, Long.parseLong(stock.getCreator()), user -> stock.setCreatorName(user.getNickname()));
        });
    }

}