package cn.iocoder.yudao.module.fish.controller.admin.zkpurifydailymonitoring;

import cn.iocoder.yudao.framework.common.util.easy.EasyDataMapper;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkpurifydailymonitoring.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifydailymonitoring.PurifyDailyMonitoringDO;
import cn.iocoder.yudao.module.fish.service.zkpurifydailymonitoring.PurifyDailyMonitoringService;

@Tag(name = "管理后台 - 日常监测记录")
@RestController
@RequestMapping("/fish/purify-daily-monitoring")
@Validated
public class PurifyDailyMonitoringController {

    @Resource
    private PurifyDailyMonitoringService purifyDailyMonitoringService;

    @Resource
    private PurifyProcessMapper purifyProcessMapper;

    @PostMapping("/create")
    @Operation(summary = "创建日常监测记录")
    @PreAuthorize("@ss.hasPermission('fish:purify-daily-monitoring:create')")
    public CommonResult<Long> createPurifyDailyMonitoring(@Valid @RequestBody PurifyDailyMonitoringSaveReqVO createReqVO) {
        return success(purifyDailyMonitoringService.createPurifyDailyMonitoring(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新日常监测记录")
    @PreAuthorize("@ss.hasPermission('fish:purify-daily-monitoring:update')")
    public CommonResult<Boolean> updatePurifyDailyMonitoring(@Valid @RequestBody PurifyDailyMonitoringSaveReqVO updateReqVO) {
        purifyDailyMonitoringService.updatePurifyDailyMonitoring(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除日常监测记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:purify-daily-monitoring:delete')")
    public CommonResult<Boolean> deletePurifyDailyMonitoring(@RequestParam("id") Long id) {
        purifyDailyMonitoringService.deletePurifyDailyMonitoring(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除日常监测记录")
                @PreAuthorize("@ss.hasPermission('fish:purify-daily-monitoring:delete')")
    public CommonResult<Boolean> deletePurifyDailyMonitoringList(@RequestParam("ids") List<Long> ids) {
        purifyDailyMonitoringService.deletePurifyDailyMonitoringListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得日常监测记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:purify-daily-monitoring:query')")
    public CommonResult<PurifyDailyMonitoringRespVO> getPurifyDailyMonitoring(@RequestParam("id") Long id) {
        PurifyDailyMonitoringDO purifyDailyMonitoring = purifyDailyMonitoringService.getPurifyDailyMonitoring(id);
        enhancerList(List.of(purifyDailyMonitoring));
        return success(BeanUtils.toBean(purifyDailyMonitoring, PurifyDailyMonitoringRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得日常监测记录分页")
    @PreAuthorize("@ss.hasPermission('fish:purify-daily-monitoring:query')")
    public CommonResult<PageResult<PurifyDailyMonitoringRespVO>> getPurifyDailyMonitoringPage(@Valid PurifyDailyMonitoringPageReqVO pageReqVO) {
        PageResult<PurifyDailyMonitoringDO> pageResult = purifyDailyMonitoringService.getPurifyDailyMonitoringPage(pageReqVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, PurifyDailyMonitoringRespVO.class));
    }

    // 用 lambda汇总
    @SuppressWarnings("unchecked")
    public void enhancerList(List<PurifyDailyMonitoringDO> tList) {
        // 1. 获取 关联 值
        Set<Long> oIds = EasyListUtil.listToIds(tList, PurifyDailyMonitoringDO::getProcessId);
        // 2. 利用 关联值 查询
        List<PurifyProcessDO> oList = EasyDataMapper.listByValueList(purifyProcessMapper, PurifyProcessDO::getId, oIds);
        // 3. 赋值
        CommonUtil.origins2Targets4(oList, PurifyProcessDO::getId, EasyListUtil.getList(PurifyProcessDO::getRecordNumber),
                tList, PurifyDailyMonitoringDO::getProcessId, EasyListUtil.setList(PurifyDailyMonitoringDO::setRecordNumber));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出日常监测记录 Excel")
    @PreAuthorize("@ss.hasPermission('fish:purify-daily-monitoring:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPurifyDailyMonitoringExcel(@Valid PurifyDailyMonitoringPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PurifyDailyMonitoringDO> list = purifyDailyMonitoringService.getPurifyDailyMonitoringPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "日常监测记录.xls", "数据", PurifyDailyMonitoringRespVO.class,
                        BeanUtils.toBean(list, PurifyDailyMonitoringRespVO.class));
    }

}