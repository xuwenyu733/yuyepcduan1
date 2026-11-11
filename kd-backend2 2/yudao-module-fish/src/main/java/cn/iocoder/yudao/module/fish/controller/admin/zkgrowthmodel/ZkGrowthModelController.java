package cn.iocoder.yudao.module.fish.controller.admin.zkgrowthmodel;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkgrowthmodel.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowthmodel.ZkGrowthModelDO;
// import cn.iocoder.yudao.module.fish.convert.zkgrowthmodel.ZkGrowthModelConvert;
import cn.iocoder.yudao.module.fish.service.zkgrowthmodel.ZkGrowthModelService;

@Tag(name = "管理后台 - 生长模型")
@RestController
@RequestMapping("/fish/zk-growth-model")
@Validated
public class ZkGrowthModelController {

    @Resource
    private ZkGrowthModelService zkGrowthModelService;

    @PostMapping("/create")
    @Operation(summary = "创建生长模型")
    public CommonResult<Long> createZkGrowthModel(@Valid @RequestBody ZkGrowthModelCreateReqVO createReqVO) {
        return success(zkGrowthModelService.createZkGrowthModel(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新生长模型")
    public CommonResult<Boolean> updateZkGrowthModel(@Valid @RequestBody ZkGrowthModelUpdateReqVO updateReqVO) {
        zkGrowthModelService.updateZkGrowthModel(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除生长模型")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkGrowthModel(@RequestParam("id") Long id) {
        zkGrowthModelService.deleteZkGrowthModel(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得生长模型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkGrowthModelRespVO> getZkGrowthModel(@RequestParam("id") Long id) {
        ZkGrowthModelDO zkGrowthModel = zkGrowthModelService.getZkGrowthModel(id);
        return success(BeanUtils.toBean(zkGrowthModel, ZkGrowthModelRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得生长模型列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkGrowthModelRespVO>> getZkGrowthModelList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkGrowthModelDO> list = zkGrowthModelService.getZkGrowthModelList(ids);
        return success(BeanUtils.toBean(list, ZkGrowthModelRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得生长模型分页")
    public CommonResult<PageResult<ZkGrowthModelRespVO>> getZkGrowthModelPage(@Valid ZkGrowthModelPageReqVO pageVO) {
        PageResult<ZkGrowthModelDO> pageResult = zkGrowthModelService.getZkGrowthModelPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkGrowthModelRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出生长模型 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkGrowthModelExcel(@Valid ZkGrowthModelExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkGrowthModelDO> list = zkGrowthModelService.getZkGrowthModelList(exportReqVO);
        // 导出 Excel
        List<ZkGrowthModelExcelVO> datas = BeanUtils.toBean(list, ZkGrowthModelExcelVO.class);
        ExcelUtils.write(response, "生长模型.xls", "数据", ZkGrowthModelExcelVO.class, datas);
    }

}
