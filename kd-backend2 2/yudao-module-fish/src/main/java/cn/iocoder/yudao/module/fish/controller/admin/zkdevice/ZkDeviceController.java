package cn.iocoder.yudao.module.fish.controller.admin.zkdevice;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import org.apache.commons.lang3.StringUtils;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevice.ZkDeviceDO;
import cn.iocoder.yudao.module.fish.dal.mysql.daping.DaPingMapper;
import cn.iocoder.yudao.module.fish.util.WaterMetricParser;
import cn.iocoder.yudao.module.fish.utils.DeptScopeSupport;
// import cn.iocoder.yudao.module.fish.convert.zkdevice.ZkDeviceConvert;
import cn.iocoder.yudao.module.fish.service.zkdevice.ZkDeviceService;

@Tag(name = "管理后台 - 设备")
@RestController
@RequestMapping("/fish/zk-device")
@Validated
public class ZkDeviceController {

    @Resource
    private ZkDeviceService zkDeviceService;

    @Resource
    private DaPingMapper daPingMapper;

    @Resource
    private DeptScopeSupport deptScopeSupport;

    @PostMapping("/create")
    @Operation(summary = "创建设备")
    public CommonResult<Long> createZkDevice(@Valid @RequestBody ZkDeviceCreateReqVO createReqVO) {
        return success(zkDeviceService.createZkDevice(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新设备")
    public CommonResult<Boolean> updateZkDevice(@Valid @RequestBody ZkDeviceUpdateReqVO updateReqVO) {
        zkDeviceService.updateZkDevice(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设备")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkDevice(@RequestParam("id") Long id) {
        zkDeviceService.deleteZkDevice(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得设备")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkDeviceRespVO> getZkDevice(@RequestParam("id") Long id) {
        ZkDeviceDO zkDevice = zkDeviceService.getZkDevice(id);
        ZkDeviceRespVO convert = BeanUtils.toBean(zkDevice, ZkDeviceRespVO.class);
        enhancer_envInfo(Arrays.asList(convert));
        return success(convert);
    }

    public void enhancer_envInfo(List<ZkDeviceRespVO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        java.util.Map<String, java.math.BigDecimal> latest = WaterMetricParser.parseLatest(
                daPingMapper.selectLatestWaterMetrics(deptScopeSupport.resolveDeptIds(null)));
        list.forEach(obj -> {
            String deviceValue = obj.getCurrValue() != null ? String.valueOf(obj.getCurrValue()) : null;
            obj.setDeviceEnvInfoVo(DeviceEnvInfoVo.fromWaterMetrics(latest, deviceValue));
        });
    }

    @GetMapping("/list")
    @Operation(summary = "获得设备列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkDeviceRespVO>> getZkDeviceList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkDeviceDO> list = zkDeviceService.getZkDeviceList(ids);
        standIntervalFilter(list);
        return success(BeanUtils.toBean(list, ZkDeviceRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得设备分页")
    public CommonResult<PageResult<ZkDeviceRespVO>> getZkDevicePage(@Valid ZkDevicePageReqVO pageVO) {
        PageResult<ZkDeviceDO> pageResult = zkDeviceService.getZkDevicePage(pageVO);
        standIntervalFilter(pageResult.getList());
        PageResult<ZkDeviceRespVO> zkDeviceRespVOPageResult = BeanUtils.toBean(pageResult, ZkDeviceRespVO.class);
        List<ZkDeviceRespVO> list = zkDeviceRespVOPageResult.getList();
        enhancer_date(list);
        return success(zkDeviceRespVOPageResult);
    }

    public void enhancer_date(List<ZkDeviceRespVO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        list.stream().forEach(obj -> obj.buildDateList());
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出设备 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkDeviceExcel(@Valid ZkDeviceExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkDeviceDO> list = zkDeviceService.getZkDeviceList(exportReqVO);
        standIntervalFilter(list);
        // 导出 Excel
        List<ZkDeviceExcelVO> datas = BeanUtils.toBean(list, ZkDeviceExcelVO.class);
        ExcelUtils.write(response, "设备.xls", "数据", ZkDeviceExcelVO.class, datas);
    }

    public void standIntervalFilter(List<ZkDeviceDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        String str = "设备值";
        for (ZkDeviceDO item : list) {
            boolean flag = false;
            StringBuilder sb = new StringBuilder();
            if(item.getStandardStart() != null) {
                String t = StringUtils.isNotBlank(item.getStartComputer())?item.getStartComputer():">";
                sb.append(str).append(t).append(item.getStandardStart()).append(";");
                flag = true;
            }
            if(item.getStandardEnd() != null) {
                String t = StringUtils.isNotBlank(item.getEndComputer())?item.getEndComputer():"<";
                sb.append(str).append(t).append(item.getStandardEnd()).append(";");
                flag = true;
            }
            if(StringUtils.isNotBlank(item.getDeviceUnit())) {
                sb.append("单位：").append(item.getDeviceUnit());
            }
            if(flag) item.setStandInterval(sb.toString());
        }
    }

}
