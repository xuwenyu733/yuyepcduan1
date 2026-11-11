package cn.iocoder.yudao.module.fish.controller.admin.zkthresholdrule;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkthresholdrule.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkthresholdrule.ZkThresholdRuleDO;
// import cn.iocoder.yudao.module.fish.convert.zkthresholdrule.ZkThresholdRuleConvert;
import cn.iocoder.yudao.module.fish.service.zkthresholdrule.ZkThresholdRuleService;

@Tag(name = "管理后台 - 阈值规则")
@RestController
@RequestMapping("/fish/zk-threshold-rule")
@Validated
public class ZkThresholdRuleController {

    @Resource
    private ZkThresholdRuleService zkThresholdRuleService;

    @PostMapping("/create")
    @Operation(summary = "创建阈值规则")
    public CommonResult<Long> createZkThresholdRule(@Valid @RequestBody ZkThresholdRuleCreateReqVO createReqVO) {
        return success(zkThresholdRuleService.createZkThresholdRule(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新阈值规则")
    public CommonResult<Boolean> updateZkThresholdRule(@Valid @RequestBody ZkThresholdRuleUpdateReqVO updateReqVO) {
        zkThresholdRuleService.updateZkThresholdRule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除阈值规则")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkThresholdRule(@RequestParam("id") Long id) {
        zkThresholdRuleService.deleteZkThresholdRule(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得阈值规则")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkThresholdRuleRespVO> getZkThresholdRule(@RequestParam("id") Long id) {
        ZkThresholdRuleDO zkThresholdRule = zkThresholdRuleService.getZkThresholdRule(id);
        return success(BeanUtils.toBean(zkThresholdRule, ZkThresholdRuleRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得阈值规则列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkThresholdRuleRespVO>> getZkThresholdRuleList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkThresholdRuleDO> list = zkThresholdRuleService.getZkThresholdRuleList(ids);
        return success(BeanUtils.toBean(list, ZkThresholdRuleRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得阈值规则分页")
    public CommonResult<PageResult<ZkThresholdRuleRespVO>> getZkThresholdRulePage(@Valid ZkThresholdRulePageReqVO pageVO) {
        PageResult<ZkThresholdRuleDO> pageResult = zkThresholdRuleService.getZkThresholdRulePage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkThresholdRuleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出阈值规则 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkThresholdRuleExcel(@Valid ZkThresholdRuleExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkThresholdRuleDO> list = zkThresholdRuleService.getZkThresholdRuleList(exportReqVO);
        // 导出 Excel
        List<ZkThresholdRuleExcelVO> datas = BeanUtils.toBean(list, ZkThresholdRuleExcelVO.class);
        ExcelUtils.write(response, "阈值规则.xls", "数据", ZkThresholdRuleExcelVO.class, datas);
    }

}
