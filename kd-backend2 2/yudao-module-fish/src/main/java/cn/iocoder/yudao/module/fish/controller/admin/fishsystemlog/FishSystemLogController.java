package cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.fishsystemlog.FishSystemLogDO;
import cn.iocoder.yudao.module.fish.service.fishsystemlog.FishSystemLogService;
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

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;


@Tag(name = "管理后台 - fish系统日志")
@RestController
@RequestMapping("/zk/fish-system-log")
@Validated
public class FishSystemLogController {

    @Resource
    private FishSystemLogService fishSystemLogService;

    @PostMapping("/create")
    @Operation(summary = "创建fish系统日志")
    @DataPermission(enable = false)
    //@PreAuthorize("@ss.hasPermission('zk:fish-system-log:create')")
    public CommonResult<Long> createFishSystemLog(@Valid @RequestBody FishSystemLogCreateReqVO createReqVO) {
        return success(fishSystemLogService.createFishSystemLog(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新fish系统日志")
    @DataPermission(enable = false)
    //@PreAuthorize("@ss.hasPermission('zk:fish-system-log:update')")
    public CommonResult<Boolean> updateFishSystemLog(@Valid @RequestBody FishSystemLogUpdateReqVO updateReqVO) {
        fishSystemLogService.updateFishSystemLog(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除fish系统日志")
    @Parameter(name = "id", description = "编号", required = true)
    @DataPermission(enable = false)
    //@PreAuthorize("@ss.hasPermission('zk:fish-system-log:delete')")
    public CommonResult<Boolean> deleteFishSystemLog(@RequestParam("id") Long id) {
        fishSystemLogService.deleteFishSystemLog(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得fish系统日志")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @DataPermission(enable = false)
    //@PreAuthorize("@ss.hasPermission('zk:fish-system-log:query')")
    public CommonResult<FishSystemLogRespVO> getFishSystemLog(@RequestParam("id") Long id) {
        FishSystemLogDO fishSystemLog = fishSystemLogService.getFishSystemLog(id);
        return success(BeanUtils.toBean(fishSystemLog, FishSystemLogRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得fish系统日志列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @DataPermission(enable = false)
    //@PreAuthorize("@ss.hasPermission('zk:fish-system-log:query')")
    public CommonResult<List<FishSystemLogRespVO>> getFishSystemLogList(@RequestParam("ids") Collection<Long> ids) {
        List<FishSystemLogDO> list = fishSystemLogService.getFishSystemLogList(ids);
        return success(BeanUtils.toBean(list, FishSystemLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得fish系统日志分页")
    @DataPermission(enable = false)
    //@PreAuthorize("@ss.hasPermission('zk:fish-system-log:query')")
    public CommonResult<PageResult<FishSystemLogRespVO>> getFishSystemLogPage(@Valid FishSystemLogPageReqVO pageVO) {
        PageResult<FishSystemLogDO> pageResult = fishSystemLogService.getFishSystemLogPage(pageVO);
        return success(BeanUtils.toBean(pageResult, FishSystemLogRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出fish系统日志 Excel")
    @DataPermission(enable = false)
    //@PreAuthorize("@ss.hasPermission('zk:fish-system-log:export')")
    // @OperateLog(type = EXPORT)
    public void exportFishSystemLogExcel(@Valid FishSystemLogExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<FishSystemLogDO> list = fishSystemLogService.getFishSystemLogList(exportReqVO);
        // 导出 Excel
        List<FishSystemLogExcelVO> datas = BeanUtils.toBean(list, FishSystemLogExcelVO.class);
        ExcelUtils.write(response, "fish系统日志.xls", "数据", FishSystemLogExcelVO.class, datas);
    }

}
