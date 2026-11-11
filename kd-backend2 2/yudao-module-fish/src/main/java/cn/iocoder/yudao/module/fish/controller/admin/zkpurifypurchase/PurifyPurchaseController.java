package cn.iocoder.yudao.module.fish.controller.admin.zkpurifypurchase;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkpurifypurchase.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifypurchase.PurifyPurchaseDO;
import cn.iocoder.yudao.module.fish.service.zkpurifypurchase.PurifyPurchaseService;

@Tag(name = "管理后台 - 采购记录")
@RestController
@RequestMapping("/fish/purify-purchase")
@Validated
public class PurifyPurchaseController {

    @Resource
    private PurifyPurchaseService purifyPurchaseService;

    @PostMapping("/create")
    @Operation(summary = "创建采购记录")
    @PreAuthorize("@ss.hasPermission('fish:purify-purchase:create')")
    public CommonResult<Long> createPurifyPurchase(@Valid @RequestBody PurifyPurchaseSaveReqVO createReqVO) {
        return success(purifyPurchaseService.createPurifyPurchase(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新采购记录")
    @PreAuthorize("@ss.hasPermission('fish:purify-purchase:update')")
    public CommonResult<Boolean> updatePurifyPurchase(@Valid @RequestBody PurifyPurchaseSaveReqVO updateReqVO) {
        purifyPurchaseService.updatePurifyPurchase(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除采购记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:purify-purchase:delete')")
    public CommonResult<Boolean> deletePurifyPurchase(@RequestParam("id") Long id) {
        purifyPurchaseService.deletePurifyPurchase(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除采购记录")
                @PreAuthorize("@ss.hasPermission('fish:purify-purchase:delete')")
    public CommonResult<Boolean> deletePurifyPurchaseList(@RequestParam("ids") List<Long> ids) {
        purifyPurchaseService.deletePurifyPurchaseListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得采购记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:purify-purchase:query')")
    public CommonResult<PurifyPurchaseRespVO> getPurifyPurchase(@RequestParam("id") Long id) {
        PurifyPurchaseDO purifyPurchase = purifyPurchaseService.getPurifyPurchase(id);
        return success(BeanUtils.toBean(purifyPurchase, PurifyPurchaseRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得采购记录分页")
    @PreAuthorize("@ss.hasPermission('fish:purify-purchase:query')")
    public CommonResult<PageResult<PurifyPurchaseRespVO>> getPurifyPurchasePage(@Valid PurifyPurchasePageReqVO pageReqVO) {
        PageResult<PurifyPurchaseDO> pageResult = purifyPurchaseService.getPurifyPurchasePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PurifyPurchaseRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出采购记录 Excel")
    @PreAuthorize("@ss.hasPermission('fish:purify-purchase:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPurifyPurchaseExcel(@Valid PurifyPurchasePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PurifyPurchaseDO> list = purifyPurchaseService.getPurifyPurchasePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "采购记录.xls", "数据", PurifyPurchaseRespVO.class,
                        BeanUtils.toBean(list, PurifyPurchaseRespVO.class));
    }

}