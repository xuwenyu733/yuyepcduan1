package cn.iocoder.yudao.module.fish.controller.admin.zkworkplan;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkworkplan.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkworkplan.ZkWorkPlanDO;
// import cn.iocoder.yudao.module.fish.convert.zkworkplan.ZkWorkPlanConvert;
import cn.iocoder.yudao.module.fish.service.zkworkplan.ZkWorkPlanService;

@Tag(name = "管理后台 - 工作目标与计划")
@RestController
@RequestMapping("/fish/zk-work-plan")
@Validated
public class ZkWorkPlanController {

    @Resource
    private ZkWorkPlanService zkWorkPlanService;

    @PostMapping("/create")
    @Operation(summary = "创建工作目标与计划")
    public CommonResult<Long> createZkWorkPlan(@Valid @RequestBody ZkWorkPlanCreateReqVO createReqVO) {
        return success(zkWorkPlanService.createZkWorkPlan(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新工作目标与计划")
    public CommonResult<Boolean> updateZkWorkPlan(@Valid @RequestBody ZkWorkPlanUpdateReqVO updateReqVO) {
        zkWorkPlanService.updateZkWorkPlan(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工作目标与计划")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkWorkPlan(@RequestParam("id") Long id) {
        zkWorkPlanService.deleteZkWorkPlan(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得工作目标与计划")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkWorkPlanRespVO> getZkWorkPlan(@RequestParam("id") Long id) {
        ZkWorkPlanDO zkWorkPlan = zkWorkPlanService.getZkWorkPlan(id);
        return success(BeanUtils.toBean(zkWorkPlan, ZkWorkPlanRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得工作目标与计划列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkWorkPlanRespVO>> getZkWorkPlanList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkWorkPlanDO> list = zkWorkPlanService.getZkWorkPlanList(ids);
        return success(BeanUtils.toBean(list, ZkWorkPlanRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得工作目标与计划分页")
    public CommonResult<PageResult<ZkWorkPlanRespVO>> getZkWorkPlanPage(@Valid ZkWorkPlanPageReqVO pageVO) {
        PageResult<ZkWorkPlanDO> pageResult = zkWorkPlanService.getZkWorkPlanPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkWorkPlanRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出工作目标与计划 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkWorkPlanExcel(@Valid ZkWorkPlanExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkWorkPlanDO> list = zkWorkPlanService.getZkWorkPlanList(exportReqVO);
        // 导出 Excel
        List<ZkWorkPlanExcelVO> datas = BeanUtils.toBean(list, ZkWorkPlanExcelVO.class);
        ExcelUtils.write(response, "工作目标与计划.xls", "数据", ZkWorkPlanExcelVO.class, datas);
    }

}
