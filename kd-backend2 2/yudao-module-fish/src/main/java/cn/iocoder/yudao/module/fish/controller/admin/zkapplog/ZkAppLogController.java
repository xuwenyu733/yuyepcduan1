package cn.iocoder.yudao.module.fish.controller.admin.zkapplog;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.fish.service.zkapplog.ZkAppLogService;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
// // import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkapplog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkapplog.ZkAppLogDO;

@Tag(name = "管理后台 - app记录")
@RestController
@RequestMapping("/fish/zk-app-log")
@Validated
public class ZkAppLogController {

    @Resource
    private ZkAppLogService zkAppLogService;

    @PostMapping("/create")
    @Operation(summary = "创建app记录")
    public CommonResult<Long> createZkAppLog(@Valid @RequestBody ZkAppLogCreateReqVO createReqVO) {
        return success(zkAppLogService.createZkAppLog(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新app记录")
    public CommonResult<Boolean> updateZkAppLog(@Valid @RequestBody ZkAppLogUpdateReqVO updateReqVO) {
        zkAppLogService.updateZkAppLog(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除app记录")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkAppLog(@RequestParam("id") Long id) {
        zkAppLogService.deleteZkAppLog(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得app记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkAppLogRespVO> getZkAppLog(@RequestParam("id") Long id) {
        ZkAppLogDO zkAppLog = zkAppLogService.getZkAppLog(id);
        return success(BeanUtils.toBean(zkAppLog, ZkAppLogRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得app记录列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkAppLogRespVO>> getZkAppLogList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkAppLogDO> list = zkAppLogService.getZkAppLogList(ids);
        return success(BeanUtils.toBean(list, ZkAppLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得app记录分页")
    @PermitAll
    public CommonResult<PageResult<ZkAppLogRespVO>> getZkAppLogPage(@Valid ZkAppLogPageReqVO pageVO) {
        PageResult<ZkAppLogDO> pageResult = zkAppLogService.getZkAppLogPage(pageVO);
        if(EasyListUtil.isEmpty(pageResult.getList())) {
            pageResult = zkAppLogService.getZkAppLogPage(pageVO.build_deviceMac(null));
        }
        return success(BeanUtils.toBean(pageResult, ZkAppLogRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出app记录 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkAppLogExcel(@Valid ZkAppLogExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkAppLogDO> list = zkAppLogService.getZkAppLogList(exportReqVO);
        // 导出 Excel
        List<ZkAppLogExcelVO> datas = BeanUtils.toBean(list, ZkAppLogExcelVO.class);
        ExcelUtils.write(response, "app记录.xls", "数据", ZkAppLogExcelVO.class, datas);
    }

}
