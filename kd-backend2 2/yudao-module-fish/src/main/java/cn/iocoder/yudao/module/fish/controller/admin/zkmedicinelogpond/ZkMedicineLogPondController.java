package cn.iocoder.yudao.module.fish.controller.admin.zkmedicinelogpond;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkmedicinelogpond.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmedicinelogpond.ZkMedicineLogPondDO;
import cn.iocoder.yudao.module.fish.service.zkmedicinelogpond.ZkMedicineLogPondService;

@Tag(name = "管理后台 - 用药存塘关联")
@RestController
@RequestMapping("/fish/zk-medicine-log-pond")
@Validated
public class ZkMedicineLogPondController {

    @Resource
    private ZkMedicineLogPondService zkMedicineLogPondService;

    @PostMapping("/create")
    @Operation(summary = "创建用药存塘关联")
    @PreAuthorize("@ss.hasPermission('fish:zk-medicine-log-pond:create')")
    public CommonResult<Long> createZkMedicineLogPond(@Valid @RequestBody ZkMedicineLogPondSaveReqVO createReqVO) {
        return success(zkMedicineLogPondService.createZkMedicineLogPond(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用药存塘关联")
    @PreAuthorize("@ss.hasPermission('fish:zk-medicine-log-pond:update')")
    public CommonResult<Boolean> updateZkMedicineLogPond(@Valid @RequestBody ZkMedicineLogPondSaveReqVO updateReqVO) {
        zkMedicineLogPondService.updateZkMedicineLogPond(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用药存塘关联")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-medicine-log-pond:delete')")
    public CommonResult<Boolean> deleteZkMedicineLogPond(@RequestParam("id") Long id) {
        zkMedicineLogPondService.deleteZkMedicineLogPond(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除用药存塘关联")
                @PreAuthorize("@ss.hasPermission('fish:zk-medicine-log-pond:delete')")
    public CommonResult<Boolean> deleteZkMedicineLogPondList(@RequestParam("ids") List<Long> ids) {
        zkMedicineLogPondService.deleteZkMedicineLogPondListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用药存塘关联")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-medicine-log-pond:query')")
    public CommonResult<ZkMedicineLogPondRespVO> getZkMedicineLogPond(@RequestParam("id") Long id) {
        ZkMedicineLogPondDO zkMedicineLogPond = zkMedicineLogPondService.getZkMedicineLogPond(id);
        return success(BeanUtils.toBean(zkMedicineLogPond, ZkMedicineLogPondRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用药存塘关联分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-medicine-log-pond:query')")
    public CommonResult<PageResult<ZkMedicineLogPondRespVO>> getZkMedicineLogPondPage(@Valid ZkMedicineLogPondPageReqVO pageReqVO) {
        PageResult<ZkMedicineLogPondDO> pageResult = zkMedicineLogPondService.getZkMedicineLogPondPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkMedicineLogPondRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用药存塘关联 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-medicine-log-pond:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkMedicineLogPondExcel(@Valid ZkMedicineLogPondPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkMedicineLogPondDO> list = zkMedicineLogPondService.getZkMedicineLogPondPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用药存塘关联.xls", "数据", ZkMedicineLogPondRespVO.class,
                        BeanUtils.toBean(list, ZkMedicineLogPondRespVO.class));
    }

}