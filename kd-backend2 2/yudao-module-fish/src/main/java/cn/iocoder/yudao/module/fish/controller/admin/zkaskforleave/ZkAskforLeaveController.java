package cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
// import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;
// import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkaskforleave.ZkAskforLeaveDO;
// import cn.iocoder.yudao.module.fish.convert.zkaskforleave.ZkAskforLeaveConvert;
import cn.iocoder.yudao.module.fish.service.zkaskforleave.ZkAskforLeaveService;

@Tag(name = "管理后台 - 请假")
@RestController
@RequestMapping("/fish/zk-askfor-leave")
@Validated
public class ZkAskforLeaveController {

    @Resource
    private ZkAskforLeaveService zkAskforLeaveService;

    @PostMapping("/create")
    @Operation(summary = "创建请假")
    public CommonResult<Long> createZkAskforLeave(@Valid @RequestBody ZkAskforLeaveCreateReqVO createReqVO) {
        return success(zkAskforLeaveService.createZkAskforLeave(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新请假")
    public CommonResult<Boolean> updateZkAskforLeave(@Valid @RequestBody ZkAskforLeaveUpdateReqVO updateReqVO) {
        zkAskforLeaveService.updateZkAskforLeave(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除请假")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkAskforLeave(@RequestParam("id") Long id) {
        zkAskforLeaveService.deleteZkAskforLeave(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得请假")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkAskforLeaveRespVO> getZkAskforLeave(@RequestParam("id") Long id) {
        ZkAskforLeaveDO zkAskforLeave = zkAskforLeaveService.getZkAskforLeave(id);
        return success(BeanUtils.toBean(zkAskforLeave, ZkAskforLeaveRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得请假列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkAskforLeaveRespVO>> getZkAskforLeaveList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkAskforLeaveDO> list = zkAskforLeaveService.getZkAskforLeaveList(ids);
        return success(BeanUtils.toBean(list, ZkAskforLeaveRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得请假分页")
    public CommonResult<PageResult<ZkAskforLeaveRespVO>> getZkAskforLeavePage(@Valid ZkAskforLeavePageReqVO pageVO) {
        PageResult<ZkAskforLeaveDO> pageResult = zkAskforLeaveService.getZkAskforLeavePage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkAskforLeaveRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出请假 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkAskforLeaveExcel(@Valid ZkAskforLeaveExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkAskforLeaveDO> list = zkAskforLeaveService.getZkAskforLeaveList(exportReqVO);
        // 导出 Excel
        List<ZkAskforLeaveExcelVO> datas = BeanUtils.toBean(list, ZkAskforLeaveExcelVO.class);
        ExcelUtils.write(response, "请假.xls", "数据", ZkAskforLeaveExcelVO.class, datas);
    }

}
