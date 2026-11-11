package cn.iocoder.yudao.module.fish.controller.admin.zkfcrmodel;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkfcrmodel.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfcrmodel.ZkFcrModelDO;
// import cn.iocoder.yudao.module.fish.convert.zkfcrmodel.ZkFcrModelConvert;
import cn.iocoder.yudao.module.fish.service.zkfcrmodel.ZkFcrModelService;

@Tag(name = "管理后台 - 饵料系数FCR模型")
@RestController
@RequestMapping("/fish/zk-fcr-model")
@Validated
public class ZkFcrModelController {

    @Resource
    private ZkFcrModelService zkFcrModelService;

    @PostMapping("/create")
    @Operation(summary = "创建饵料系数FCR模型")
    public CommonResult<Long> createZkFcrModel(@Valid @RequestBody ZkFcrModelCreateReqVO createReqVO) {
        return success(zkFcrModelService.createZkFcrModel(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新饵料系数FCR模型")
    public CommonResult<Boolean> updateZkFcrModel(@Valid @RequestBody ZkFcrModelUpdateReqVO updateReqVO) {
        zkFcrModelService.updateZkFcrModel(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除饵料系数FCR模型")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkFcrModel(@RequestParam("id") Long id) {
        zkFcrModelService.deleteZkFcrModel(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得饵料系数FCR模型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkFcrModelRespVO> getZkFcrModel(@RequestParam("id") Long id) {
        ZkFcrModelDO zkFcrModel = zkFcrModelService.getZkFcrModel(id);
        return success(BeanUtils.toBean(zkFcrModel, ZkFcrModelRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得饵料系数FCR模型列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkFcrModelRespVO>> getZkFcrModelList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkFcrModelDO> list = zkFcrModelService.getZkFcrModelList(ids);
        return success(BeanUtils.toBean(list, ZkFcrModelRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得饵料系数FCR模型分页")
    public CommonResult<PageResult<ZkFcrModelRespVO>> getZkFcrModelPage(@Valid ZkFcrModelPageReqVO pageVO) {
        PageResult<ZkFcrModelDO> pageResult = zkFcrModelService.getZkFcrModelPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkFcrModelRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出饵料系数FCR模型 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkFcrModelExcel(@Valid ZkFcrModelExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkFcrModelDO> list = zkFcrModelService.getZkFcrModelList(exportReqVO);
        // 导出 Excel
        List<ZkFcrModelExcelVO> datas = BeanUtils.toBean(list, ZkFcrModelExcelVO.class);
        ExcelUtils.write(response, "饵料系数FCR模型.xls", "数据", ZkFcrModelExcelVO.class, datas);
    }

}
