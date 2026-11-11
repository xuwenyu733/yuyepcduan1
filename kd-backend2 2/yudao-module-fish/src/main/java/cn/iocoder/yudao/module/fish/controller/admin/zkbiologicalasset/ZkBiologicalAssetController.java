package cn.iocoder.yudao.module.fish.controller.admin.zkbiologicalasset;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkbiologicalasset.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologicalasset.ZkBiologicalAssetDO;
// import cn.iocoder.yudao.module.fish.convert.zkbiologicalasset.ZkBiologicalAssetConvert;
import cn.iocoder.yudao.module.fish.service.zkbiologicalasset.ZkBiologicalAssetService;

@Tag(name = "管理后台 - 生物资产评估")
@RestController
@RequestMapping("/fish/zk-biological-asset")
@Validated
public class ZkBiologicalAssetController {

    @Resource
    private ZkBiologicalAssetService zkBiologicalAssetService;

    @PostMapping("/create")
    @Operation(summary = "创建生物资产评估")
    public CommonResult<Long> createZkBiologicalAsset(@Valid @RequestBody ZkBiologicalAssetCreateReqVO createReqVO) {
        return success(zkBiologicalAssetService.createZkBiologicalAsset(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新生物资产评估")
    public CommonResult<Boolean> updateZkBiologicalAsset(@Valid @RequestBody ZkBiologicalAssetUpdateReqVO updateReqVO) {
        zkBiologicalAssetService.updateZkBiologicalAsset(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除生物资产评估")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkBiologicalAsset(@RequestParam("id") Long id) {
        zkBiologicalAssetService.deleteZkBiologicalAsset(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得生物资产评估")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkBiologicalAssetRespVO> getZkBiologicalAsset(@RequestParam("id") Long id) {
        ZkBiologicalAssetDO zkBiologicalAsset = zkBiologicalAssetService.getZkBiologicalAsset(id);
        return success(BeanUtils.toBean(zkBiologicalAsset, ZkBiologicalAssetRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得生物资产评估列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkBiologicalAssetRespVO>> getZkBiologicalAssetList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkBiologicalAssetDO> list = zkBiologicalAssetService.getZkBiologicalAssetList(ids);
        return success(BeanUtils.toBean(list, ZkBiologicalAssetRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得生物资产评估分页")
    public CommonResult<PageResult<ZkBiologicalAssetRespVO>> getZkBiologicalAssetPage(@Valid ZkBiologicalAssetPageReqVO pageVO) {
        PageResult<ZkBiologicalAssetDO> pageResult = zkBiologicalAssetService.getZkBiologicalAssetPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkBiologicalAssetRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出生物资产评估 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkBiologicalAssetExcel(@Valid ZkBiologicalAssetExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkBiologicalAssetDO> list = zkBiologicalAssetService.getZkBiologicalAssetList(exportReqVO);
        // 导出 Excel
        List<ZkBiologicalAssetExcelVO> datas = BeanUtils.toBean(list, ZkBiologicalAssetExcelVO.class);
        ExcelUtils.write(response, "生物资产评估.xls", "数据", ZkBiologicalAssetExcelVO.class, datas);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "获得生物资产评估仪表盘数据")
    @Parameter(name = "startDate", description = "统计周期起 yyyy-MM-dd")
    @Parameter(name = "endDate", description = "统计周期止/盘点日 yyyy-MM-dd，取 ≤ 该日的最近日快照")
    public CommonResult<ZkBiologicalAssetDashboardRespVO> getDashboardData(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        return success(zkBiologicalAssetService.getDashboardData(startDate, endDate));
    }

}
