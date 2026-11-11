package cn.iocoder.yudao.module.fish.controller.admin.zkdevicerecord;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkdevicerecord.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicerecord.ZkDeviceRecordDO;
// import cn.iocoder.yudao.module.fish.convert.zkdevicerecord.ZkDeviceRecordConvert;
import cn.iocoder.yudao.module.fish.service.zkdevicerecord.ZkDeviceRecordService;

@Tag(name = "管理后台 - 设备数据记录")
@RestController
@RequestMapping("/fish/zk-device-record")
@Validated
public class ZkDeviceRecordController {

    @Resource
    private ZkDeviceRecordService zkDeviceRecordService;

    @PostMapping("/create")
    @Operation(summary = "创建设备数据记录")
    public CommonResult<Long> createZkDeviceRecord(@Valid @RequestBody ZkDeviceRecordCreateReqVO createReqVO) {
        return success(zkDeviceRecordService.createZkDeviceRecord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新设备数据记录")
    public CommonResult<Boolean> updateZkDeviceRecord(@Valid @RequestBody ZkDeviceRecordUpdateReqVO updateReqVO) {
        zkDeviceRecordService.updateZkDeviceRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设备数据记录")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkDeviceRecord(@RequestParam("id") Long id) {
        zkDeviceRecordService.deleteZkDeviceRecord(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得设备数据记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkDeviceRecordRespVO> getZkDeviceRecord(@RequestParam("id") Long id) {
        ZkDeviceRecordDO zkDeviceRecord = zkDeviceRecordService.getZkDeviceRecord(id);
        return success(BeanUtils.toBean(zkDeviceRecord, ZkDeviceRecordRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得设备数据记录列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkDeviceRecordRespVO>> getZkDeviceRecordList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkDeviceRecordDO> list = zkDeviceRecordService.getZkDeviceRecordList(ids);
        return success(BeanUtils.toBean(list, ZkDeviceRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得设备数据记录分页")
    public CommonResult<PageResult<ZkDeviceRecordRespVO>> getZkDeviceRecordPage(@Valid ZkDeviceRecordPageReqVO pageVO) {
        PageResult<ZkDeviceRecordDO> pageResult = zkDeviceRecordService.getZkDeviceRecordPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkDeviceRecordRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出设备数据记录 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkDeviceRecordExcel(@Valid ZkDeviceRecordExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkDeviceRecordDO> list = zkDeviceRecordService.getZkDeviceRecordList(exportReqVO);
        // 导出 Excel
        List<ZkDeviceRecordExcelVO> datas = BeanUtils.toBean(list, ZkDeviceRecordExcelVO.class);
        ExcelUtils.write(response, "设备数据记录.xls", "数据", ZkDeviceRecordExcelVO.class, datas);
    }

}
