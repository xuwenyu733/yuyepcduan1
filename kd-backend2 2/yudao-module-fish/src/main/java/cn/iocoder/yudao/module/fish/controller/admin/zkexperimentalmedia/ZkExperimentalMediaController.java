package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalmedia;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalmedia.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalmedia.ZkExperimentalMediaDO;
import cn.iocoder.yudao.module.fish.service.zkexperimentalmedia.ZkExperimentalMediaService;

@Tag(name = "管理后台 - 实验多媒体")
@RestController
@RequestMapping("/fish/zk-experimental-media")
@Validated
public class ZkExperimentalMediaController {

    @Resource
    private ZkExperimentalMediaService zkExperimentalMediaService;

    @PostMapping("/create")
    @Operation(summary = "创建实验多媒体")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-media:create')")
    public CommonResult<Long> createZkExperimentalMedia(@Valid @RequestBody ZkExperimentalMediaSaveReqVO createReqVO) {
        return success(zkExperimentalMediaService.createZkExperimentalMedia(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新实验多媒体")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-media:update')")
    public CommonResult<Boolean> updateZkExperimentalMedia(@Valid @RequestBody ZkExperimentalMediaSaveReqVO updateReqVO) {
        zkExperimentalMediaService.updateZkExperimentalMedia(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除实验多媒体")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-media:delete')")
    public CommonResult<Boolean> deleteZkExperimentalMedia(@RequestParam("id") Long id) {
        zkExperimentalMediaService.deleteZkExperimentalMedia(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除实验多媒体")
                @PreAuthorize("@ss.hasPermission('fish:zk-experimental-media:delete')")
    public CommonResult<Boolean> deleteZkExperimentalMediaList(@RequestParam("ids") List<Long> ids) {
        zkExperimentalMediaService.deleteZkExperimentalMediaListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得实验多媒体")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-media:query')")
    public CommonResult<ZkExperimentalMediaRespVO> getZkExperimentalMedia(@RequestParam("id") Long id) {
        ZkExperimentalMediaDO zkExperimentalMedia = zkExperimentalMediaService.getZkExperimentalMedia(id);
        return success(BeanUtils.toBean(zkExperimentalMedia, ZkExperimentalMediaRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得实验多媒体分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-media:query')")
    public CommonResult<PageResult<ZkExperimentalMediaRespVO>> getZkExperimentalMediaPage(@Valid ZkExperimentalMediaPageReqVO pageReqVO) {
        PageResult<ZkExperimentalMediaDO> pageResult = zkExperimentalMediaService.getZkExperimentalMediaPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkExperimentalMediaRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出实验多媒体 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-media:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkExperimentalMediaExcel(@Valid ZkExperimentalMediaPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkExperimentalMediaDO> list = zkExperimentalMediaService.getZkExperimentalMediaPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "实验多媒体.xls", "数据", ZkExperimentalMediaRespVO.class,
                        BeanUtils.toBean(list, ZkExperimentalMediaRespVO.class));
    }

}