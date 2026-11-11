package cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitingredients;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitingredients.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitingredients.ZkGrowBaitIngredientsDO;
import cn.iocoder.yudao.module.fish.service.zkgrowbaitingredients.ZkGrowBaitIngredientsService;

@Tag(name = "管理后台 - 饵料成分对比")
@RestController
@RequestMapping("/fish/zk-grow-bait-ingredients")
@Validated
public class ZkGrowBaitIngredientsController {

    @Resource
    private ZkGrowBaitIngredientsService zkGrowBaitIngredientsService;

    @PostMapping("/create")
    @Operation(summary = "创建饵料成分对比")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-bait-ingredients:create')")
    public CommonResult<Long> createZkGrowBaitIngredients(@Valid @RequestBody ZkGrowBaitIngredientsSaveReqVO createReqVO) {
        return success(zkGrowBaitIngredientsService.createZkGrowBaitIngredients(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新饵料成分对比")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-bait-ingredients:update')")
    public CommonResult<Boolean> updateZkGrowBaitIngredients(@Valid @RequestBody ZkGrowBaitIngredientsSaveReqVO updateReqVO) {
        zkGrowBaitIngredientsService.updateZkGrowBaitIngredients(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除饵料成分对比")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-bait-ingredients:delete')")
    public CommonResult<Boolean> deleteZkGrowBaitIngredients(@RequestParam("id") Long id) {
        zkGrowBaitIngredientsService.deleteZkGrowBaitIngredients(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除饵料成分对比")
                @PreAuthorize("@ss.hasPermission('fish:zk-grow-bait-ingredients:delete')")
    public CommonResult<Boolean> deleteZkGrowBaitIngredientsList(@RequestParam("ids") List<Long> ids) {
        zkGrowBaitIngredientsService.deleteZkGrowBaitIngredientsListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得饵料成分对比")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-bait-ingredients:query')")
    public CommonResult<ZkGrowBaitIngredientsRespVO> getZkGrowBaitIngredients(@RequestParam("id") Long id) {
        ZkGrowBaitIngredientsDO zkGrowBaitIngredients = zkGrowBaitIngredientsService.getZkGrowBaitIngredients(id);
        return success(BeanUtils.toBean(zkGrowBaitIngredients, ZkGrowBaitIngredientsRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得饵料成分对比分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-bait-ingredients:query')")
    public CommonResult<PageResult<ZkGrowBaitIngredientsRespVO>> getZkGrowBaitIngredientsPage(@Valid ZkGrowBaitIngredientsPageReqVO pageReqVO) {
        PageResult<ZkGrowBaitIngredientsDO> pageResult = zkGrowBaitIngredientsService.getZkGrowBaitIngredientsPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkGrowBaitIngredientsRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出饵料成分对比 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-bait-ingredients:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkGrowBaitIngredientsExcel(@Valid ZkGrowBaitIngredientsPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkGrowBaitIngredientsDO> list = zkGrowBaitIngredientsService.getZkGrowBaitIngredientsPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "饵料成分对比.xls", "数据", ZkGrowBaitIngredientsRespVO.class,
                        BeanUtils.toBean(list, ZkGrowBaitIngredientsRespVO.class));
    }

}