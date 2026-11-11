package cn.iocoder.yudao.module.fish.controller.admin.zkbreedspecies;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkbreedspecies.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbreedspecies.ZkBreedSpeciesDO;
import cn.iocoder.yudao.module.fish.service.zkbreedspecies.ZkBreedSpeciesService;

@Tag(name = "管理后台 - 养殖品种")
@RestController
@RequestMapping("/fish/zk-breed-species")
@Validated
public class ZkBreedSpeciesController {

    @Resource
    private ZkBreedSpeciesService zkBreedSpeciesService;

    @PostMapping("/create")
    @Operation(summary = "创建养殖品种")
    @PreAuthorize("@ss.hasPermission('fish:zk-breed-species:create')")
    public CommonResult<Long> createZkBreedSpecies(@Valid @RequestBody ZkBreedSpeciesSaveReqVO createReqVO) {
        return success(zkBreedSpeciesService.createZkBreedSpecies(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新养殖品种")
    @PreAuthorize("@ss.hasPermission('fish:zk-breed-species:update')")
    public CommonResult<Boolean> updateZkBreedSpecies(@Valid @RequestBody ZkBreedSpeciesSaveReqVO updateReqVO) {
        zkBreedSpeciesService.updateZkBreedSpecies(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除养殖品种")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-breed-species:delete')")
    public CommonResult<Boolean> deleteZkBreedSpecies(@RequestParam("id") Long id) {
        zkBreedSpeciesService.deleteZkBreedSpecies(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除养殖品种")
                @PreAuthorize("@ss.hasPermission('fish:zk-breed-species:delete')")
    public CommonResult<Boolean> deleteZkBreedSpeciesList(@RequestParam("ids") List<Long> ids) {
        zkBreedSpeciesService.deleteZkBreedSpeciesListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得养殖品种")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-breed-species:query')")
    public CommonResult<ZkBreedSpeciesRespVO> getZkBreedSpecies(@RequestParam("id") Long id) {
        ZkBreedSpeciesDO zkBreedSpecies = zkBreedSpeciesService.getZkBreedSpecies(id);
        return success(BeanUtils.toBean(zkBreedSpecies, ZkBreedSpeciesRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得养殖品种分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-breed-species:query')")
    public CommonResult<PageResult<ZkBreedSpeciesRespVO>> getZkBreedSpeciesPage(@Valid ZkBreedSpeciesPageReqVO pageReqVO) {
        PageResult<ZkBreedSpeciesDO> pageResult = zkBreedSpeciesService.getZkBreedSpeciesPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkBreedSpeciesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出养殖品种 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-breed-species:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkBreedSpeciesExcel(@Valid ZkBreedSpeciesPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkBreedSpeciesDO> list = zkBreedSpeciesService.getZkBreedSpeciesPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "养殖品种.xls", "数据", ZkBreedSpeciesRespVO.class,
                        BeanUtils.toBean(list, ZkBreedSpeciesRespVO.class));
    }

}