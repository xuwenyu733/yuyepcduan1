package cn.iocoder.yudao.module.fish.controller.admin.zkintelligentdiagnosis;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.infra.websocket.WebSocketConfig;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkintelligentdiagnosis.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkintelligentdiagnosis.ZkIntelligentDiagnosisDO;
// import cn.iocoder.yudao.module.fish.convert.zkintelligentdiagnosis.ZkIntelligentDiagnosisConvert;
import cn.iocoder.yudao.module.fish.service.zkintelligentdiagnosis.ZkIntelligentDiagnosisService;

@Tag(name = "管理后台 - 智能诊断")
@RestController
@RequestMapping("/fish/zk-intelligent-diagnosis")
@Validated
public class ZkIntelligentDiagnosisController {

    @Resource
    private ZkIntelligentDiagnosisService zkIntelligentDiagnosisService;

    @PostMapping("/create")
    @Operation(summary = "创建智能诊断")
    public CommonResult<Long> createZkIntelligentDiagnosis(@Valid @RequestBody ZkIntelligentDiagnosisCreateReqVO createReqVO) {
        return success(zkIntelligentDiagnosisService.createZkIntelligentDiagnosis(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新智能诊断")
    public CommonResult<Boolean> updateZkIntelligentDiagnosis(@Valid @RequestBody ZkIntelligentDiagnosisUpdateReqVO updateReqVO) {
        zkIntelligentDiagnosisService.updateZkIntelligentDiagnosis(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除智能诊断")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkIntelligentDiagnosis(@RequestParam("id") Long id) {
        zkIntelligentDiagnosisService.deleteZkIntelligentDiagnosis(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得智能诊断")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkIntelligentDiagnosisRespVO> getZkIntelligentDiagnosis(@RequestParam("id") Long id) {
        ZkIntelligentDiagnosisDO zkIntelligentDiagnosis = zkIntelligentDiagnosisService.getZkIntelligentDiagnosis(id);
        return success(BeanUtils.toBean(zkIntelligentDiagnosis, ZkIntelligentDiagnosisRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得智能诊断列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkIntelligentDiagnosisRespVO>> getZkIntelligentDiagnosisList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkIntelligentDiagnosisDO> list = zkIntelligentDiagnosisService.getZkIntelligentDiagnosisList(ids);
        return success(BeanUtils.toBean(list, ZkIntelligentDiagnosisRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得智能诊断分页")
    public CommonResult<PageResult<ZkIntelligentDiagnosisRespVO>> getZkIntelligentDiagnosisPage(@Valid ZkIntelligentDiagnosisPageReqVO pageVO) {
        PageResult<ZkIntelligentDiagnosisDO> pageResult = zkIntelligentDiagnosisService.getZkIntelligentDiagnosisPage(pageVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkIntelligentDiagnosisRespVO.class));
    }

    public void enhancerList(List<ZkIntelligentDiagnosisDO> tList) {
        if(EasyListUtil.isEmpty(tList)) return;
        Set<Long> userIds = tList.stream().map(ZkIntelligentDiagnosisDO::getUserId).filter(userId -> !Objects.isNull(userId)).collect(Collectors.toSet());
        Map<Long, Map<String, Object>> fullUserInfo = WebSocketConfig.baseUserService.getFullUserInfo(new ArrayList<>(userIds));
        CommonUtil.originMap2Targets(fullUserInfo, "id", List.of("nickname"), tList, "userId", List.of("userName"));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出智能诊断 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkIntelligentDiagnosisExcel(@Valid ZkIntelligentDiagnosisExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkIntelligentDiagnosisDO> list = zkIntelligentDiagnosisService.getZkIntelligentDiagnosisList(exportReqVO);
        // 导出 Excel
        List<ZkIntelligentDiagnosisExcelVO> datas = BeanUtils.toBean(list, ZkIntelligentDiagnosisExcelVO.class);
        ExcelUtils.write(response, "智能诊断.xls", "数据", ZkIntelligentDiagnosisExcelVO.class, datas);
    }

}
