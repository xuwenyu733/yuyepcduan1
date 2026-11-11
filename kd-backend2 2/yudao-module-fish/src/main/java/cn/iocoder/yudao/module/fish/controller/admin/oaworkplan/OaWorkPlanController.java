package cn.iocoder.yudao.module.fish.controller.admin.oaworkplan;

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

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.controller.admin.oaworkplan.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.oaworkplan.OaWorkPlanDO;
import cn.iocoder.yudao.module.fish.service.oaworkplan.OaWorkPlanService;

@Tag(name = "管理后台 - OA工作计划")
@RestController
@RequestMapping("/fish/oa-work-plan")
@Validated
public class OaWorkPlanController {

    @Resource
    private OaWorkPlanService oaWorkPlanService;

    @PostMapping("/create")
    @Operation(summary = "创建OA工作计划")
    public CommonResult<Long> createOaWorkPlan(@Valid @RequestBody OaWorkPlanCreateReqVO createReqVO) {
        return success(oaWorkPlanService.createOaWorkPlan(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新OA工作计划")
    public CommonResult<Boolean> updateOaWorkPlan(@Valid @RequestBody OaWorkPlanUpdateReqVO updateReqVO) {
        oaWorkPlanService.updateOaWorkPlan(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除OA工作计划")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteOaWorkPlan(@RequestParam("id") Long id) {
        oaWorkPlanService.deleteOaWorkPlan(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得OA工作计划")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<OaWorkPlanRespVO> getOaWorkPlan(@RequestParam("id") Long id) {
        OaWorkPlanDO oaWorkPlan = oaWorkPlanService.getOaWorkPlan(id);
        return success(BeanUtils.toBean(oaWorkPlan, OaWorkPlanRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得OA工作计划列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<OaWorkPlanRespVO>> getOaWorkPlanList(@RequestParam("ids") Collection<Long> ids) {
        List<OaWorkPlanDO> list = oaWorkPlanService.getOaWorkPlanList(ids);
        return success(BeanUtils.toBean(list, OaWorkPlanRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得OA工作计划分页")
    public CommonResult<PageResult<OaWorkPlanRespVO>> getOaWorkPlanPage(@Valid OaWorkPlanPageReqVO pageVO) {
        PageResult<OaWorkPlanDO> pageResult = oaWorkPlanService.getOaWorkPlanPage(pageVO);
        return success(BeanUtils.toBean(pageResult, OaWorkPlanRespVO.class));
    }

    @PostMapping("/review")
    @Operation(summary = "审核OA工作计划")
    public CommonResult<Boolean> reviewOaWorkPlan(@Valid @RequestBody OaWorkPlanReviewReqVO reviewReqVO) {
        oaWorkPlanService.reviewOaWorkPlan(reviewReqVO);
        return success(true);
    }

    @PostMapping("/complete")
    @Operation(summary = "更新OA工作计划完成情况")
    public CommonResult<Boolean> completeOaWorkPlan(@Valid @RequestBody OaWorkPlanCompleteReqVO completeReqVO) {
        oaWorkPlanService.completeOaWorkPlan(completeReqVO);
        return success(true);
    }

    @PostMapping("/evaluate")
    @Operation(summary = "评价OA工作计划")
    public CommonResult<Boolean> evaluateOaWorkPlan(@Valid @RequestBody OaWorkPlanEvaluationReqVO evaluationReqVO) {
        oaWorkPlanService.evaluateOaWorkPlan(evaluationReqVO);
        return success(true);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获得OA工作计划统计")
    public CommonResult<OaWorkPlanStatisticsVO> getOaWorkPlanStatistics() {
        return success(oaWorkPlanService.getOaWorkPlanStatistics());
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出OA工作计划 Excel")
    public void exportOaWorkPlanExcel(@Valid OaWorkPlanExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<OaWorkPlanDO> list = oaWorkPlanService.getOaWorkPlanList(exportReqVO);
        // 导出 Excel
        // List<OaWorkPlanExcelVO> datas = BeanUtils.toBean(list, OaWorkPlanExcelVO.class);
        // ExcelUtils.write(response, "OA工作计划.xls", "数据", OaWorkPlanExcelVO.class, datas);
    }

}

