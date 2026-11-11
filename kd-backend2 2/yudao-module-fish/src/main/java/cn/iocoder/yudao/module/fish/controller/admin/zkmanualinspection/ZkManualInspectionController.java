package cn.iocoder.yudao.module.fish.controller.admin.zkmanualinspection;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkmanualinspection.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmanualinspection.ZkManualInspectionDO;
// import cn.iocoder.yudao.module.fish.convert.zkmanualinspection.ZkManualInspectionConvert;
import cn.iocoder.yudao.module.fish.service.zkmanualinspection.ZkManualInspectionService;

@Tag(name = "管理后台 - 人工巡检")
@RestController
@RequestMapping("/fish/zk-manual-inspection")
@Validated
public class ZkManualInspectionController {

    @Resource
    private ZkManualInspectionService zkManualInspectionService;

    @PostMapping("/create")
    @Operation(summary = "创建人工巡检")
    public CommonResult<Long> createZkManualInspection(@Valid @RequestBody ZkManualInspectionCreateReqVO createReqVO) {
        return success(zkManualInspectionService.createZkManualInspection(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新人工巡检")
    public CommonResult<Boolean> updateZkManualInspection(@Valid @RequestBody ZkManualInspectionUpdateReqVO updateReqVO) {
        zkManualInspectionService.updateZkManualInspection(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除人工巡检")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkManualInspection(@RequestParam("id") Long id) {
        zkManualInspectionService.deleteZkManualInspection(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得人工巡检")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkManualInspectionRespVO> getZkManualInspection(@RequestParam("id") Long id) {
        ZkManualInspectionDO zkManualInspection = zkManualInspectionService.getZkManualInspection(id);
        return success(BeanUtils.toBean(zkManualInspection, ZkManualInspectionRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得人工巡检列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkManualInspectionRespVO>> getZkManualInspectionList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkManualInspectionDO> list = zkManualInspectionService.getZkManualInspectionList(ids);
        return success(BeanUtils.toBean(list, ZkManualInspectionRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得人工巡检分页")
    public CommonResult<PageResult<ZkManualInspectionRespVO>> getZkManualInspectionPage(@Valid ZkManualInspectionPageReqVO pageVO) {
        PageResult<ZkManualInspectionDO> pageResult = zkManualInspectionService.getZkManualInspectionPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkManualInspectionRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出人工巡检 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkManualInspectionExcel(@Valid ZkManualInspectionExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkManualInspectionDO> list = zkManualInspectionService.getZkManualInspectionList(exportReqVO);
        // 导出 Excel
        List<ZkManualInspectionExcelVO> datas = BeanUtils.toBean(list, ZkManualInspectionExcelVO.class);
        ExcelUtils.write(response, "人工巡检.xls", "数据", ZkManualInspectionExcelVO.class, datas);
    }

}
