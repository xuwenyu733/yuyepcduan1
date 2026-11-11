package cn.iocoder.yudao.module.fish.controller.admin.zkillcontrol;

import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.TextVo;
import cn.iocoder.yudao.module.fish.service.aiagent.AiAgentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkillcontrol.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkillcontrol.ZkIllControlDO;
// import cn.iocoder.yudao.module.fish.convert.zkillcontrol.ZkIllControlConvert;
import cn.iocoder.yudao.module.fish.service.zkillcontrol.ZkIllControlService;

@Tag(name = "管理后台 - 疾病诊断与防治记录")
@RestController
@RequestMapping("/fish/zk-ill-control")
@Validated
public class ZkIllControlController {

    @Resource
    private ZkIllControlService zkIllControlService;
    @Autowired
    private AiAgentService aiAgentService;

    @PostMapping("/create")
    @Operation(summary = "创建疾病诊断与防治记录")
    public CommonResult<Long> createZkIllControl(@Valid @RequestBody ZkIllControlCreateReqVO createReqVO) {
        // 1. 同步到知识库
        if(StringUtils.equals(createReqVO.getStatus(), "1")) {
            String q_id = UUID.randomUUID().toString();
            String text = String.format("病害简称：%s，病害描述:%s，诊断标题:%s，诊断描述:%s", createReqVO.getIllTitle(),
                    createReqVO.getIllContent(), createReqVO.getControlTitle(), createReqVO.getControlContent());
            aiAgentService.add_text(new TextVo(q_id, text));
            createReqVO.setAgentId(q_id);
        }
        // 2. 添加数据
        return success(zkIllControlService.createZkIllControl(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新疾病诊断与防治记录")
    public CommonResult<Boolean> updateZkIllControl(@Valid @RequestBody ZkIllControlUpdateReqVO updateReqVO) {
        // 1. 同步到知识库
        if(StringUtils.equals(updateReqVO.getStatus(), "1")) {
            String q_id = StringUtil.isEmptyWith0(updateReqVO.getAgentId())?UUID.randomUUID().toString():updateReqVO.getAgentId();
            String text = String.format("病害简称：%s，病害描述:%s，诊断标题:%s，诊断描述:%s", updateReqVO.getIllTitle(),
                    updateReqVO.getIllContent(), updateReqVO.getControlTitle(), updateReqVO.getControlContent());
            aiAgentService.add_text(new TextVo(q_id, text));
            updateReqVO.setAgentId(q_id);
        } else if(!StringUtils.equals(updateReqVO.getStatus(), "1") && StringUtils.isNotEmpty(updateReqVO.getAgentId())) {
            aiAgentService.delete_text(new TextVo(updateReqVO.getAgentId(), ""));
            updateReqVO.setAgentId("0");
        }
        zkIllControlService.updateZkIllControl(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除疾病诊断与防治记录")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkIllControl(@RequestParam("id") Long id) {
        ZkIllControlDO item = zkIllControlService.deleteZkIllControl(id);
        // 2. 删除知识库
        if(!StringUtil.isEmptyWith0(item.getAgentId())) {
            aiAgentService.delete_text(new TextVo(item.getAgentId(), ""));
        }
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得疾病诊断与防治记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkIllControlRespVO> getZkIllControl(@RequestParam("id") Long id) {
        ZkIllControlDO zkIllControl = zkIllControlService.getZkIllControl(id);
        return success(BeanUtils.toBean(zkIllControl, ZkIllControlRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得疾病诊断与防治记录列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkIllControlRespVO>> getZkIllControlList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkIllControlDO> list = zkIllControlService.getZkIllControlList(ids);
        return success(BeanUtils.toBean(list, ZkIllControlRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得疾病诊断与防治记录分页")
    public CommonResult<PageResult<ZkIllControlRespVO>> getZkIllControlPage(@Valid ZkIllControlPageReqVO pageVO) {
        PageResult<ZkIllControlDO> pageResult = zkIllControlService.getZkIllControlPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkIllControlRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出疾病诊断与防治记录 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkIllControlExcel(@Valid ZkIllControlExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkIllControlDO> list = zkIllControlService.getZkIllControlList(exportReqVO);
        // 导出 Excel
        List<ZkIllControlExcelVO> datas = BeanUtils.toBean(list, ZkIllControlExcelVO.class);
        ExcelUtils.write(response, "疾病诊断与防治记录.xls", "数据", ZkIllControlExcelVO.class, datas);
    }

}
