package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaltemplate;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaltemplate.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaltemplate.ZkExperimentalTemplateDO;
import cn.iocoder.yudao.module.fish.service.zkexperimentaltemplate.ZkExperimentalTemplateService;

@Tag(name = "管理后台 - 实验模板")
@RestController
@RequestMapping("/fish/zk-experimental-template")
@Validated
public class ZkExperimentalTemplateController {

    @Resource
    private ZkExperimentalTemplateService zkExperimentalTemplateService;

    @GetMapping({"/types", "/templates/types"})
    @Operation(summary = "获得实验模板类型列表")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-template:query')")
    public CommonResult<List<ZkExperimentalTemplateTypeRespVO>> getTemplateTypes() {
        return success(zkExperimentalTemplateService.getTemplateTypes());
    }

    @PostMapping("/create")
    @Operation(summary = "创建实验模板")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-template:create')")
    public CommonResult<Long> createZkExperimentalTemplate(@Valid @RequestBody ZkExperimentalTemplateSaveReqVO createReqVO) {
        return success(zkExperimentalTemplateService.createZkExperimentalTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新实验模板")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-template:update')")
    public CommonResult<Boolean> updateZkExperimentalTemplate(@Valid @RequestBody ZkExperimentalTemplateSaveReqVO updateReqVO) {
        zkExperimentalTemplateService.updateZkExperimentalTemplate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除实验模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-template:delete')")
    public CommonResult<Boolean> deleteZkExperimentalTemplate(@RequestParam("id") Long id) {
        zkExperimentalTemplateService.deleteZkExperimentalTemplate(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除实验模板")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-template:delete')")
    public CommonResult<Boolean> deleteZkExperimentalTemplateList(@RequestParam("ids") List<Long> ids) {
        zkExperimentalTemplateService.deleteZkExperimentalTemplateListByIds(ids);
        return success(true);
    }

    @PostMapping({"/copy", "/{id}/copy"})
    @Operation(summary = "复制实验模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-template:create')")
    public CommonResult<Long> copyZkExperimentalTemplate(@PathVariable(value = "id", required = false) Long pathId,
                                                         @RequestParam(value = "id", required = false) Long id) {
        Long templateId = pathId != null ? pathId : id;
        return success(zkExperimentalTemplateService.copyZkExperimentalTemplate(templateId));
    }

    @GetMapping("/get")
    @Operation(summary = "获得实验模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-template:query')")
    public CommonResult<ZkExperimentalTemplateRespVO> getZkExperimentalTemplate(@RequestParam("id") Long id) {
        ZkExperimentalTemplateDO template = zkExperimentalTemplateService.getZkExperimentalTemplate(id);
        return success(BeanUtils.toBean(template, ZkExperimentalTemplateRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得实验模板分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-template:query')")
    public CommonResult<PageResult<ZkExperimentalTemplateRespVO>> getZkExperimentalTemplatePage(@Valid ZkExperimentalTemplatePageReqVO pageReqVO) {
        PageResult<ZkExperimentalTemplateDO> pageResult = zkExperimentalTemplateService.getZkExperimentalTemplatePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkExperimentalTemplateRespVO.class));
    }

    @GetMapping({"/list", "/templates"})
    @Operation(summary = "获得实验模板列表")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-template:query')")
    public CommonResult<List<ZkExperimentalTemplateRespVO>> getZkExperimentalTemplateList(@Valid ZkExperimentalTemplatePageReqVO pageReqVO) {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<ZkExperimentalTemplateDO> pageResult = zkExperimentalTemplateService.getZkExperimentalTemplatePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult.getList(), ZkExperimentalTemplateRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出实验模板 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-template:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkExperimentalTemplateExcel(@Valid ZkExperimentalTemplatePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkExperimentalTemplateDO> list = zkExperimentalTemplateService.getZkExperimentalTemplatePage(pageReqVO).getList();
        ExcelUtils.write(response, "实验模板.xls", "数据", ZkExperimentalTemplateRespVO.class,
                        BeanUtils.toBean(list, ZkExperimentalTemplateRespVO.class));
    }

}
