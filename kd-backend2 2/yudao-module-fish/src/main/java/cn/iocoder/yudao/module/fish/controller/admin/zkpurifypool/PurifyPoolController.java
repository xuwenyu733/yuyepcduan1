package cn.iocoder.yudao.module.fish.controller.admin.zkpurifypool;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkpurifypool.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifypool.PurifyPoolDO;
import cn.iocoder.yudao.module.fish.service.zkpurifypool.PurifyPoolService;

@Tag(name = "管理后台 - 鱼池管理")
@RestController
@RequestMapping("/fish/purify-pool")
@Validated
public class PurifyPoolController {

    @Resource
    private PurifyPoolService purifyPoolService;

    @PostMapping("/create")
    @Operation(summary = "创建鱼池管理")
    @PreAuthorize("@ss.hasPermission('fish:purify-pool:create')")
    public CommonResult<Long> createPurifyPool(@Valid @RequestBody PurifyPoolSaveReqVO createReqVO) {
        return success(purifyPoolService.createPurifyPool(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新鱼池管理")
    @PreAuthorize("@ss.hasPermission('fish:purify-pool:update')")
    public CommonResult<Boolean> updatePurifyPool(@Valid @RequestBody PurifyPoolSaveReqVO updateReqVO) {
        purifyPoolService.updatePurifyPool(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除鱼池管理")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:purify-pool:delete')")
    public CommonResult<Boolean> deletePurifyPool(@RequestParam("id") Long id) {
        purifyPoolService.deletePurifyPool(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除鱼池管理")
                @PreAuthorize("@ss.hasPermission('fish:purify-pool:delete')")
    public CommonResult<Boolean> deletePurifyPoolList(@RequestParam("ids") List<Long> ids) {
        purifyPoolService.deletePurifyPoolListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得鱼池管理")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:purify-pool:query')")
    public CommonResult<PurifyPoolRespVO> getPurifyPool(@RequestParam("id") Long id) {
        PurifyPoolDO purifyPool = purifyPoolService.getPurifyPool(id);
        return success(BeanUtils.toBean(purifyPool, PurifyPoolRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得鱼池管理分页")
    @PreAuthorize("@ss.hasPermission('fish:purify-pool:query')")
    public CommonResult<PageResult<PurifyPoolRespVO>> getPurifyPoolPage(@Valid PurifyPoolPageReqVO pageReqVO) {
        PageResult<PurifyPoolDO> pageResult = purifyPoolService.getPurifyPoolPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PurifyPoolRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出鱼池管理 Excel")
    @PreAuthorize("@ss.hasPermission('fish:purify-pool:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPurifyPoolExcel(@Valid PurifyPoolPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PurifyPoolDO> list = purifyPoolService.getPurifyPoolPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "鱼池管理.xls", "数据", PurifyPoolRespVO.class,
                        BeanUtils.toBean(list, PurifyPoolRespVO.class));
    }

}