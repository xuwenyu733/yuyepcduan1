package cn.iocoder.yudao.module.fish.controller.admin.zkpurifyarchive;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyarchive.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyarchive.PurifyArchiveDO;
import cn.iocoder.yudao.module.fish.service.zkpurifyarchive.PurifyArchiveService;

@Tag(name = "管理后台 - 入池档案")
@RestController
@RequestMapping("/fish/purify-archive")
@Validated
public class PurifyArchiveController {

    @Resource
    private PurifyArchiveService purifyArchiveService;

    @PostMapping("/create")
    @Operation(summary = "创建入池档案")
    @PreAuthorize("@ss.hasPermission('fish:purify-archive:create')")
    public CommonResult<Long> createPurifyArchive(@Valid @RequestBody PurifyArchiveSaveReqVO createReqVO) {
        return success(purifyArchiveService.createPurifyArchive(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新入池档案")
    @PreAuthorize("@ss.hasPermission('fish:purify-archive:update')")
    public CommonResult<Boolean> updatePurifyArchive(@Valid @RequestBody PurifyArchiveSaveReqVO updateReqVO) {
        purifyArchiveService.updatePurifyArchive(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除入池档案")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:purify-archive:delete')")
    public CommonResult<Boolean> deletePurifyArchive(@RequestParam("id") Long id) {
        purifyArchiveService.deletePurifyArchive(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除入池档案")
                @PreAuthorize("@ss.hasPermission('fish:purify-archive:delete')")
    public CommonResult<Boolean> deletePurifyArchiveList(@RequestParam("ids") List<Long> ids) {
        purifyArchiveService.deletePurifyArchiveListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得入池档案")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:purify-archive:query')")
    public CommonResult<PurifyArchiveRespVO> getPurifyArchive(@RequestParam("id") Long id) {
        PurifyArchiveDO purifyArchive = purifyArchiveService.getPurifyArchive(id);
        return success(BeanUtils.toBean(purifyArchive, PurifyArchiveRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得入池档案分页")
    @PreAuthorize("@ss.hasPermission('fish:purify-archive:query')")
    public CommonResult<PageResult<PurifyArchiveRespVO>> getPurifyArchivePage(@Valid PurifyArchivePageReqVO pageReqVO) {
        PageResult<PurifyArchiveDO> pageResult = purifyArchiveService.getPurifyArchivePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PurifyArchiveRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出入池档案 Excel")
    @PreAuthorize("@ss.hasPermission('fish:purify-archive:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPurifyArchiveExcel(@Valid PurifyArchivePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PurifyArchiveDO> list = purifyArchiveService.getPurifyArchivePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "入池档案.xls", "数据", PurifyArchiveRespVO.class,
                        BeanUtils.toBean(list, PurifyArchiveRespVO.class));
    }

    @GetMapping("/get-stats")
    @Operation(summary = "获得风味优化统计数据")
    @PreAuthorize("@ss.hasPermission('fish:purify-archive:query')")
    public CommonResult<PurifyStatsRespVO> getPurifyStats(@Valid PurifyArchivePageReqVO pageReqVO) {
        return success(purifyArchiveService.getPurifyStats(pageReqVO));
    }

    @GetMapping("/record-query-page")
    @Operation(summary = "获得记录查询分页")
    @PreAuthorize("@ss.hasPermission('fish:purify-archive:query')")
    public CommonResult<PageResult<PurifyArchiveRespVO>> getRecordQueryPage(@Valid PurifyArchivePageReqVO pageReqVO) {
        return success(purifyArchiveService.getRecordQueryPage(pageReqVO));
    }

    @GetMapping("/sync-all-stats")
    @Operation(summary = "手动同步所有流程统计数据")
    @PreAuthorize("@ss.hasPermission('fish:purify-archive:update')")
    public CommonResult<Boolean> syncAllStats() {
        purifyArchiveService.syncAllProcessStats();
        return success(true);
    }

}