package cn.iocoder.yudao.module.fish.controller.admin.zkpurifyoutpool;

import cn.iocoder.yudao.framework.common.util.easy.EasyDataMapper;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyprocess.PurifyProcessDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifytreatment.PurifyTreatmentDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifyprocess.PurifyProcessMapper;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyoutpool.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyoutpool.PurifyOutPoolDO;
import cn.iocoder.yudao.module.fish.service.zkpurifyoutpool.PurifyOutPoolService;

@Tag(name = "管理后台 - 出池登记")
@RestController
@RequestMapping("/fish/purify-out-pool")
@Validated
public class PurifyOutPoolController {

    @Resource
    private PurifyOutPoolService purifyOutPoolService;
    @Resource
    private PurifyProcessMapper purifyProcessMapper;

    @PostMapping("/create")
    @Operation(summary = "创建出池登记")
    @PreAuthorize("@ss.hasPermission('fish:purify-out-pool:create')")
    public CommonResult<Long> createPurifyOutPool(@Valid @RequestBody PurifyOutPoolSaveReqVO createReqVO) {
        return success(purifyOutPoolService.createPurifyOutPool(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新出池登记")
    @PreAuthorize("@ss.hasPermission('fish:purify-out-pool:update')")
    public CommonResult<Boolean> updatePurifyOutPool(@Valid @RequestBody PurifyOutPoolSaveReqVO updateReqVO) {
        purifyOutPoolService.updatePurifyOutPool(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除出池登记")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:purify-out-pool:delete')")
    public CommonResult<Boolean> deletePurifyOutPool(@RequestParam("id") Long id) {
        purifyOutPoolService.deletePurifyOutPool(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除出池登记")
                @PreAuthorize("@ss.hasPermission('fish:purify-out-pool:delete')")
    public CommonResult<Boolean> deletePurifyOutPoolList(@RequestParam("ids") List<Long> ids) {
        purifyOutPoolService.deletePurifyOutPoolListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得出池登记")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:purify-out-pool:query')")
    public CommonResult<PurifyOutPoolRespVO> getPurifyOutPool(@RequestParam("id") Long id) {
        PurifyOutPoolDO purifyOutPool = purifyOutPoolService.getPurifyOutPool(id);
        enhancerList(List.of(purifyOutPool));
        return success(BeanUtils.toBean(purifyOutPool, PurifyOutPoolRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得出池登记分页")
    @PreAuthorize("@ss.hasPermission('fish:purify-out-pool:query')")
    public CommonResult<PageResult<PurifyOutPoolRespVO>> getPurifyOutPoolPage(@Valid PurifyOutPoolPageReqVO pageReqVO) {
        PageResult<PurifyOutPoolDO> pageResult = purifyOutPoolService.getPurifyOutPoolPage(pageReqVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, PurifyOutPoolRespVO.class));
    }

    // 用 lambda汇总
    @SuppressWarnings("unchecked")
    public void enhancerList(List<PurifyOutPoolDO> tList) {
        // 1. 获取 关联 值
        Set<Long> oIds = EasyListUtil.listToIds(tList, PurifyOutPoolDO::getProcessId);
        // 2. 利用 关联值 查询
        List<PurifyProcessDO> oList = EasyDataMapper.listByValueList(purifyProcessMapper, PurifyProcessDO::getId, oIds);
        // 3. 赋值
        CommonUtil.origins2Targets4(oList, PurifyProcessDO::getId, EasyListUtil.getList(PurifyProcessDO::getRecordNumber),
                tList, PurifyOutPoolDO::getProcessId, EasyListUtil.setList(PurifyOutPoolDO::setRecordNumber));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出出池登记 Excel")
    @PreAuthorize("@ss.hasPermission('fish:purify-out-pool:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPurifyOutPoolExcel(@Valid PurifyOutPoolPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PurifyOutPoolDO> list = purifyOutPoolService.getPurifyOutPoolPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "出池登记.xls", "数据", PurifyOutPoolRespVO.class,
                        BeanUtils.toBean(list, PurifyOutPoolRespVO.class));
    }

}