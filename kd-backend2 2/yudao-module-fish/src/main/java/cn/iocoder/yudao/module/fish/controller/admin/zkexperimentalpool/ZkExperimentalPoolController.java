package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpool;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpool.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpool.ZkExperimentalPoolDO;
import cn.iocoder.yudao.module.fish.service.zkexperimentalpool.ZkExperimentalPoolService;

@Tag(name = "管理后台 - 实验鱼池")
@RestController
@RequestMapping("/fish/zk-experimental-pool")
@Validated
public class ZkExperimentalPoolController {

    @Resource
    private ZkExperimentalPoolService zkExperimentalPoolService;

    @PostMapping("/create")
    @Operation(summary = "创建实验鱼池")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-pool:create')")
    public CommonResult<Long> createZkExperimentalPool(@Valid @RequestBody ZkExperimentalPoolSaveReqVO createReqVO) {
        return success(zkExperimentalPoolService.createZkExperimentalPool(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新实验鱼池")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-pool:update')")
    public CommonResult<Boolean> updateZkExperimentalPool(@Valid @RequestBody ZkExperimentalPoolSaveReqVO updateReqVO) {
        zkExperimentalPoolService.updateZkExperimentalPool(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除实验鱼池")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-pool:delete')")
    public CommonResult<Boolean> deleteZkExperimentalPool(@RequestParam("id") Long id) {
        zkExperimentalPoolService.deleteZkExperimentalPool(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除实验鱼池")
                @PreAuthorize("@ss.hasPermission('fish:zk-experimental-pool:delete')")
    public CommonResult<Boolean> deleteZkExperimentalPoolList(@RequestParam("ids") List<Long> ids) {
        zkExperimentalPoolService.deleteZkExperimentalPoolListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得实验鱼池")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-pool:query')")
    public CommonResult<ZkExperimentalPoolRespVO> getZkExperimentalPool(@RequestParam("id") Long id) {
        ZkExperimentalPoolDO zkExperimentalPool = zkExperimentalPoolService.getZkExperimentalPool(id);
        return success(BeanUtils.toBean(zkExperimentalPool, ZkExperimentalPoolRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得实验鱼池分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-pool:query')")
    public CommonResult<PageResult<ZkExperimentalPoolRespVO>> getZkExperimentalPoolPage(@Valid ZkExperimentalPoolPageReqVO pageReqVO) {
        PageResult<ZkExperimentalPoolDO> pageResult = zkExperimentalPoolService.getZkExperimentalPoolPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkExperimentalPoolRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出实验鱼池 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-pool:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkExperimentalPoolExcel(@Valid ZkExperimentalPoolPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkExperimentalPoolDO> list = zkExperimentalPoolService.getZkExperimentalPoolPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "实验鱼池.xls", "数据", ZkExperimentalPoolRespVO.class,
                        BeanUtils.toBean(list, ZkExperimentalPoolRespVO.class));
    }

}