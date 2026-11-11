package cn.iocoder.yudao.module.fish.controller.admin.zkpurifyprocess;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyarchive.PurifyArchiveDO;
import cn.iocoder.yudao.module.fish.service.zkpurifyarchive.PurifyArchiveService;
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
import java.util.stream.Collectors;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyprocess.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyprocess.PurifyProcessDO;
import cn.iocoder.yudao.module.fish.service.zkpurifyprocess.PurifyProcessService;

@Tag(name = "管理后台 - 优化流程记录")
@RestController
@RequestMapping("/fish/purify-process")
@Validated
public class PurifyProcessController {

    @Resource
    private PurifyProcessService purifyProcessService;

    @Resource
    private PurifyArchiveService purifyArchiveService;

    @PostMapping("/create")
    @Operation(summary = "创建优化流程记录")
    @PreAuthorize("@ss.hasPermission('fish:purify-process:create')")
    public CommonResult<Long> createPurifyProcess(@Valid @RequestBody PurifyProcessSaveReqVO createReqVO) {
        return success(purifyProcessService.createPurifyProcess(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新优化流程记录")
    @PreAuthorize("@ss.hasPermission('fish:purify-process:update')")
    public CommonResult<Boolean> updatePurifyProcess(@Valid @RequestBody PurifyProcessSaveReqVO updateReqVO) {
        purifyProcessService.updatePurifyProcess(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除优化流程记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:purify-process:delete')")
    public CommonResult<Boolean> deletePurifyProcess(@RequestParam("id") Long id) {
        purifyProcessService.deletePurifyProcess(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除优化流程记录")
                @PreAuthorize("@ss.hasPermission('fish:purify-process:delete')")
    public CommonResult<Boolean> deletePurifyProcessList(@RequestParam("ids") List<Long> ids) {
        purifyProcessService.deletePurifyProcessListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得优化流程记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:purify-process:query')")
    public CommonResult<PurifyProcessRespVO> getPurifyProcess(@RequestParam("id") Long id) {
        PurifyProcessDO purifyProcess = purifyProcessService.getPurifyProcess(id);
        enhancerList(List.of(purifyProcess));
        return success(BeanUtils.toBean(purifyProcess, PurifyProcessRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得优化流程记录分页")
    @PreAuthorize("@ss.hasPermission('fish:purify-process:query')")
    public CommonResult<PageResult<PurifyProcessRespVO>> getPurifyProcessPage(@Valid PurifyProcessPageReqVO pageReqVO) {
        PageResult<PurifyProcessDO> pageResult = purifyProcessService.getPurifyProcessPage(pageReqVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, PurifyProcessRespVO.class));
    }

    public void enhancerList(List<PurifyProcessDO> tList) {
        if(EasyListUtil.isEmpty(tList)) return;
        // 1. 获取采购信息
        Set<Long> achrivedIdSet = tList.stream().map(PurifyProcessDO::getArchiveId).collect(Collectors.toSet());
        List<PurifyArchiveDO> oList = purifyArchiveService.getByIds(achrivedIdSet);
        // 2. 执行赋值
        CommonUtil.origins2Targets4(oList, PurifyArchiveDO::getId, tList, PurifyProcessDO::getArchiveId, (PurifyArchiveDO o, PurifyProcessDO t) -> {
            t.setPoolNumber(o.getPoolNumber()).setPurchaseOrderNo(o.getPurchaseOrderNo())
                    .setCategory(o.getCategory());
        });
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出优化流程记录 Excel")
    @PreAuthorize("@ss.hasPermission('fish:purify-process:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPurifyProcessExcel(@Valid PurifyProcessPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PurifyProcessDO> list = purifyProcessService.getPurifyProcessPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "优化流程记录.xls", "数据", PurifyProcessRespVO.class,
                        BeanUtils.toBean(list, PurifyProcessRespVO.class));
    }

}