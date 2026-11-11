package cn.iocoder.yudao.module.fish.controller.admin.evaluation;

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

import cn.iocoder.yudao.module.fish.controller.admin.evaluation.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.evaluation.OaEvaluationDO;
import cn.iocoder.yudao.module.fish.service.evaluation.OaEvaluationService;

@Tag(name = "管理后台 - OA考核评分")
@RestController
@RequestMapping("/fish/oa-evaluation")
@Validated
public class OaEvaluationController {

    @Resource
    private OaEvaluationService oaEvaluationService;

    @GetMapping("/get")
    @Operation(summary = "获得OA考核评分")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<OaEvaluationRespVO> getOaEvaluation(@RequestParam("id") Long id) {
        OaEvaluationDO evaluation = oaEvaluationService.getOaEvaluation(id);
        return success(BeanUtils.toBean(evaluation, OaEvaluationRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得OA考核评分分页")
    public CommonResult<PageResult<OaEvaluationRespVO>> getOaEvaluationPage(@Valid OaEvaluationPageReqVO pageReqVO) {
        PageResult<OaEvaluationDO> pageResult = oaEvaluationService.getOaEvaluationPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, OaEvaluationRespVO.class));
    }

    @GetMapping("/personal")
    @Operation(summary = "获取个人考核数据")
    @Parameter(name = "period", description = "考核期（格式：yyyy-MM）", example = "2024-12")
    public CommonResult<Map<String, Object>> getPersonalEvaluation(@RequestParam(value = "period", required = false) String period) {
        if (period == null || period.isEmpty()) {
            // 默认使用当前月份
            java.time.LocalDate now = java.time.LocalDate.now();
            period = java.time.YearMonth.from(now).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        return success(oaEvaluationService.getPersonalEvaluation(period));
    }

    @GetMapping("/personal-trend")
    @Operation(summary = "获取个人考核趋势数据（近6个月）")
    public CommonResult<List<Map<String, Object>>> getPersonalEvaluationTrend() {
        return success(oaEvaluationService.getPersonalEvaluationTrend());
    }

    @GetMapping("/personal-summary")
    @Operation(summary = "获取个人考核统计摘要")
    public CommonResult<Map<String, Object>> getPersonalEvaluationSummary() {
        return success(oaEvaluationService.getPersonalEvaluationSummary());
    }

    @GetMapping("/department-ranking")
    @Operation(summary = "获取部门排名数据")
    @Parameter(name = "period", description = "考核期（格式：yyyy-MM）", example = "2024-12")
    public CommonResult<List<Map<String, Object>>> getDepartmentRanking(@RequestParam(value = "period", required = false) String period) {
        if (period == null || period.isEmpty()) {
            // 默认使用当前月份
            java.time.LocalDate now = java.time.LocalDate.now();
            period = java.time.YearMonth.from(now).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        return success(oaEvaluationService.getDepartmentRanking(period));
    }

    @GetMapping("/history")
    @Operation(summary = "获取历史考核记录")
    public CommonResult<List<Map<String, Object>>> getEvaluationHistory(@RequestParam(value = "periods", required = false) List<String> periods) {
        Map<String, Object> params = new HashMap<>();
        params.put("periods", periods != null ? periods : Collections.emptyList());
        return success(oaEvaluationService.getEvaluationHistory(params));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出OA考核评分 Excel")
    public void exportOaEvaluationExcel(@Valid OaEvaluationExportReqVO exportReqVO,
            HttpServletResponse response) throws IOException {
        List<OaEvaluationDO> list = oaEvaluationService.getOaEvaluationList(exportReqVO);
        // 导出 Excel
        List<OaEvaluationRespVO> datas = BeanUtils.toBean(list, OaEvaluationRespVO.class);
        ExcelUtils.write(response, "OA考核评分.xls", "数据", OaEvaluationRespVO.class,
                datas);
    }

    @GetMapping("/dimension-weights")
    @Operation(summary = "获取考核维度满分配置（对齐OA考核指标文档）")
    public CommonResult<Map<String, Object>> getDimensionWeights() {
        return success(oaEvaluationService.getDimensionWeights());
    }

    @PostMapping("/calculate")
    @Operation(summary = "手动触发考核评分计算（并发送站内信结果通知）")
    @Parameter(name = "period", description = "考核期 yyyy-MM，默认上月", example = "2026-06")
    public CommonResult<Boolean> calculateEvaluation(@RequestParam(value = "period", required = false) String period) {
        if (period == null || period.isEmpty()) {
            java.time.YearMonth lastMonth = java.time.YearMonth.now().minusMonths(1);
            period = lastMonth.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        oaEvaluationService.calculateEvaluation(period);
        return success(true);
    }

    @PostMapping("/practice-exam")
    @Operation(summary = "录入实操考试得分（0-5），保留评分接口")
    public CommonResult<Boolean> setPracticeExamScore(@RequestBody Map<String, Object> body) {
        Long userId = body.get("userId") != null ? Long.valueOf(String.valueOf(body.get("userId"))) : null;
        String period = body.get("period") != null ? String.valueOf(body.get("period")) : null;
        java.math.BigDecimal score = body.get("score") != null
            ? new java.math.BigDecimal(String.valueOf(body.get("score"))) : java.math.BigDecimal.ZERO;
        if (userId == null || period == null || period.isEmpty()) {
            throw new IllegalArgumentException("userId、period 必填");
        }
        oaEvaluationService.setPracticeExamScore(userId, period, score);
        return success(true);
    }

}



