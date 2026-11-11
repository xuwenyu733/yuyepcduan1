package cn.iocoder.yudao.module.fish.controller.admin.zkpondsystem;

import cn.iocoder.yudao.framework.common.base_api.BaseDeptService;
import org.springframework.security.access.prepost.PreAuthorize;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkpondsystem.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondsystem.ZkPondSystemDO;
// import cn.iocoder.yudao.module.fish.convert.zkpondsystem.ZkPondSystemConvert;
import cn.iocoder.yudao.module.fish.service.zkpondsystem.ZkPondSystemService;

@Tag(name = "管理后台 - 存塘系统")
@RestController
@RequestMapping("/fish/zk-pond-system")
@Validated
public class ZkPondSystemController {

    @Resource
    private ZkPondSystemService zkPondSystemService;

    @Resource
    private BaseDeptService baseDeptService;

    @PostMapping("/create")
    @Operation(summary = "创建存塘系统")
    public CommonResult<Long> createZkPondSystem(@Valid @RequestBody ZkPondSystemCreateReqVO createReqVO) {
        return success(zkPondSystemService.createZkPondSystem(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新存塘系统")
    public CommonResult<Boolean> updateZkPondSystem(@Valid @RequestBody ZkPondSystemUpdateReqVO updateReqVO) {
        zkPondSystemService.updateZkPondSystem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除存塘系统")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pl:zk-pond-system:delete')")
    public CommonResult<Boolean> deleteZkPondSystem(@RequestParam("id") Long id) {
        zkPondSystemService.deleteZkPondSystem(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得存塘系统")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkPondSystemRespVO> getZkPondSystem(@RequestParam("id") Long id) {
        ZkPondSystemDO zkPondSystem = zkPondSystemService.getZkPondSystem(id);
        return success(BeanUtils.toBean(zkPondSystem, ZkPondSystemRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得存塘系统列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkPondSystemRespVO>> getZkPondSystemList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkPondSystemDO> list = zkPondSystemService.getZkPondSystemList(ids);
        return success(BeanUtils.toBean(list, ZkPondSystemRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得存塘系统分页")
    public CommonResult<PageResult<ZkPondSystemRespVO>> getZkPondSystemPage(@Valid ZkPondSystemPageReqVO pageVO) {
        PageResult<ZkPondSystemDO> pageResult = zkPondSystemService.getZkPondSystemPage(pageVO);
        // 补充部门名称
        baseDeptService.listEnhancerDeptName(pageResult.getList(), Optional.of("deptId"), Optional.of("deptName"));
        return success(BeanUtils.toBean(pageResult, ZkPondSystemRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出存塘系统 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkPondSystemExcel(@Valid ZkPondSystemExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkPondSystemDO> list = zkPondSystemService.getZkPondSystemList(exportReqVO);
        // 导出 Excel
        List<ZkPondSystemExcelVO> datas = BeanUtils.toBean(list, ZkPondSystemExcelVO.class);
        ExcelUtils.write(response, "存塘系统.xls", "数据", ZkPondSystemExcelVO.class, datas);
    }

}
