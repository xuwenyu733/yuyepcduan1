package cn.iocoder.yudao.module.fish.controller.admin.zkinspectionreport;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkinspectionreport.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkinspectionreport.ZkInspectionReportDO;
// import cn.iocoder.yudao.module.fish.convert.zkinspectionreport.ZkInspectionReportConvert;
import cn.iocoder.yudao.module.fish.service.zkinspectionreport.ZkInspectionReportService;

@Tag(name = "管理后台 - 巡检智能分析报告")
@RestController
@RequestMapping("/fish/zk-inspection-report")
@Validated
public class ZkInspectionReportController {

    @Resource
    private ZkInspectionReportService zkInspectionReportService;

    @PostMapping("/create")
    @Operation(summary = "创建巡检智能分析报告")
    public CommonResult<Long> createZkInspectionReport(@Valid @RequestBody ZkInspectionReportCreateReqVO createReqVO) {
        return success(zkInspectionReportService.createZkInspectionReport(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新巡检智能分析报告")
    public CommonResult<Boolean> updateZkInspectionReport(@Valid @RequestBody ZkInspectionReportUpdateReqVO updateReqVO) {
        zkInspectionReportService.updateZkInspectionReport(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除巡检智能分析报告")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkInspectionReport(@RequestParam("id") Long id) {
        zkInspectionReportService.deleteZkInspectionReport(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得巡检智能分析报告")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkInspectionReportRespVO> getZkInspectionReport(@RequestParam("id") Long id) {
        ZkInspectionReportDO zkInspectionReport = zkInspectionReportService.getZkInspectionReport(id);
        return success(BeanUtils.toBean(zkInspectionReport, ZkInspectionReportRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得巡检智能分析报告列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkInspectionReportRespVO>> getZkInspectionReportList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkInspectionReportDO> list = zkInspectionReportService.getZkInspectionReportList(ids);
        return success(BeanUtils.toBean(list, ZkInspectionReportRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得巡检智能分析报告分页")
    public CommonResult<PageResult<ZkInspectionReportRespVO>> getZkInspectionReportPage(@Valid ZkInspectionReportPageReqVO pageVO) {
        PageResult<ZkInspectionReportDO> pageResult = zkInspectionReportService.getZkInspectionReportPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkInspectionReportRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出巡检智能分析报告 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkInspectionReportExcel(@Valid ZkInspectionReportExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkInspectionReportDO> list = zkInspectionReportService.getZkInspectionReportList(exportReqVO);
        // 导出 Excel
        List<ZkInspectionReportExcelVO> datas = BeanUtils.toBean(list, ZkInspectionReportExcelVO.class);
        ExcelUtils.write(response, "巡检智能分析报告.xls", "数据", ZkInspectionReportExcelVO.class, datas);
    }

}
