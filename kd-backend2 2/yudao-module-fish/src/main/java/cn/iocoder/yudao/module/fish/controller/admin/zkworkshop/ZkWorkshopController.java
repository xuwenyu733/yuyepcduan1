package cn.iocoder.yudao.module.fish.controller.admin.zkworkshop;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkworkshop.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkworkshop.ZkWorkshopDO;
// import cn.iocoder.yudao.module.fish.convert.zkworkshop.ZkWorkshopConvert;
import cn.iocoder.yudao.module.fish.service.zkworkshop.ZkWorkshopService;

@Tag(name = "管理后台 - 车间")
@RestController
@RequestMapping("/fish/zk-workshop")
@Validated
public class ZkWorkshopController {

    @Resource
    private ZkWorkshopService zkWorkshopService;

    @Resource
    private BaseDeptService baseDeptService;

    @PostMapping("/create")
    @Operation(summary = "创建车间")
    public CommonResult<Long> createZkWorkshop(@Valid @RequestBody ZkWorkshopCreateReqVO createReqVO) {
        return success(zkWorkshopService.createZkWorkshop(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新车间")
    public CommonResult<Boolean> updateZkWorkshop(@Valid @RequestBody ZkWorkshopUpdateReqVO updateReqVO) {
        zkWorkshopService.updateZkWorkshop(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除车间")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pl:zk-workshop:delete')")
    public CommonResult<Boolean> deleteZkWorkshop(@RequestParam("id") Long id) {
        zkWorkshopService.deleteZkWorkshop(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得车间")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkWorkshopRespVO> getZkWorkshop(@RequestParam("id") Long id) {
        ZkWorkshopDO zkWorkshop = zkWorkshopService.getZkWorkshop(id);
        return success(BeanUtils.toBean(zkWorkshop, ZkWorkshopRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得车间列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkWorkshopRespVO>> getZkWorkshopList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkWorkshopDO> list = zkWorkshopService.getZkWorkshopList(ids);
        return success(BeanUtils.toBean(list, ZkWorkshopRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得车间分页")
    public CommonResult<PageResult<ZkWorkshopRespVO>> getZkWorkshopPage(@Valid ZkWorkshopPageReqVO pageVO) {
        PageResult<ZkWorkshopDO> pageResult = zkWorkshopService.getZkWorkshopPage(pageVO);
        // 补充部门名称
        baseDeptService.listEnhancerDeptName(pageResult.getList(), Optional.of("deptId"), Optional.of("deptName"));
        return success(BeanUtils.toBean(pageResult, ZkWorkshopRespVO.class));
    }

    @GetMapping("/list-for-dashboard")
    @Operation(summary = "获得车间列表，用于首页大屏")
    public CommonResult<List<ZkWorkshopDashboardRespVO>> getZkWorkshopDashboardList() {
        return success(zkWorkshopService.getZkWorkshopDashboardList());
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出车间 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkWorkshopExcel(@Valid ZkWorkshopExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkWorkshopDO> list = zkWorkshopService.getZkWorkshopList(exportReqVO);
        // 导出 Excel
        List<ZkWorkshopExcelVO> datas = BeanUtils.toBean(list, ZkWorkshopExcelVO.class);
        ExcelUtils.write(response, "车间.xls", "数据", ZkWorkshopExcelVO.class, datas);
    }

}
