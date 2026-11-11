package cn.iocoder.yudao.module.fish.controller.admin.zkscatteredinventory;

import cn.iocoder.yudao.framework.common.base_api.fish.ZkScatteredInventoryControllerApi;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkscatteredinventory.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkscatteredinventory.ZkScatteredInventoryDO;
import cn.iocoder.yudao.module.fish.service.zkscatteredinventory.ZkScatteredInventoryService;

@Tag(name = "管理后台 - 零星库存")
@RestController
@RequestMapping("/fish/zk-scattered-inventory")
@Validated
public class ZkScatteredInventoryController implements ZkScatteredInventoryControllerApi {

    @Resource
    private ZkScatteredInventoryService zkScatteredInventoryService;

    @PostMapping("/create")
    @Operation(summary = "创建零星库存")
    @PreAuthorize("@ss.hasPermission('fish:zk-scattered-inventory:create')")
    public CommonResult<Long> createZkScatteredInventory(@Valid @RequestBody ZkScatteredInventorySaveReqVO createReqVO) {
        return success(zkScatteredInventoryService.createZkScatteredInventory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新零星库存")
    @PreAuthorize("@ss.hasPermission('fish:zk-scattered-inventory:update')")
    public CommonResult<Boolean> updateZkScatteredInventory(@Valid @RequestBody ZkScatteredInventorySaveReqVO updateReqVO) {
        zkScatteredInventoryService.updateZkScatteredInventory(updateReqVO);
        return success(true);
    }

    @Override
    public CommonResult<Long> createZkScatteredInventory(Map<String, Object> param) {
        ZkScatteredInventorySaveReqVO zkScatteredInventorySaveReqVO = JSON.parseObject(JSON.toJSONString(param), ZkScatteredInventorySaveReqVO.class);
        return createZkScatteredInventory(zkScatteredInventorySaveReqVO);
    }

    @Override
    public CommonResult<Boolean> deleteZkScatteredInventoryByBusId(Long busId) {
        // 1. 查询数据
        List<ZkScatteredInventoryDO> list = zkScatteredInventoryService.getByQuery(new QueryWrapper<>(new ZkScatteredInventoryDO().setBusId(busId)));
        if(EasyListUtil.isEmpty(list)) return CommonResult.success(true);
        // 2. 删除
        List<Long> collect = list.stream().map(ZkScatteredInventoryDO::getId).toList();
        return deleteZkScatteredInventoryList(collect);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除零星库存")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-scattered-inventory:delete')")
    public CommonResult<Boolean> deleteZkScatteredInventory(@RequestParam("id") Long id) {
        zkScatteredInventoryService.deleteZkScatteredInventory(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除零星库存")
                @PreAuthorize("@ss.hasPermission('fish:zk-scattered-inventory:delete')")
    public CommonResult<Boolean> deleteZkScatteredInventoryList(@RequestParam("ids") List<Long> ids) {
        zkScatteredInventoryService.deleteZkScatteredInventoryListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得零星库存")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-scattered-inventory:query')")
    public CommonResult<ZkScatteredInventoryRespVO> getZkScatteredInventory(@RequestParam("id") Long id) {
        ZkScatteredInventoryDO zkScatteredInventory = zkScatteredInventoryService.getZkScatteredInventory(id);
        return success(BeanUtils.toBean(zkScatteredInventory, ZkScatteredInventoryRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得零星库存分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-scattered-inventory:query')")
    public CommonResult<PageResult<ZkScatteredInventoryRespVO>> getZkScatteredInventoryPage(@Valid ZkScatteredInventoryPageReqVO pageReqVO) {
        PageResult<ZkScatteredInventoryDO> pageResult = zkScatteredInventoryService.getZkScatteredInventoryPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkScatteredInventoryRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出零星库存 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-scattered-inventory:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkScatteredInventoryExcel(@Valid ZkScatteredInventoryPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkScatteredInventoryDO> list = zkScatteredInventoryService.getZkScatteredInventoryPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "零星库存.xls", "数据", ZkScatteredInventoryRespVO.class,
                        BeanUtils.toBean(list, ZkScatteredInventoryRespVO.class));
    }

}