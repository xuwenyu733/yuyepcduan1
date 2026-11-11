package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishtype;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishtype.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishtype.ZkPondFishTypeDO;
import cn.iocoder.yudao.module.fish.service.zkpondfishtype.ZkPondFishTypeService;

@Tag(name = "管理后台 - 鱼苗种类")
@RestController
@RequestMapping("/fish/zk-pond-fish-type")
@Validated
public class ZkPondFishTypeController {

    @Resource
    private ZkPondFishTypeService zkPondFishTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建鱼苗种类")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-fish-type:create')")
    public CommonResult<Long> createZkPondFishType(@Valid @RequestBody ZkPondFishTypeSaveReqVO createReqVO) {
        return success(zkPondFishTypeService.createZkPondFishType(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新鱼苗种类")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-fish-type:update')")
    public CommonResult<Boolean> updateZkPondFishType(@Valid @RequestBody ZkPondFishTypeSaveReqVO updateReqVO) {
        zkPondFishTypeService.updateZkPondFishType(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除鱼苗种类")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-fish-type:delete')")
    public CommonResult<Boolean> deleteZkPondFishType(@RequestParam("id") Long id) {
        zkPondFishTypeService.deleteZkPondFishType(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除鱼苗种类")
                @PreAuthorize("@ss.hasPermission('fish:zk-pond-fish-type:delete')")
    public CommonResult<Boolean> deleteZkPondFishTypeList(@RequestParam("ids") List<Long> ids) {
        zkPondFishTypeService.deleteZkPondFishTypeListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得鱼苗种类")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-fish-type:query')")
    public CommonResult<ZkPondFishTypeRespVO> getZkPondFishType(@RequestParam("id") Long id) {
        ZkPondFishTypeDO zkPondFishType = zkPondFishTypeService.getZkPondFishType(id);
        return success(BeanUtils.toBean(zkPondFishType, ZkPondFishTypeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得鱼苗种类分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-fish-type:query')")
    public CommonResult<PageResult<ZkPondFishTypeRespVO>> getZkPondFishTypePage(@Valid ZkPondFishTypePageReqVO pageReqVO) {
        PageResult<ZkPondFishTypeDO> pageResult = zkPondFishTypeService.getZkPondFishTypePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkPondFishTypeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出鱼苗种类 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-fish-type:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkPondFishTypeExcel(@Valid ZkPondFishTypePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkPondFishTypeDO> list = zkPondFishTypeService.getZkPondFishTypePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "鱼苗种类.xls", "数据", ZkPondFishTypeRespVO.class,
                        BeanUtils.toBean(list, ZkPondFishTypeRespVO.class));
    }

}