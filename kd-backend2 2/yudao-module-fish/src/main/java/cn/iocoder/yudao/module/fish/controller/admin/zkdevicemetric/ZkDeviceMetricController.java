package cn.iocoder.yudao.module.fish.controller.admin.zkdevicemetric;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkdevicemetric.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicemetric.ZkDeviceMetricDO;
// import cn.iocoder.yudao.module.fish.convert.zkdevicemetric.ZkDeviceMetricConvert;
import cn.iocoder.yudao.module.fish.service.zkdevicemetric.ZkDeviceMetricService;

@Tag(name = "管理后台 - 设备指标")
@RestController
@RequestMapping("/fish/zk-device-metric")
@Validated
public class ZkDeviceMetricController {

    @Resource
    private ZkDeviceMetricService zkDeviceMetricService;

    @PostMapping("/create")
    @Operation(summary = "创建设备指标")
    public CommonResult<Long> createZkDeviceMetric(@Valid @RequestBody ZkDeviceMetricCreateReqVO createReqVO) {
        return success(zkDeviceMetricService.createZkDeviceMetric(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新设备指标")
    public CommonResult<Boolean> updateZkDeviceMetric(@Valid @RequestBody ZkDeviceMetricUpdateReqVO updateReqVO) {
        zkDeviceMetricService.updateZkDeviceMetric(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设备指标")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkDeviceMetric(@RequestParam("id") Long id) {
        zkDeviceMetricService.deleteZkDeviceMetric(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得设备指标")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkDeviceMetricRespVO> getZkDeviceMetric(@RequestParam("id") Long id) {
        ZkDeviceMetricDO zkDeviceMetric = zkDeviceMetricService.getZkDeviceMetric(id);
        return success(BeanUtils.toBean(zkDeviceMetric, ZkDeviceMetricRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得设备指标列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkDeviceMetricRespVO>> getZkDeviceMetricList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkDeviceMetricDO> list = zkDeviceMetricService.getZkDeviceMetricList(ids);
        return success(BeanUtils.toBean(list, ZkDeviceMetricRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得设备指标分页")
    public CommonResult<PageResult<ZkDeviceMetricRespVO>> getZkDeviceMetricPage(@Valid ZkDeviceMetricPageReqVO pageVO) {
        PageResult<ZkDeviceMetricDO> pageResult = zkDeviceMetricService.getZkDeviceMetricPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkDeviceMetricRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出设备指标 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkDeviceMetricExcel(@Valid ZkDeviceMetricExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkDeviceMetricDO> list = zkDeviceMetricService.getZkDeviceMetricList(exportReqVO);
        // 导出 Excel
        List<ZkDeviceMetricExcelVO> datas = BeanUtils.toBean(list, ZkDeviceMetricExcelVO.class);
        ExcelUtils.write(response, "设备指标.xls", "数据", ZkDeviceMetricExcelVO.class, datas);
    }

}
