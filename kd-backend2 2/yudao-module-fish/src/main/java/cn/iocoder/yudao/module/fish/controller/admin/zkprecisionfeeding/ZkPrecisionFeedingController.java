package cn.iocoder.yudao.module.fish.controller.admin.zkprecisionfeeding;

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
// import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;
// import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;

import cn.iocoder.yudao.module.fish.controller.admin.zkprecisionfeeding.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkprecisionfeeding.ZkPrecisionFeedingDO;
// import cn.iocoder.yudao.module.fish.convert.zkprecisionfeeding.ZkPrecisionFeedingConvert;
import cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.ZkPrecisionFeedingService;
import cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.ZkPrecisionFeedingBizService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.util.FishFrontParamHelper;

import java.time.LocalDate;

@Tag(name = "管理后台 - 精准投喂")
@RestController
@RequestMapping("/fish/zk-precision-feeding")
@Validated
public class ZkPrecisionFeedingController {

    @Resource
    private ZkPrecisionFeedingService zkPrecisionFeedingService;

    @Resource
    private ZkPrecisionFeedingBizService zkPrecisionFeedingBizService;

    @Resource
    private ZkPondService zkPondService;

    @PostMapping("/create")
    @Operation(summary = "创建精准投喂")
    public CommonResult<Long> createZkPrecisionFeeding(@Valid @RequestBody ZkPrecisionFeedingCreateReqVO createReqVO) {
        return success(zkPrecisionFeedingService.createZkPrecisionFeeding(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新精准投喂")
    public CommonResult<Boolean> updateZkPrecisionFeeding(@Valid @RequestBody ZkPrecisionFeedingUpdateReqVO updateReqVO) {
        zkPrecisionFeedingService.updateZkPrecisionFeeding(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除精准投喂")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkPrecisionFeeding(@RequestParam("id") Long id) {
        zkPrecisionFeedingService.deleteZkPrecisionFeeding(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得精准投喂")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkPrecisionFeedingRespVO> getZkPrecisionFeeding(@RequestParam("id") Long id) {
        ZkPrecisionFeedingDO zkPrecisionFeeding = zkPrecisionFeedingService.getZkPrecisionFeeding(id);
        return success(BeanUtils.toBean(zkPrecisionFeeding, ZkPrecisionFeedingRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得精准投喂列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkPrecisionFeedingRespVO>> getZkPrecisionFeedingList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkPrecisionFeedingDO> list = zkPrecisionFeedingService.getZkPrecisionFeedingList(ids);
        return success(BeanUtils.toBean(list, ZkPrecisionFeedingRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得精准投喂分页")
    public CommonResult<PageResult<ZkPrecisionFeedingRespVO>> getZkPrecisionFeedingPage(@Valid ZkPrecisionFeedingPageReqVO pageVO) {
        PageResult<ZkPrecisionFeedingDO> pageResult = zkPrecisionFeedingService.getZkPrecisionFeedingPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkPrecisionFeedingRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出精准投喂 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkPrecisionFeedingExcel(@Valid ZkPrecisionFeedingExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkPrecisionFeedingDO> list = zkPrecisionFeedingService.getZkPrecisionFeedingList(exportReqVO);
        // 导出 Excel
        List<ZkPrecisionFeedingExcelVO> datas = BeanUtils.toBean(list, ZkPrecisionFeedingExcelVO.class);
        ExcelUtils.write(response, "精准投喂.xls", "数据", ZkPrecisionFeedingExcelVO.class, datas);
    }

    @GetMapping("/recommend")
    @Operation(summary = "计算鱼池智能投喂推荐量 Q日")
    public CommonResult<PrecisionFeedRecommendRespVO> recommend(@RequestParam("pondId") String pondId) {
        return success(zkPrecisionFeedingBizService.recommend(FishFrontParamHelper.resolvePondId(pondId, zkPondService)));
    }

    @GetMapping("/plans")
    @Operation(summary = "查询智能/实际投喂计划")
    public CommonResult<List<PrecisionFeedPlanVO>> listPlans(@RequestParam("pondId") String pondId,
                                                             @RequestParam(value = "planDate", required = false) LocalDate planDate) {
        return success(zkPrecisionFeedingBizService.listPlans(
                FishFrontParamHelper.resolvePondId(pondId, zkPondService), planDate));
    }

    @PostMapping("/plan/save")
    @Operation(summary = "保存智能或实际投喂计划（草稿）")
    public CommonResult<PrecisionFeedPlanVO> savePlan(@Valid @RequestBody PrecisionFeedPlanVO plan) {
        if (plan.getPondId() == null && plan.getPondDetailNum() != null) {
            plan.setPondId(FishFrontParamHelper.resolvePondId(plan.getPondDetailNum(), zkPondService));
        }
        if (plan.getPondId() == null) {
            throw new IllegalArgumentException("pondId 不能为空（可传 pondId 或 pondDetailNum）");
        }
        return success(zkPrecisionFeedingBizService.savePlan(plan));
    }

    @PostMapping("/plan/confirm")
    @Operation(summary = "确认实施投喂计划（实际计划会写入投喂记录）")
    public CommonResult<PrecisionFeedPlanVO> confirmPlan(@RequestParam("pondId") String pondId,
                                                         @RequestParam(value = "planType", defaultValue = "actual") String planType,
                                                         @RequestParam(value = "planDate", required = false) LocalDate planDate) {
        return success(zkPrecisionFeedingBizService.confirmPlan(
                FishFrontParamHelper.resolvePondId(pondId, zkPondService), planType, planDate));
    }

    @GetMapping("/feed-consumption")
    @Operation(summary = "饲料消耗汇总")
    public CommonResult<PrecisionFeedConsumptionRespVO> feedConsumption(
            @RequestParam(value = "workshopId", required = false) Long workshopId,
            @RequestParam(value = "pondSystemId", required = false) Long pondSystemId,
            @RequestParam(value = "pondId", required = false) String pondId,
            @RequestParam(value = "beginDate", required = false) LocalDate beginDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate) {
        Long resolvedPondId = pondId != null ? FishFrontParamHelper.resolvePondId(pondId, zkPondService) : null;
        return success(zkPrecisionFeedingBizService.feedConsumption(workshopId, pondSystemId, resolvedPondId, beginDate, endDate));
    }

    @GetMapping("/ai-analysis")
    @Operation(summary = "投喂 AI 智能分析（计划偏差）")
    public CommonResult<PrecisionFeedAiAnalysisRespVO> aiAnalysis(@RequestParam("pondId") String pondId) {
        return success(zkPrecisionFeedingBizService.aiAnalysis(
                FishFrontParamHelper.resolvePondId(pondId, zkPondService)));
    }

    @GetMapping("/affecting-tasks")
    @Operation(summary = "影响投喂的任务列表")
    public CommonResult<List<PrecisionFeedAffectingTaskVO>> affectingTasks(@RequestParam("pondId") String pondId) {
        return success(zkPrecisionFeedingBizService.affectingTasks(
                FishFrontParamHelper.resolvePondId(pondId, zkPondService)));
    }

}
