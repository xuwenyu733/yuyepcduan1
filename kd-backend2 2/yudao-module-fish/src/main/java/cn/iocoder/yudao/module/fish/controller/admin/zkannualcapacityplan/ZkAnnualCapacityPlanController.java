package cn.iocoder.yudao.module.fish.controller.admin.zkannualcapacityplan;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkannualcapacityplan.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkannualcapacityplan.ZkAnnualCapacityPlanDO;
import cn.iocoder.yudao.module.fish.service.zkannualcapacityplan.ZkAnnualCapacityPlanService;

@Tag(name = "管理后台 - 年度产能计划")
@RestController
@RequestMapping("/fish/zk-annual-capacity-plan")
@Validated
public class ZkAnnualCapacityPlanController {

    @Resource
    private ZkAnnualCapacityPlanService zkAnnualCapacityPlanService;

    @PostMapping("/create")
    @Operation(summary = "创建年度产能计划")
    @PreAuthorize("@ss.hasPermission('fish:zk-annual-capacity-plan:create')")
    public CommonResult<Long> createZkAnnualCapacityPlan(@Valid @RequestBody ZkAnnualCapacityPlanSaveReqVO createReqVO) {
        return success(zkAnnualCapacityPlanService.createZkAnnualCapacityPlan(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新年度产能计划")
    @PreAuthorize("@ss.hasPermission('fish:zk-annual-capacity-plan:update')")
    public CommonResult<Boolean> updateZkAnnualCapacityPlan(@Valid @RequestBody ZkAnnualCapacityPlanSaveReqVO updateReqVO) {
        zkAnnualCapacityPlanService.updateZkAnnualCapacityPlan(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除年度产能计划")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-annual-capacity-plan:delete')")
    public CommonResult<Boolean> deleteZkAnnualCapacityPlan(@RequestParam("id") Long id) {
        zkAnnualCapacityPlanService.deleteZkAnnualCapacityPlan(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除年度产能计划")
                @PreAuthorize("@ss.hasPermission('fish:zk-annual-capacity-plan:delete')")
    public CommonResult<Boolean> deleteZkAnnualCapacityPlanList(@RequestParam("ids") List<Long> ids) {
        zkAnnualCapacityPlanService.deleteZkAnnualCapacityPlanListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得年度产能计划")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-annual-capacity-plan:query')")
    public CommonResult<ZkAnnualCapacityPlanRespVO> getZkAnnualCapacityPlan(@RequestParam("id") Long id) {
        ZkAnnualCapacityPlanDO zkAnnualCapacityPlan = zkAnnualCapacityPlanService.getZkAnnualCapacityPlan(id);
        return success(BeanUtils.toBean(zkAnnualCapacityPlan, ZkAnnualCapacityPlanRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得年度产能计划分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-annual-capacity-plan:query')")
    public CommonResult<PageResult<ZkAnnualCapacityPlanRespVO>> getZkAnnualCapacityPlanPage(@Valid ZkAnnualCapacityPlanPageReqVO pageReqVO) {
        PageResult<ZkAnnualCapacityPlanDO> pageResult = zkAnnualCapacityPlanService.getZkAnnualCapacityPlanPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkAnnualCapacityPlanRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出年度产能计划 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-annual-capacity-plan:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkAnnualCapacityPlanExcel(@Valid ZkAnnualCapacityPlanPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkAnnualCapacityPlanDO> list = zkAnnualCapacityPlanService.getZkAnnualCapacityPlanPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "年度产能计划.xls", "数据", ZkAnnualCapacityPlanRespVO.class,
                        BeanUtils.toBean(list, ZkAnnualCapacityPlanRespVO.class));
    }

}