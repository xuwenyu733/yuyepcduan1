package cn.iocoder.yudao.module.fish.controller.admin.zkenergymeterreading;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.fish.controller.admin.zkenergymeterreading.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkenergymeterreading.ZkEnergyMeterReadingDO;
import cn.iocoder.yudao.module.fish.service.zkenergymeterreading.ZkEnergyMeterReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 电度抄表")
@RestController
@RequestMapping("/fish/zk-energy-meter-reading")
@Validated
public class ZkEnergyMeterReadingController {

    @Resource
    private ZkEnergyMeterReadingService service;

    @PostMapping("/create")
    @Operation(summary = "创建电度抄表")
    public CommonResult<Long> create(@Valid @RequestBody ZkEnergyMeterReadingSaveReqVO reqVO) {
        return success(service.create(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新电度抄表")
    public CommonResult<Boolean> update(@Valid @RequestBody ZkEnergyMeterReadingSaveReqVO reqVO) {
        service.update(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除电度抄表")
    @Parameter(name = "id", required = true)
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        service.delete(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得电度抄表")
    public CommonResult<ZkEnergyMeterReadingRespVO> get(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(service.get(id), ZkEnergyMeterReadingRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "电度抄表分页")
    public CommonResult<PageResult<ZkEnergyMeterReadingRespVO>> page(@Valid ZkEnergyMeterReadingPageReqVO pageReqVO) {
        PageResult<ZkEnergyMeterReadingDO> page = service.getPage(pageReqVO);
        return success(BeanUtils.toBean(page, ZkEnergyMeterReadingRespVO.class));
    }
}
