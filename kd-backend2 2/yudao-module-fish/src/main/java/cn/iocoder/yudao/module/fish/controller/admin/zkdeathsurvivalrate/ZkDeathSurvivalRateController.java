package cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate.vo.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate.vo.stats.ZkDeathSurvivalRateStatsRespVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeathsurvivalrate.ZkDeathSurvivalRateDO;
// import cn.iocoder.yudao.module.fish.convert.zkdeathsurvivalrate.ZkDeathSurvivalRateConvert;
import cn.iocoder.yudao.module.fish.service.zkdeathsurvivalrate.ZkDeathSurvivalRateService;

@Tag(name = "管理后台 - 死亡和存活率")
@RestController
@RequestMapping("/fish/zk-death-survival-rate")
@Validated
public class ZkDeathSurvivalRateController {

    @Resource
    private ZkDeathSurvivalRateService zkDeathSurvivalRateService;

    @PostMapping("/create")
    @Operation(summary = "创建死亡和存活率")
    public CommonResult<Long> createZkDeathSurvivalRate(@Valid @RequestBody ZkDeathSurvivalRateCreateReqVO createReqVO) {
        return success(zkDeathSurvivalRateService.createZkDeathSurvivalRate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新死亡和存活率")
    public CommonResult<Boolean> updateZkDeathSurvivalRate(@Valid @RequestBody ZkDeathSurvivalRateUpdateReqVO updateReqVO) {
        zkDeathSurvivalRateService.updateZkDeathSurvivalRate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除死亡和存活率")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkDeathSurvivalRate(@RequestParam("id") Long id) {
        zkDeathSurvivalRateService.deleteZkDeathSurvivalRate(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得死亡和存活率")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkDeathSurvivalRateRespVO> getZkDeathSurvivalRate(@RequestParam("id") Long id) {
        ZkDeathSurvivalRateDO zkDeathSurvivalRate = zkDeathSurvivalRateService.getZkDeathSurvivalRate(id);
        return success(BeanUtils.toBean(zkDeathSurvivalRate, ZkDeathSurvivalRateRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得死亡和存活率列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkDeathSurvivalRateRespVO>> getZkDeathSurvivalRateList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkDeathSurvivalRateDO> list = zkDeathSurvivalRateService.getZkDeathSurvivalRateList(ids);
        return success(BeanUtils.toBean(list, ZkDeathSurvivalRateRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得死亡和存活率分页")
    public CommonResult<PageResult<ZkDeathSurvivalRateRespVO>> getZkDeathSurvivalRatePage(@Valid ZkDeathSurvivalRatePageReqVO pageVO) {
        PageResult<ZkDeathSurvivalRateDO> pageResult = zkDeathSurvivalRateService.getZkDeathSurvivalRatePage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkDeathSurvivalRateRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出死亡和存活率 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkDeathSurvivalRateExcel(@Valid ZkDeathSurvivalRateExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkDeathSurvivalRateDO> list = zkDeathSurvivalRateService.getZkDeathSurvivalRateList(exportReqVO);
        // 导出 Excel
        List<ZkDeathSurvivalRateExcelVO> datas = BeanUtils.toBean(list, ZkDeathSurvivalRateExcelVO.class);
        ExcelUtils.write(response, "死亡和存活率.xls", "数据", ZkDeathSurvivalRateExcelVO.class, datas);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获得死亡和存活率统计数据")
    @Parameter(name = "deptId", description = "归属部门（本部门+子部门）；不传则取登录用户部门", required = false)
    public CommonResult<ZkDeathSurvivalRateStatsRespVO> getStatistics(
            @RequestParam(value = "deptId", required = false) Long deptId) {
        return success(zkDeathSurvivalRateService.getStatistics(deptId));
    }

}
