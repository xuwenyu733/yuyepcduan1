package cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitnutrition;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitnutrition.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitnutrition.ZkGrowBaitNutritionDO;
import cn.iocoder.yudao.module.fish.service.zkgrowbaitnutrition.ZkGrowBaitNutritionService;

@Tag(name = "管理后台 - 饵料营养成分")
@RestController
@RequestMapping("/fish/zk-grow-bait-nutrition")
@Validated
public class ZkGrowBaitNutritionController {

    @Resource
    private ZkGrowBaitNutritionService zkGrowBaitNutritionService;

    @PostMapping("/create")
    @Operation(summary = "创建饵料营养成分")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-bait-nutrition:create')")
    public CommonResult<Long> createZkGrowBaitNutrition(@Valid @RequestBody ZkGrowBaitNutritionSaveReqVO createReqVO) {
        return success(zkGrowBaitNutritionService.createZkGrowBaitNutrition(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新饵料营养成分")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-bait-nutrition:update')")
    public CommonResult<Boolean> updateZkGrowBaitNutrition(@Valid @RequestBody ZkGrowBaitNutritionSaveReqVO updateReqVO) {
        zkGrowBaitNutritionService.updateZkGrowBaitNutrition(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除饵料营养成分")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-bait-nutrition:delete')")
    public CommonResult<Boolean> deleteZkGrowBaitNutrition(@RequestParam("id") Long id) {
        zkGrowBaitNutritionService.deleteZkGrowBaitNutrition(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除饵料营养成分")
                @PreAuthorize("@ss.hasPermission('fish:zk-grow-bait-nutrition:delete')")
    public CommonResult<Boolean> deleteZkGrowBaitNutritionList(@RequestParam("ids") List<Long> ids) {
        zkGrowBaitNutritionService.deleteZkGrowBaitNutritionListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得饵料营养成分")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-bait-nutrition:query')")
    public CommonResult<ZkGrowBaitNutritionRespVO> getZkGrowBaitNutrition(@RequestParam("id") Long id) {
        ZkGrowBaitNutritionDO zkGrowBaitNutrition = zkGrowBaitNutritionService.getZkGrowBaitNutrition(id);
        return success(BeanUtils.toBean(zkGrowBaitNutrition, ZkGrowBaitNutritionRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得饵料营养成分分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-bait-nutrition:query')")
    public CommonResult<PageResult<ZkGrowBaitNutritionRespVO>> getZkGrowBaitNutritionPage(@Valid ZkGrowBaitNutritionPageReqVO pageReqVO) {
        PageResult<ZkGrowBaitNutritionDO> pageResult = zkGrowBaitNutritionService.getZkGrowBaitNutritionPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkGrowBaitNutritionRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出饵料营养成分 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-grow-bait-nutrition:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkGrowBaitNutritionExcel(@Valid ZkGrowBaitNutritionPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkGrowBaitNutritionDO> list = zkGrowBaitNutritionService.getZkGrowBaitNutritionPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "饵料营养成分.xls", "数据", ZkGrowBaitNutritionRespVO.class,
                        BeanUtils.toBean(list, ZkGrowBaitNutritionRespVO.class));
    }

}