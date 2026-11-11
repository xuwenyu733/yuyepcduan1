package cn.iocoder.yudao.module.fish.controller.admin.zkdeviceevent;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkdeviceevent.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeviceevent.ZkDeviceEventDO;
// import cn.iocoder.yudao.module.fish.convert.zkdeviceevent.ZkDeviceEventConvert;
import cn.iocoder.yudao.module.fish.service.zkdeviceevent.ZkDeviceEventService;

@Tag(name = "管理后台 - 设备事件记录")
@RestController
@RequestMapping("/fish/zk-device-event")
@Validated
public class ZkDeviceEventController {

    @Resource
    private ZkDeviceEventService zkDeviceEventService;

    @PostMapping("/create")
    @Operation(summary = "创建设备事件记录")
    public CommonResult<Long> createZkDeviceEvent(@Valid @RequestBody ZkDeviceEventCreateReqVO createReqVO) {
        return success(zkDeviceEventService.createZkDeviceEvent(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新设备事件记录")
    public CommonResult<Boolean> updateZkDeviceEvent(@Valid @RequestBody ZkDeviceEventUpdateReqVO updateReqVO) {
        zkDeviceEventService.updateZkDeviceEvent(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设备事件记录")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkDeviceEvent(@RequestParam("id") Long id) {
        zkDeviceEventService.deleteZkDeviceEvent(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得设备事件记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkDeviceEventRespVO> getZkDeviceEvent(@RequestParam("id") Long id) {
        ZkDeviceEventDO zkDeviceEvent = zkDeviceEventService.getZkDeviceEvent(id);
        return success(BeanUtils.toBean(zkDeviceEvent, ZkDeviceEventRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得设备事件记录列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkDeviceEventRespVO>> getZkDeviceEventList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkDeviceEventDO> list = zkDeviceEventService.getZkDeviceEventList(ids);
        return success(BeanUtils.toBean(list, ZkDeviceEventRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得设备事件记录分页")
    public CommonResult<PageResult<ZkDeviceEventRespVO>> getZkDeviceEventPage(@Valid ZkDeviceEventPageReqVO pageVO) {
        PageResult<ZkDeviceEventDO> pageResult = zkDeviceEventService.getZkDeviceEventPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkDeviceEventRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出设备事件记录 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkDeviceEventExcel(@Valid ZkDeviceEventExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkDeviceEventDO> list = zkDeviceEventService.getZkDeviceEventList(exportReqVO);
        // 导出 Excel
        List<ZkDeviceEventExcelVO> datas = BeanUtils.toBean(list, ZkDeviceEventExcelVO.class);
        ExcelUtils.write(response, "设备事件记录.xls", "数据", ZkDeviceEventExcelVO.class, datas);
    }

}
