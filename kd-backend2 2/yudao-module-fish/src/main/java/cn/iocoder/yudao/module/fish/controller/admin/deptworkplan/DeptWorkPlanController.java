package cn.iocoder.yudao.module.fish.controller.admin.deptworkplan;

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

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.controller.admin.deptworkplan.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.deptworkplan.DeptWorkPlanDO;
import cn.iocoder.yudao.module.fish.service.deptworkplan.DeptWorkPlanService;

@Tag(name = "管理后台 - 部门工作计划")
@RestController
@RequestMapping("/fish/dept-work-plan")
@Validated
public class DeptWorkPlanController {

    @Resource
    private DeptWorkPlanService deptWorkPlanService;

    @PostMapping("/create")
    @Operation(summary = "创建部门工作计划")
    public CommonResult<Long> createDeptWorkPlan(@Valid @RequestBody DeptWorkPlanCreateReqVO createReqVO) {
        return success(deptWorkPlanService.createDeptWorkPlan(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新部门工作计划")
    public CommonResult<Boolean> updateDeptWorkPlan(@Valid @RequestBody DeptWorkPlanUpdateReqVO updateReqVO) {
        deptWorkPlanService.updateDeptWorkPlan(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除部门工作计划")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteDeptWorkPlan(@RequestParam("id") Long id) {
        deptWorkPlanService.deleteDeptWorkPlan(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得部门工作计划")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<DeptWorkPlanRespVO> getDeptWorkPlan(@RequestParam("id") Long id) {
        DeptWorkPlanDO plan = deptWorkPlanService.getDeptWorkPlan(id);
        return success(BeanUtils.toBean(plan, DeptWorkPlanRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得部门工作计划列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<DeptWorkPlanRespVO>> getDeptWorkPlanList(@RequestParam("ids") Collection<Long> ids) {
        List<DeptWorkPlanDO> list = deptWorkPlanService.getDeptWorkPlanList(ids);
        return success(BeanUtils.toBean(list, DeptWorkPlanRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得部门工作计划分页")
    public CommonResult<PageResult<DeptWorkPlanRespVO>> getDeptWorkPlanPage(@Valid DeptWorkPlanPageReqVO pageVO) {
        PageResult<DeptWorkPlanDO> pageResult = deptWorkPlanService.getDeptWorkPlanPage(pageVO);
        return success(BeanUtils.toBean(pageResult, DeptWorkPlanRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出部门工作计划 Excel")
    public void exportDeptWorkPlanExcel(@Valid DeptWorkPlanExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<DeptWorkPlanDO> list = deptWorkPlanService.getDeptWorkPlanList(exportReqVO);
        // 导出 Excel
        // List<DeptWorkPlanExcelVO> datas = BeanUtils.toBean(list, DeptWorkPlanExcelVO.class);
        // ExcelUtils.write(response, "部门工作计划.xls", "数据", DeptWorkPlanExcelVO.class, datas);
    }
}

