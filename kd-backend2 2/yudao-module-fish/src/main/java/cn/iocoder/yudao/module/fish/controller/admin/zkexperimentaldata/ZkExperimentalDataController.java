package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaldata;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaldata.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaldata.ZkExperimentalDataDO;
import cn.iocoder.yudao.module.fish.service.zkexperimentaldata.ZkExperimentalDataService;

@Tag(name = "管理后台 - 实验数据采集")
@RestController
@RequestMapping("/fish/zk-experimental-data")
@Validated
public class ZkExperimentalDataController {

    @Resource
    private ZkExperimentalDataService zkExperimentalDataService;

    @PostMapping("/create")
    @Operation(summary = "创建实验数据采集")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-data:create')")
    public CommonResult<Long> createZkExperimentalData(@Valid @RequestBody ZkExperimentalDataSaveReqVO createReqVO) {
        return success(zkExperimentalDataService.createZkExperimentalData(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新实验数据采集")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-data:update')")
    public CommonResult<Boolean> updateZkExperimentalData(@Valid @RequestBody ZkExperimentalDataSaveReqVO updateReqVO) {
        zkExperimentalDataService.updateZkExperimentalData(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除实验数据采集")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-data:delete')")
    public CommonResult<Boolean> deleteZkExperimentalData(@RequestParam("id") Long id) {
        zkExperimentalDataService.deleteZkExperimentalData(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除实验数据采集")
                @PreAuthorize("@ss.hasPermission('fish:zk-experimental-data:delete')")
    public CommonResult<Boolean> deleteZkExperimentalDataList(@RequestParam("ids") List<Long> ids) {
        zkExperimentalDataService.deleteZkExperimentalDataListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得实验数据采集")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-data:query')")
    public CommonResult<ZkExperimentalDataRespVO> getZkExperimentalData(@RequestParam("id") Long id) {
        ZkExperimentalDataDO zkExperimentalData = zkExperimentalDataService.getZkExperimentalData(id);
        return success(BeanUtils.toBean(zkExperimentalData, ZkExperimentalDataRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得实验数据采集分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-data:query')")
    public CommonResult<PageResult<ZkExperimentalDataRespVO>> getZkExperimentalDataPage(@Valid ZkExperimentalDataPageReqVO pageReqVO) {
        PageResult<ZkExperimentalDataDO> pageResult = zkExperimentalDataService.getZkExperimentalDataPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkExperimentalDataRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出实验数据采集 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-data:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkExperimentalDataExcel(@Valid ZkExperimentalDataPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkExperimentalDataDO> list = zkExperimentalDataService.getZkExperimentalDataPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "实验数据采集.xls", "数据", ZkExperimentalDataRespVO.class,
                        BeanUtils.toBean(list, ZkExperimentalDataRespVO.class));
    }

}