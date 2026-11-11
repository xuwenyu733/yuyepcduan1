package cn.iocoder.yudao.module.fish.controller.admin.zkgrowpool;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkgrowpool.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowpool.ZkGrowPoolDO;
import cn.iocoder.yudao.module.fish.service.zkgrowpool.ZkGrowPoolService;

@Tag(name = "管理后台 - 生长模型鱼池")
@RestController
@RequestMapping("/fish/zk-grow-pool")
@Validated
public class ZkGrowPoolController {

    @Resource
    private ZkGrowPoolService zkGrowPoolService;

    @PostMapping("/create")
    @Operation(summary = "创建生长模型鱼池")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-pool:create')")
    public CommonResult<Long> createZkGrowPool(@Valid @RequestBody ZkGrowPoolSaveReqVO createReqVO) {
        return success(zkGrowPoolService.createZkGrowPool(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新生长模型鱼池")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-pool:update')")
    public CommonResult<Boolean> updateZkGrowPool(@Valid @RequestBody ZkGrowPoolSaveReqVO updateReqVO) {
        zkGrowPoolService.updateZkGrowPool(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除生长模型鱼池")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-pool:delete')")
    public CommonResult<Boolean> deleteZkGrowPool(@RequestParam("id") Long id) {
        zkGrowPoolService.deleteZkGrowPool(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除生长模型鱼池")
                @PreAuthorize("@ss.hasPermission('fish:zk-grow-pool:delete')")
    public CommonResult<Boolean> deleteZkGrowPoolList(@RequestParam("ids") List<Long> ids) {
        zkGrowPoolService.deleteZkGrowPoolListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得生长模型鱼池")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-pool:query')")
    public CommonResult<ZkGrowPoolRespVO> getZkGrowPool(@RequestParam("id") Long id) {
        ZkGrowPoolDO zkGrowPool = zkGrowPoolService.getZkGrowPool(id);
        return success(BeanUtils.toBean(zkGrowPool, ZkGrowPoolRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得生长模型鱼池分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-pool:query')")
    public CommonResult<PageResult<ZkGrowPoolRespVO>> getZkGrowPoolPage(@Valid ZkGrowPoolPageReqVO pageReqVO) {
        PageResult<ZkGrowPoolDO> pageResult = zkGrowPoolService.getZkGrowPoolPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkGrowPoolRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出生长模型鱼池 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-pool:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkGrowPoolExcel(@Valid ZkGrowPoolPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkGrowPoolDO> list = zkGrowPoolService.getZkGrowPoolPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "生长模型鱼池.xls", "数据", ZkGrowPoolRespVO.class,
                        BeanUtils.toBean(list, ZkGrowPoolRespVO.class));
    }

}