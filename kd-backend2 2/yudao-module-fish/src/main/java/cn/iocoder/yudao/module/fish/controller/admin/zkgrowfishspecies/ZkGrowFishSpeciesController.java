package cn.iocoder.yudao.module.fish.controller.admin.zkgrowfishspecies;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkgrowfishspecies.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfishspecies.ZkGrowFishSpeciesDO;
import cn.iocoder.yudao.module.fish.service.zkgrowfishspecies.ZkGrowFishSpeciesService;

@Tag(name = "管理后台 - 鱼类品种")
@RestController
@RequestMapping("/fish/zk-grow-fish-species")
@Validated
public class ZkGrowFishSpeciesController {

    @Resource
    private ZkGrowFishSpeciesService zkGrowFishSpeciesService;

    @PostMapping("/create")
    @Operation(summary = "创建鱼类品种")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-fish-species:create')")
    public CommonResult<Long> createZkGrowFishSpecies(@Valid @RequestBody ZkGrowFishSpeciesSaveReqVO createReqVO) {
        return success(zkGrowFishSpeciesService.createZkGrowFishSpecies(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新鱼类品种")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-fish-species:update')")
    public CommonResult<Boolean> updateZkGrowFishSpecies(@Valid @RequestBody ZkGrowFishSpeciesSaveReqVO updateReqVO) {
        zkGrowFishSpeciesService.updateZkGrowFishSpecies(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除鱼类品种")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-fish-species:delete')")
    public CommonResult<Boolean> deleteZkGrowFishSpecies(@RequestParam("id") Long id) {
        zkGrowFishSpeciesService.deleteZkGrowFishSpecies(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除鱼类品种")
                @PreAuthorize("@ss.hasPermission('fish:zk-grow-fish-species:delete')")
    public CommonResult<Boolean> deleteZkGrowFishSpeciesList(@RequestParam("ids") List<Long> ids) {
        zkGrowFishSpeciesService.deleteZkGrowFishSpeciesListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得鱼类品种")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-fish-species:query')")
    public CommonResult<ZkGrowFishSpeciesRespVO> getZkGrowFishSpecies(@RequestParam("id") Long id) {
        ZkGrowFishSpeciesDO zkGrowFishSpecies = zkGrowFishSpeciesService.getZkGrowFishSpecies(id);
        return success(BeanUtils.toBean(zkGrowFishSpecies, ZkGrowFishSpeciesRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得鱼类品种分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-fish-species:query')")
    public CommonResult<PageResult<ZkGrowFishSpeciesRespVO>> getZkGrowFishSpeciesPage(@Valid ZkGrowFishSpeciesPageReqVO pageReqVO) {
        PageResult<ZkGrowFishSpeciesDO> pageResult = zkGrowFishSpeciesService.getZkGrowFishSpeciesPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkGrowFishSpeciesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出鱼类品种 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-fish-species:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkGrowFishSpeciesExcel(@Valid ZkGrowFishSpeciesPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkGrowFishSpeciesDO> list = zkGrowFishSpeciesService.getZkGrowFishSpeciesPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "鱼类品种.xls", "数据", ZkGrowFishSpeciesRespVO.class,
                        BeanUtils.toBean(list, ZkGrowFishSpeciesRespVO.class));
    }

}