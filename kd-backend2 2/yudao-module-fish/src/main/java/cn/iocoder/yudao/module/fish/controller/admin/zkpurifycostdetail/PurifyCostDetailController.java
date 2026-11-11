package cn.iocoder.yudao.module.fish.controller.admin.zkpurifycostdetail;

import cn.iocoder.yudao.framework.common.util.easy.EasyDataMapper;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifydailymonitoring.PurifyDailyMonitoringDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyprocess.PurifyProcessDO;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkpurifycostdetail.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifycostdetail.PurifyCostDetailDO;
import cn.iocoder.yudao.module.fish.service.zkpurifycostdetail.PurifyCostDetailService;

@Tag(name = "管理后台 - 成本明细记录")
@RestController
@RequestMapping("/fish/purify-cost-detail")
@Validated
public class PurifyCostDetailController {

    @Resource
    private PurifyCostDetailService purifyCostDetailService;

    @Resource
    private PurifyProcessMapper purifyProcessMapper;

    @PostMapping("/create")
    @Operation(summary = "创建成本明细记录")
    @PreAuthorize("@ss.hasPermission('fish:purify-cost-detail:create')")
    public CommonResult<Long> createPurifyCostDetail(@Valid @RequestBody PurifyCostDetailSaveReqVO createReqVO) {
        return success(purifyCostDetailService.createPurifyCostDetail(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新成本明细记录")
    @PreAuthorize("@ss.hasPermission('fish:purify-cost-detail:update')")
    public CommonResult<Boolean> updatePurifyCostDetail(@Valid @RequestBody PurifyCostDetailSaveReqVO updateReqVO) {
        purifyCostDetailService.updatePurifyCostDetail(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除成本明细记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:purify-cost-detail:delete')")
    public CommonResult<Boolean> deletePurifyCostDetail(@RequestParam("id") Long id) {
        purifyCostDetailService.deletePurifyCostDetail(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除成本明细记录")
                @PreAuthorize("@ss.hasPermission('fish:purify-cost-detail:delete')")
    public CommonResult<Boolean> deletePurifyCostDetailList(@RequestParam("ids") List<Long> ids) {
        purifyCostDetailService.deletePurifyCostDetailListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得成本明细记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:purify-cost-detail:query')")
    public CommonResult<PurifyCostDetailRespVO> getPurifyCostDetail(@RequestParam("id") Long id) {
        PurifyCostDetailDO purifyCostDetail = purifyCostDetailService.getPurifyCostDetail(id);
        enhancerList(List.of(purifyCostDetail));
        return success(BeanUtils.toBean(purifyCostDetail, PurifyCostDetailRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得成本明细记录分页")
    @PreAuthorize("@ss.hasPermission('fish:purify-cost-detail:query')")
    public CommonResult<PageResult<PurifyCostDetailRespVO>> getPurifyCostDetailPage(@Valid PurifyCostDetailPageReqVO pageReqVO) {
        PageResult<PurifyCostDetailDO> pageResult = purifyCostDetailService.getPurifyCostDetailPage(pageReqVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, PurifyCostDetailRespVO.class));
    }


    // 用 lambda汇总
    @SuppressWarnings("unchecked")
    public void enhancerList(List<PurifyCostDetailDO> tList) {
        // 1. 获取 关联 值
        Set<Long> oIds = EasyListUtil.listToIds(tList, PurifyCostDetailDO::getProcessId);
        // 2. 利用 关联值 查询
        List<PurifyProcessDO> oList = EasyDataMapper.listByValueList(purifyProcessMapper, PurifyProcessDO::getId, oIds);
        // 3. 赋值
        CommonUtil.origins2Targets4(oList, PurifyProcessDO::getId, EasyListUtil.getList(PurifyProcessDO::getRecordNumber),
                tList, PurifyCostDetailDO::getProcessId, EasyListUtil.setList(PurifyCostDetailDO::setRecordNumber));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出成本明细记录 Excel")
    @PreAuthorize("@ss.hasPermission('fish:purify-cost-detail:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPurifyCostDetailExcel(@Valid PurifyCostDetailPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PurifyCostDetailDO> list = purifyCostDetailService.getPurifyCostDetailPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "成本明细记录.xls", "数据", PurifyCostDetailRespVO.class,
                        BeanUtils.toBean(list, PurifyCostDetailRespVO.class));
    }

}