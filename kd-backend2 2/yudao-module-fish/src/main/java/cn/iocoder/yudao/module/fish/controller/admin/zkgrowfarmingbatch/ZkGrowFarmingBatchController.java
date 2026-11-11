package cn.iocoder.yudao.module.fish.controller.admin.zkgrowfarmingbatch;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfishspecies.ZkGrowFishSpeciesDO;
import cn.iocoder.yudao.module.fish.service.zkgrowfishspecies.ZkGrowFishSpeciesService;
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
import java.util.stream.Collectors;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkgrowfarmingbatch.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfarmingbatch.ZkGrowFarmingBatchDO;
import cn.iocoder.yudao.module.fish.service.zkgrowfarmingbatch.ZkGrowFarmingBatchService;

@Tag(name = "管理后台 - 养殖批次")
@RestController
@RequestMapping("/fish/zk-grow-farming-batch")
@Validated
public class ZkGrowFarmingBatchController {

    @Resource
    private ZkGrowFarmingBatchService zkGrowFarmingBatchService;

    @Resource
    private ZkGrowFishSpeciesService zkGrowFishSpeciesService;

    @PostMapping("/create")
    @Operation(summary = "创建养殖批次")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-farming-batch:create')")
    public CommonResult<Long> createZkGrowFarmingBatch(@Valid @RequestBody ZkGrowFarmingBatchSaveReqVO createReqVO) {
        return success(zkGrowFarmingBatchService.createZkGrowFarmingBatch(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新养殖批次")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-farming-batch:update')")
    public CommonResult<Boolean> updateZkGrowFarmingBatch(@Valid @RequestBody ZkGrowFarmingBatchSaveReqVO updateReqVO) {
        zkGrowFarmingBatchService.updateZkGrowFarmingBatch(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除养殖批次")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-farming-batch:delete')")
    public CommonResult<Boolean> deleteZkGrowFarmingBatch(@RequestParam("id") Long id) {
        zkGrowFarmingBatchService.deleteZkGrowFarmingBatch(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除养殖批次")
                @PreAuthorize("@ss.hasPermission('fish:zk-grow-farming-batch:delete')")
    public CommonResult<Boolean> deleteZkGrowFarmingBatchList(@RequestParam("ids") List<Long> ids) {
        zkGrowFarmingBatchService.deleteZkGrowFarmingBatchListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得养殖批次")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-farming-batch:query')")
    public CommonResult<ZkGrowFarmingBatchRespVO> getZkGrowFarmingBatch(@RequestParam("id") Long id) {
        ZkGrowFarmingBatchDO zkGrowFarmingBatch = zkGrowFarmingBatchService.getZkGrowFarmingBatch(id);
        enhancerList(List.of(zkGrowFarmingBatch));
        return success(BeanUtils.toBean(zkGrowFarmingBatch, ZkGrowFarmingBatchRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得养殖批次分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-farming-batch:query')")
    public CommonResult<PageResult<ZkGrowFarmingBatchRespVO>> getZkGrowFarmingBatchPage(@Valid ZkGrowFarmingBatchPageReqVO pageReqVO) {
        PageResult<ZkGrowFarmingBatchDO> pageResult = zkGrowFarmingBatchService.getZkGrowFarmingBatchPage(pageReqVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkGrowFarmingBatchRespVO.class));
    }

    public void enhancerList(List<ZkGrowFarmingBatchDO> tList) {
        if(EasyListUtil.isEmpty(tList)) return;
        // 1. 获取品种
        Set<Long> speciesIds = tList.stream().map(ZkGrowFarmingBatchDO::getSpeciesId).collect(Collectors.toSet());
        List<ZkGrowFishSpeciesDO> oList= zkGrowFishSpeciesService.getByIds(speciesIds);
        // 2. 赋值
        CommonUtil.origins2Targets4(oList, ZkGrowFishSpeciesDO::getId, tList, ZkGrowFarmingBatchDO::getSpeciesId, (ZkGrowFishSpeciesDO o, ZkGrowFarmingBatchDO t) -> {
            t.setScientificName(o.getScientificName());
        });
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出养殖批次 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-farming-batch:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkGrowFarmingBatchExcel(@Valid ZkGrowFarmingBatchPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkGrowFarmingBatchDO> list = zkGrowFarmingBatchService.getZkGrowFarmingBatchPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "养殖批次.xls", "数据", ZkGrowFarmingBatchRespVO.class,
                        BeanUtils.toBean(list, ZkGrowFarmingBatchRespVO.class));
    }

}