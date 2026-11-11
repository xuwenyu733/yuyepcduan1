package cn.iocoder.yudao.module.fish.controller.admin.zkdevicealarm;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkdevicealarm.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicealarm.ZkDeviceAlarmDO;
// import cn.iocoder.yudao.module.fish.convert.zkdevicealarm.ZkDeviceAlarmConvert;
import cn.iocoder.yudao.module.fish.service.zkdevicealarm.ZkDeviceAlarmService;

@Tag(name = "管理后台 - 设备报警")
@RestController
@RequestMapping("/fish/zk-device-alarm")
@Validated
public class ZkDeviceAlarmController {

    @Resource
    private ZkDeviceAlarmService zkDeviceAlarmService;

    @PostMapping("/create")
    @Operation(summary = "创建设备报警")
    public CommonResult<Long> createZkDeviceAlarm(@Valid @RequestBody ZkDeviceAlarmCreateReqVO createReqVO) {
        return success(zkDeviceAlarmService.createZkDeviceAlarm(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新设备报警")
    public CommonResult<Boolean> updateZkDeviceAlarm(@Valid @RequestBody ZkDeviceAlarmUpdateReqVO updateReqVO) {
        zkDeviceAlarmService.updateZkDeviceAlarm(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设备报警")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkDeviceAlarm(@RequestParam("id") Long id) {
        zkDeviceAlarmService.deleteZkDeviceAlarm(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得设备报警")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkDeviceAlarmRespVO> getZkDeviceAlarm(@RequestParam("id") Long id) {
        ZkDeviceAlarmDO zkDeviceAlarm = zkDeviceAlarmService.getZkDeviceAlarm(id);
        return success(BeanUtils.toBean(zkDeviceAlarm, ZkDeviceAlarmRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得设备报警列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkDeviceAlarmRespVO>> getZkDeviceAlarmList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkDeviceAlarmDO> list = zkDeviceAlarmService.getZkDeviceAlarmList(ids);
        return success(BeanUtils.toBean(list, ZkDeviceAlarmRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得设备报警分页")
    public CommonResult<PageResult<ZkDeviceAlarmRespVO>> getZkDeviceAlarmPage(@Valid ZkDeviceAlarmPageReqVO pageVO) {
        PageResult<ZkDeviceAlarmDO> pageResult = zkDeviceAlarmService.getZkDeviceAlarmPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkDeviceAlarmRespVO.class));
    }

    @GetMapping("/list-for-dashboard")
    @Operation(summary = "获得设备报警列表，用于首页大屏")
    @Parameter(name = "deptId", description = "归属部门（本部门+子部门）；不传则取登录用户部门", required = false)
    public CommonResult<List<ZkDeviceAlarmDashboardRespVO>> getZkDeviceAlarmDashboardList(
            @RequestParam(value = "deptId", required = false) Long deptId) {
        return success(zkDeviceAlarmService.getZkDeviceAlarmDashboardList(deptId));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出设备报警 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkDeviceAlarmExcel(@Valid ZkDeviceAlarmExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkDeviceAlarmDO> list = zkDeviceAlarmService.getZkDeviceAlarmList(exportReqVO);
        // 导出 Excel
        List<ZkDeviceAlarmExcelVO> datas = BeanUtils.toBean(list, ZkDeviceAlarmExcelVO.class);
        ExcelUtils.write(response, "设备报警.xls", "数据", ZkDeviceAlarmExcelVO.class, datas);
    }

}
