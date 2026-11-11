package cn.iocoder.yudao.module.fish.controller.admin.zkpurifytreatment;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkpurifytreatment.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifytreatment.PurifyTreatmentDO;
import cn.iocoder.yudao.module.fish.service.zkpurifytreatment.PurifyTreatmentService;

@Tag(name = "管理后台 - 处理措施记录")
@RestController
@RequestMapping("/fish/purify-treatment")
@Validated
public class PurifyTreatmentController {

    @Resource
    private PurifyTreatmentService purifyTreatmentService;

    @Resource
    private PurifyProcessMapper purifyProcessMapper;

    @PostMapping("/create")
    @Operation(summary = "创建处理措施记录")
    @PreAuthorize("@ss.hasPermission('fish:purify-treatment:create')")
    public CommonResult<Long> createPurifyTreatment(@Valid @RequestBody PurifyTreatmentSaveReqVO createReqVO) {
        return success(purifyTreatmentService.createPurifyTreatment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新处理措施记录")
    @PreAuthorize("@ss.hasPermission('fish:purify-treatment:update')")
    public CommonResult<Boolean> updatePurifyTreatment(@Valid @RequestBody PurifyTreatmentSaveReqVO updateReqVO) {
        purifyTreatmentService.updatePurifyTreatment(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除处理措施记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:purify-treatment:delete')")
    public CommonResult<Boolean> deletePurifyTreatment(@RequestParam("id") Long id) {
        purifyTreatmentService.deletePurifyTreatment(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除处理措施记录")
                @PreAuthorize("@ss.hasPermission('fish:purify-treatment:delete')")
    public CommonResult<Boolean> deletePurifyTreatmentList(@RequestParam("ids") List<Long> ids) {
        purifyTreatmentService.deletePurifyTreatmentListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得处理措施记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:purify-treatment:query')")
    public CommonResult<PurifyTreatmentRespVO> getPurifyTreatment(@RequestParam("id") Long id) {
        PurifyTreatmentDO purifyTreatment = purifyTreatmentService.getPurifyTreatment(id);
        enhancerList(List.of(purifyTreatment));
        return success(BeanUtils.toBean(purifyTreatment, PurifyTreatmentRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得处理措施记录分页")
    @PreAuthorize("@ss.hasPermission('fish:purify-treatment:query')")
    public CommonResult<PageResult<PurifyTreatmentRespVO>> getPurifyTreatmentPage(@Valid PurifyTreatmentPageReqVO pageReqVO) {
        PageResult<PurifyTreatmentDO> pageResult = purifyTreatmentService.getPurifyTreatmentPage(pageReqVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, PurifyTreatmentRespVO.class));
    }

    // 用 lambda汇总
    @SuppressWarnings("unchecked")
    public void enhancerList(List<PurifyTreatmentDO> tList) {
        // 1. 获取 关联 值
        Set<Long> oIds = EasyListUtil.listToIds(tList, PurifyTreatmentDO::getProcessId);
        // 2. 利用 关联值 查询
        List<PurifyProcessDO> oList = EasyDataMapper.listByValueList(purifyProcessMapper, PurifyProcessDO::getId, oIds);
        // 3. 赋值
        CommonUtil.origins2Targets4(oList, PurifyProcessDO::getId, EasyListUtil.getList(PurifyProcessDO::getRecordNumber),
                tList, PurifyTreatmentDO::getProcessId, EasyListUtil.setList(PurifyTreatmentDO::setRecordNumber));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出处理措施记录 Excel")
    @PreAuthorize("@ss.hasPermission('fish:purify-treatment:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPurifyTreatmentExcel(@Valid PurifyTreatmentPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PurifyTreatmentDO> list = purifyTreatmentService.getPurifyTreatmentPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "处理措施记录.xls", "数据", PurifyTreatmentRespVO.class,
                        BeanUtils.toBean(list, PurifyTreatmentRespVO.class));
    }

}