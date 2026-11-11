package cn.iocoder.yudao.module.fish.controller.admin.zkschedulingtype;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkschedulingtype.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulingtype.ZkSchedulingTypeDO;
// import cn.iocoder.yudao.module.fish.convert.zkschedulingtype.ZkSchedulingTypeConvert;
import cn.iocoder.yudao.module.fish.service.zkschedulingtype.ZkSchedulingTypeService;

@Tag(name = "管理后台 - 检测配置")
@RestController
@RequestMapping("/fish/zk-scheduling-type")
@Validated
public class ZkSchedulingTypeController {

    @Resource
    private ZkSchedulingTypeService zkSchedulingTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建检测配置")
    public CommonResult<Long> createZkSchedulingType(@Valid @RequestBody ZkSchedulingTypeCreateReqVO createReqVO) {
        return success(zkSchedulingTypeService.createZkSchedulingType(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新检测配置")
    public CommonResult<Boolean> updateZkSchedulingType(@Valid @RequestBody ZkSchedulingTypeUpdateReqVO updateReqVO) {
        zkSchedulingTypeService.updateZkSchedulingType(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除检测配置")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkSchedulingType(@RequestParam("id") Long id) {
        zkSchedulingTypeService.deleteZkSchedulingType(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得检测配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkSchedulingTypeRespVO> getZkSchedulingType(@RequestParam("id") Long id) {
        ZkSchedulingTypeDO zkSchedulingType = zkSchedulingTypeService.getZkSchedulingType(id);
        return success(BeanUtils.toBean(zkSchedulingType, ZkSchedulingTypeRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得检测配置列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkSchedulingTypeRespVO>> getZkSchedulingTypeList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkSchedulingTypeDO> list = zkSchedulingTypeService.getZkSchedulingTypeList(ids);
        return success(BeanUtils.toBean(list, ZkSchedulingTypeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得检测配置分页")
    public CommonResult<PageResult<ZkSchedulingTypeRespVO>> getZkSchedulingTypePage(@Valid ZkSchedulingTypePageReqVO pageVO) {
        PageResult<ZkSchedulingTypeDO> pageResult = zkSchedulingTypeService.getZkSchedulingTypePage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkSchedulingTypeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出检测配置 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkSchedulingTypeExcel(@Valid ZkSchedulingTypeExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkSchedulingTypeDO> list = zkSchedulingTypeService.getZkSchedulingTypeList(exportReqVO);
        // 导出 Excel
        List<ZkSchedulingTypeExcelVO> datas = BeanUtils.toBean(list, ZkSchedulingTypeExcelVO.class);
        ExcelUtils.write(response, "检测配置.xls", "数据", ZkSchedulingTypeExcelVO.class, datas);
    }

}
