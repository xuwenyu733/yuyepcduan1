package cn.iocoder.yudao.module.fish.controller.admin.workperformance;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.controller.admin.workperformance.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.workperformance.OaWorkPerformanceDO;
import cn.iocoder.yudao.module.fish.service.workperformance.OaWorkPerformanceService;

@Tag(name = "管理后台 - OA工作绩效评分")
@RestController
@RequestMapping("/fish/oa-work-performance")
@Validated
public class OaWorkPerformanceController {

    @Resource
    private OaWorkPerformanceService oaWorkPerformanceService;

    @PostMapping("/create")
    @Operation(summary = "创建OA工作绩效评分")
    public CommonResult<Long> createOaWorkPerformance(@Valid @RequestBody OaWorkPerformanceCreateReqVO createReqVO) {
        return success(oaWorkPerformanceService.createOaWorkPerformance(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新OA工作绩效评分")
    public CommonResult<Boolean> updateOaWorkPerformance(@Valid @RequestBody OaWorkPerformanceUpdateReqVO updateReqVO) {
        oaWorkPerformanceService.updateOaWorkPerformance(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除OA工作绩效评分")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteOaWorkPerformance(@RequestParam("id") Long id) {
        oaWorkPerformanceService.deleteOaWorkPerformance(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得OA工作绩效评分")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<OaWorkPerformanceRespVO> getOaWorkPerformance(@RequestParam("id") Long id) {
        OaWorkPerformanceDO performance = oaWorkPerformanceService.getOaWorkPerformance(id);
        return success(BeanUtils.toBean(performance, OaWorkPerformanceRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得OA工作绩效评分分页")
    public CommonResult<PageResult<OaWorkPerformanceRespVO>> getOaWorkPerformancePage(@Valid OaWorkPerformancePageReqVO pageReqVO) {
        PageResult<OaWorkPerformanceDO> pageResult = oaWorkPerformanceService.getOaWorkPerformancePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, OaWorkPerformanceRespVO.class));
    }

    @GetMapping("/rateable-users")
    @Operation(summary = "获取可评分人员列表")
    public CommonResult<List<Map<String, Object>>> getRateableUsers() {
        return success(oaWorkPerformanceService.getRateableUsers());
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出OA工作绩效评分 Excel")
    public void exportOaWorkPerformanceExcel(@Valid OaWorkPerformanceExportReqVO exportReqVO,
            HttpServletResponse response) throws IOException {
        List<OaWorkPerformanceDO> list = oaWorkPerformanceService.getOaWorkPerformanceList(exportReqVO);
        // 导出 Excel
        List<OaWorkPerformanceRespVO> datas = BeanUtils.toBean(list, OaWorkPerformanceRespVO.class);
        ExcelUtils.write(response, "OA工作绩效评分.xls", "数据", OaWorkPerformanceRespVO.class,
                datas);
    }

}



