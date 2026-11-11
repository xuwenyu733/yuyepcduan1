package cn.iocoder.yudao.module.fish.controller.admin.zkgrowmodelconfig;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkgrowmodelconfig.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowmodelconfig.ZkGrowModelConfigDO;
import cn.iocoder.yudao.module.fish.service.zkgrowmodelconfig.ZkGrowModelConfigService;

@Tag(name = "管理后台 - 生长模型配置")
@RestController
@RequestMapping("/fish/zk-grow-model-config")
@Validated
public class ZkGrowModelConfigController {

    @Resource
    private ZkGrowModelConfigService zkGrowModelConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建生长模型配置")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-model-config:create')")
    public CommonResult<Long> createZkGrowModelConfig(@Valid @RequestBody ZkGrowModelConfigSaveReqVO createReqVO) {
        return success(zkGrowModelConfigService.createZkGrowModelConfig(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新生长模型配置")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-model-config:update')")
    public CommonResult<Boolean> updateZkGrowModelConfig(@Valid @RequestBody ZkGrowModelConfigSaveReqVO updateReqVO) {
        zkGrowModelConfigService.updateZkGrowModelConfig(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除生长模型配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-model-config:delete')")
    public CommonResult<Boolean> deleteZkGrowModelConfig(@RequestParam("id") Long id) {
        zkGrowModelConfigService.deleteZkGrowModelConfig(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除生长模型配置")
                @PreAuthorize("@ss.hasPermission('fish:zk-grow-model-config:delete')")
    public CommonResult<Boolean> deleteZkGrowModelConfigList(@RequestParam("ids") List<Long> ids) {
        zkGrowModelConfigService.deleteZkGrowModelConfigListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得生长模型配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-model-config:query')")
    public CommonResult<ZkGrowModelConfigRespVO> getZkGrowModelConfig(@RequestParam("id") Long id) {
        ZkGrowModelConfigDO zkGrowModelConfig = zkGrowModelConfigService.getZkGrowModelConfig(id);
        return success(BeanUtils.toBean(zkGrowModelConfig, ZkGrowModelConfigRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得生长模型配置分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-model-config:query')")
    public CommonResult<PageResult<ZkGrowModelConfigRespVO>> getZkGrowModelConfigPage(@Valid ZkGrowModelConfigPageReqVO pageReqVO) {
        PageResult<ZkGrowModelConfigDO> pageResult = zkGrowModelConfigService.getZkGrowModelConfigPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkGrowModelConfigRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出生长模型配置 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-model-config:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkGrowModelConfigExcel(@Valid ZkGrowModelConfigPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkGrowModelConfigDO> list = zkGrowModelConfigService.getZkGrowModelConfigPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "生长模型配置.xls", "数据", ZkGrowModelConfigRespVO.class,
                        BeanUtils.toBean(list, ZkGrowModelConfigRespVO.class));
    }

}