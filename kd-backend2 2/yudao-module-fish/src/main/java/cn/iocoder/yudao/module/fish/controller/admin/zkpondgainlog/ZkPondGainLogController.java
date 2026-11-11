package cn.iocoder.yudao.module.fish.controller.admin.zkpondgainlog;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkpondgainlog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondgainlog.ZkPondGainLogDO;
import cn.iocoder.yudao.module.fish.service.zkpondgainlog.ZkPondGainLogService;

@Tag(name = "管理后台 - 鱼生长")
@RestController
@RequestMapping("/fish/zk-pond-gain-log")
@Validated
public class ZkPondGainLogController {

    @Resource
    private ZkPondGainLogService zkPondGainLogService;

    @PostMapping("/create")
    @Operation(summary = "创建鱼生长")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-gain-log:create')")
    public CommonResult<Long> createZkPondGainLog(@Valid @RequestBody ZkPondGainLogSaveReqVO createReqVO) {
        return success(zkPondGainLogService.createZkPondGainLog(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新鱼生长")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-gain-log:update')")
    public CommonResult<Boolean> updateZkPondGainLog(@Valid @RequestBody ZkPondGainLogSaveReqVO updateReqVO) {
        zkPondGainLogService.updateZkPondGainLog(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除鱼生长")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-gain-log:delete')")
    public CommonResult<Boolean> deleteZkPondGainLog(@RequestParam("id") Long id) {
        zkPondGainLogService.deleteZkPondGainLog(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除鱼生长")
                @PreAuthorize("@ss.hasPermission('fish:zk-pond-gain-log:delete')")
    public CommonResult<Boolean> deleteZkPondGainLogList(@RequestParam("ids") List<Long> ids) {
        zkPondGainLogService.deleteZkPondGainLogListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得鱼生长")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-gain-log:query')")
    public CommonResult<ZkPondGainLogRespVO> getZkPondGainLog(@RequestParam("id") Long id) {
        ZkPondGainLogDO zkPondGainLog = zkPondGainLogService.getZkPondGainLog(id);
        return success(BeanUtils.toBean(zkPondGainLog, ZkPondGainLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得鱼生长分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-gain-log:query')")
    public CommonResult<PageResult<ZkPondGainLogRespVO>> getZkPondGainLogPage(@Valid ZkPondGainLogPageReqVO pageReqVO) {
        PageResult<ZkPondGainLogDO> pageResult = zkPondGainLogService.getZkPondGainLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkPondGainLogRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出鱼生长 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-gain-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkPondGainLogExcel(@Valid ZkPondGainLogPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkPondGainLogDO> list = zkPondGainLogService.getZkPondGainLogPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "鱼生长.xls", "数据", ZkPondGainLogRespVO.class,
                        BeanUtils.toBean(list, ZkPondGainLogRespVO.class));
    }

}