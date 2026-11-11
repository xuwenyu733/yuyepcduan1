package cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicinelog.ZkUseMedicineLogDO;
import cn.iocoder.yudao.module.fish.service.zkusemedicinelog.ZkUseMedicineLogService;
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
import java.util.stream.Collectors;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
// import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;
// import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;

import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicine.ZkUseMedicineDO;
// import cn.iocoder.yudao.module.fish.convert.zkusemedicine.ZkUseMedicineConvert;
import cn.iocoder.yudao.module.fish.service.zkusemedicine.ZkUseMedicineService;

@Tag(name = "管理后台 - 用药记录")
@RestController
@RequestMapping("/fish/zk-use-medicine")
@Validated
public class ZkUseMedicineController {

    @Resource
    private ZkUseMedicineService zkUseMedicineService;

    @Resource
    private ZkUseMedicineLogService zkUseMedicineLogService;

    @PostMapping("/create")
    @Operation(summary = "创建用药记录")
    public CommonResult<Long> createZkUseMedicine(@Valid @RequestBody ZkUseMedicineCreateReqVO createReqVO) {
        return success(zkUseMedicineService.createZkUseMedicine(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用药记录")
    public CommonResult<Boolean> updateZkUseMedicine(@Valid @RequestBody ZkUseMedicineUpdateReqVO updateReqVO) {
        zkUseMedicineService.updateZkUseMedicine(updateReqVO);
        return success(true);
    }


    @DeleteMapping("/delete")
    @Operation(summary = "删除用药记录")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkUseMedicine(@RequestParam("id") Long id) {
        zkUseMedicineService.deleteZkUseMedicine(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用药记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkUseMedicineRespVO> getZkUseMedicine(@RequestParam("id") Long id) {
        ZkUseMedicineDO zkUseMedicine = zkUseMedicineService.getZkUseMedicine(id);
        enhancer_logList(Arrays.asList(zkUseMedicine));
        return success(BeanUtils.toBean(zkUseMedicine, ZkUseMedicineRespVO.class));
    }

    public void enhancer_logList(List<ZkUseMedicineDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        // 1. 获取 记录
        Set<Long> useMedicineIds = list.stream().map(ZkUseMedicineDO::getId).collect(Collectors.toSet());
        List<ZkUseMedicineLogDO> medicineDOS = zkUseMedicineLogService.getByUseMedicineIds(useMedicineIds);
        if(EasyListUtil.isEmpty(medicineDOS)) return;
        Map<Long, List<ZkUseMedicineLogDO>> collect = medicineDOS.stream().collect(Collectors.groupingBy(ZkUseMedicineLogDO::getZkUseMedicineId));
        // 2. 设置数据
        for(Long key : collect.keySet()) {
            List<ZkUseMedicineLogDO> tempList = collect.get(key);
            for(ZkUseMedicineDO item : list) {
                if(Objects.equals(item.getId(), key)) {
                    item.setUseMedicineLogList(tempList);
                    break;
                }
            }
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获得用药记录列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkUseMedicineRespVO>> getZkUseMedicineList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkUseMedicineDO> list = zkUseMedicineService.getZkUseMedicineList(ids);
        enhancer_logList(list);
        return success(BeanUtils.toBean(list, ZkUseMedicineRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用药记录分页")
    public CommonResult<PageResult<ZkUseMedicineRespVO>> getZkUseMedicinePage(@Valid ZkUseMedicinePageReqVO pageVO) {
        PageResult<ZkUseMedicineDO> pageResult = zkUseMedicineService.getZkUseMedicinePage(pageVO);
        enhancer_logList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkUseMedicineRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用药记录 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkUseMedicineExcel(@Valid ZkUseMedicineExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkUseMedicineDO> list = zkUseMedicineService.getZkUseMedicineList(exportReqVO);
        // 导出 Excel
        List<ZkUseMedicineExcelVO> datas = BeanUtils.toBean(list, ZkUseMedicineExcelVO.class);
        ExcelUtils.write(response, "用药记录.xls", "数据", ZkUseMedicineExcelVO.class, datas);
    }

}
