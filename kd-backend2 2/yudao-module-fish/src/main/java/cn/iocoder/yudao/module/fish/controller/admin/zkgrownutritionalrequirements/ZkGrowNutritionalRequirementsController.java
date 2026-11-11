package cn.iocoder.yudao.module.fish.controller.admin.zkgrownutritionalrequirements;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkgrownutritionalrequirements.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrownutritionalrequirements.ZkGrowNutritionalRequirementsDO;
import cn.iocoder.yudao.module.fish.service.zkgrownutritionalrequirements.ZkGrowNutritionalRequirementsService;

@Tag(name = "管理后台 - 各阶段营养需求")
@RestController
@RequestMapping("/fish/zk-grow-nutritional-requirements")
@Validated
public class ZkGrowNutritionalRequirementsController {

    @Resource
    private ZkGrowNutritionalRequirementsService zkGrowNutritionalRequirementsService;

    @PostMapping("/create")
    @Operation(summary = "创建各阶段营养需求")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-nutritional-requirements:create')")
    public CommonResult<Long> createZkGrowNutritionalRequirements(@Valid @RequestBody ZkGrowNutritionalRequirementsSaveReqVO createReqVO) {
        return success(zkGrowNutritionalRequirementsService.createZkGrowNutritionalRequirements(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新各阶段营养需求")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-nutritional-requirements:update')")
    public CommonResult<Boolean> updateZkGrowNutritionalRequirements(@Valid @RequestBody ZkGrowNutritionalRequirementsSaveReqVO updateReqVO) {
        zkGrowNutritionalRequirementsService.updateZkGrowNutritionalRequirements(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除各阶段营养需求")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-nutritional-requirements:delete')")
    public CommonResult<Boolean> deleteZkGrowNutritionalRequirements(@RequestParam("id") Long id) {
        zkGrowNutritionalRequirementsService.deleteZkGrowNutritionalRequirements(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除各阶段营养需求")
                @PreAuthorize("@ss.hasPermission('fish:zk-grow-nutritional-requirements:delete')")
    public CommonResult<Boolean> deleteZkGrowNutritionalRequirementsList(@RequestParam("ids") List<Long> ids) {
        zkGrowNutritionalRequirementsService.deleteZkGrowNutritionalRequirementsListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得各阶段营养需求")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-nutritional-requirements:query')")
    public CommonResult<ZkGrowNutritionalRequirementsRespVO> getZkGrowNutritionalRequirements(@RequestParam("id") Long id) {
        ZkGrowNutritionalRequirementsDO zkGrowNutritionalRequirements = zkGrowNutritionalRequirementsService.getZkGrowNutritionalRequirements(id);
        return success(BeanUtils.toBean(zkGrowNutritionalRequirements, ZkGrowNutritionalRequirementsRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得各阶段营养需求分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-nutritional-requirements:query')")
    public CommonResult<PageResult<ZkGrowNutritionalRequirementsRespVO>> getZkGrowNutritionalRequirementsPage(@Valid ZkGrowNutritionalRequirementsPageReqVO pageReqVO) {
        PageResult<ZkGrowNutritionalRequirementsDO> pageResult = zkGrowNutritionalRequirementsService.getZkGrowNutritionalRequirementsPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkGrowNutritionalRequirementsRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出各阶段营养需求 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-nutritional-requirements:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkGrowNutritionalRequirementsExcel(@Valid ZkGrowNutritionalRequirementsPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkGrowNutritionalRequirementsDO> list = zkGrowNutritionalRequirementsService.getZkGrowNutritionalRequirementsPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "各阶段营养需求.xls", "数据", ZkGrowNutritionalRequirementsRespVO.class,
                        BeanUtils.toBean(list, ZkGrowNutritionalRequirementsRespVO.class));
    }

}