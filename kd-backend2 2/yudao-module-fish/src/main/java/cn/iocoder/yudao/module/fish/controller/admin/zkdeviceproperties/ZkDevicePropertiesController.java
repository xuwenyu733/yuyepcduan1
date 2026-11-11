package cn.iocoder.yudao.module.fish.controller.admin.zkdeviceproperties;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkdeviceproperties.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeviceproperties.ZkDevicePropertiesDO;
// import cn.iocoder.yudao.module.fish.convert.zkdeviceproperties.ZkDevicePropertiesConvert;
import cn.iocoder.yudao.module.fish.service.zkdeviceproperties.ZkDevicePropertiesService;

@Tag(name = "管理后台 - 设备属性记录")
@RestController
@RequestMapping("/fish/zk-device-properties")
@Validated
public class ZkDevicePropertiesController {

    @Resource
    private ZkDevicePropertiesService zkDevicePropertiesService;

    @PostMapping("/create")
    @Operation(summary = "创建设备属性记录")
    public CommonResult<Long> createZkDeviceProperties(@Valid @RequestBody ZkDevicePropertiesCreateReqVO createReqVO) {
        return success(zkDevicePropertiesService.createZkDeviceProperties(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新设备属性记录")
    public CommonResult<Boolean> updateZkDeviceProperties(@Valid @RequestBody ZkDevicePropertiesUpdateReqVO updateReqVO) {
        zkDevicePropertiesService.updateZkDeviceProperties(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设备属性记录")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkDeviceProperties(@RequestParam("id") Long id) {
        zkDevicePropertiesService.deleteZkDeviceProperties(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得设备属性记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkDevicePropertiesRespVO> getZkDeviceProperties(@RequestParam("id") Long id) {
        ZkDevicePropertiesDO zkDeviceProperties = zkDevicePropertiesService.getZkDeviceProperties(id);
        return success(BeanUtils.toBean(zkDeviceProperties, ZkDevicePropertiesRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得设备属性记录列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkDevicePropertiesRespVO>> getZkDevicePropertiesList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkDevicePropertiesDO> list = zkDevicePropertiesService.getZkDevicePropertiesList(ids);
        return success(BeanUtils.toBean(list, ZkDevicePropertiesRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得设备属性记录分页")
    public CommonResult<PageResult<ZkDevicePropertiesRespVO>> getZkDevicePropertiesPage(@Valid ZkDevicePropertiesPageReqVO pageVO) {
        PageResult<ZkDevicePropertiesDO> pageResult = zkDevicePropertiesService.getZkDevicePropertiesPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkDevicePropertiesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出设备属性记录 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkDevicePropertiesExcel(@Valid ZkDevicePropertiesExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkDevicePropertiesDO> list = zkDevicePropertiesService.getZkDevicePropertiesList(exportReqVO);
        // 导出 Excel
        List<ZkDevicePropertiesExcelVO> datas = BeanUtils.toBean(list, ZkDevicePropertiesExcelVO.class);
        ExcelUtils.write(response, "设备属性记录.xls", "数据", ZkDevicePropertiesExcelVO.class, datas);
    }

}
