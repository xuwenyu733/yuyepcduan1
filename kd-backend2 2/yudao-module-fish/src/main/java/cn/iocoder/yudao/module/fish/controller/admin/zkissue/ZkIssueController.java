package cn.iocoder.yudao.module.fish.controller.admin.zkissue;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
// import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.TextVo;
import cn.iocoder.yudao.module.fish.controller.admin.zkissue.vo.*;
// import cn.iocoder.yudao.module.fish.convert.zkissue.ZkIssueConvert;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkissue.ZkIssueDO;
import cn.iocoder.yudao.module.fish.service.aiagent.AiAgentService;
import cn.iocoder.yudao.module.fish.service.zkissue.ZkIssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
// import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 问题总结")
@RestController
@RequestMapping("/fish/zk-issue")
@Validated
public class ZkIssueController {

    @Resource
    private ZkIssueService zkIssueService;
    @Autowired
    private AiAgentService aiAgentService;

    @PostMapping("/create")
    @Operation(summary = "创建问题总结")
    // @DataPermission(enable = false)
    //@PreAuthorize("@ss.hasPermission('pl:zk-issue:create')")
    public CommonResult<Long> createZkIssue(@Valid @RequestBody ZkIssueCreateReqVO createReqVO) {
        // 1. 同步到知识库
        if(StringUtils.equals(createReqVO.getStatus(), "1")) {
            String q_id = UUID.randomUUID().toString();
            String text = String.format("问题标题：%s，问题描述:%s，总结标题:%s，总结内容:%s", createReqVO.getIssueTitle(),
                    createReqVO.getIssueContent(), createReqVO.getSummaryTitle(), createReqVO.getSummaryContent());
            aiAgentService.add_text(new TextVo(q_id, text));
            createReqVO.setAgentId(q_id);
        }
        // 2. 添加数据
        return success(zkIssueService.createZkIssue(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新问题总结")
    // @DataPermission(enable = false)
    //@PreAuthorize("@ss.hasPermission('pl:zk-issue:update')")
    public CommonResult<Boolean> updateZkIssue(@Valid @RequestBody ZkIssueUpdateReqVO updateReqVO) {
        // 1. 同步到知识库
        if(StringUtils.equals(updateReqVO.getStatus(), "1")) {
            String q_id = StringUtil.isEmptyWith0(updateReqVO.getAgentId())?UUID.randomUUID().toString():updateReqVO.getAgentId();
            String text = String.format("问题标题：%s，问题描述:%s，总结标题:%s，总结内容:%s", updateReqVO.getIssueTitle(),
                    updateReqVO.getIssueContent(), updateReqVO.getSummaryTitle(), updateReqVO.getSummaryContent());
            aiAgentService.add_text(new TextVo(q_id, text));
            updateReqVO.setAgentId(q_id);
        } else if(!StringUtils.equals(updateReqVO.getStatus(), "1") && StringUtils.isNotEmpty(updateReqVO.getAgentId())) {
            aiAgentService.delete_text(new TextVo(updateReqVO.getAgentId(), ""));
            updateReqVO.setAgentId("0");
        }
        zkIssueService.updateZkIssue(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除问题总结")
    @Parameter(name = "id", description = "编号", required = true)
    // @DataPermission(enable = false)
    //@PreAuthorize("@ss.hasPermission('pl:zk-issue:delete')")
    public CommonResult<Boolean> deleteZkIssue(@RequestParam("id") Long id) {
        ZkIssueDO item = zkIssueService.deleteZkIssue(id);
        // 2. 删除知识库
        if(!StringUtil.isEmptyWith0(item.getAgentId())) {
            aiAgentService.delete_text(new TextVo(item.getAgentId(), ""));
        }
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得问题总结")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    // @DataPermission(enable = false)
    //@PreAuthorize("@ss.hasPermission('pl:zk-issue:query')")
    public CommonResult<ZkIssueRespVO> getZkIssue(@RequestParam("id") Long id) {
        ZkIssueDO zkIssue = zkIssueService.getZkIssue(id);
        return success(BeanUtils.toBean(zkIssue, ZkIssueRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得问题总结列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    // @DataPermission(enable = false)
    //@PreAuthorize("@ss.hasPermission('pl:zk-issue:query')")
    public CommonResult<List<ZkIssueRespVO>> getZkIssueList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkIssueDO> list = zkIssueService.getZkIssueList(ids);
        return success(BeanUtils.toBean(list, ZkIssueRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得问题总结分页")
    // @DataPermission(enable = false)
    //@PreAuthorize("@ss.hasPermission('pl:zk-issue:query')")
    public CommonResult<PageResult<ZkIssueRespVO>> getZkIssuePage(@Valid ZkIssuePageReqVO pageVO) {
        PageResult<ZkIssueDO> pageResult = zkIssueService.getZkIssuePage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkIssueRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出问题总结 Excel")
    // @DataPermission(enable = false)
    //@PreAuthorize("@ss.hasPermission('pl:zk-issue:export')")
    // @OperateLog(type = EXPORT)
    public void exportZkIssueExcel(@Valid ZkIssueExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkIssueDO> list = zkIssueService.getZkIssueList(exportReqVO);
        // 导出 Excel
        List<ZkIssueExcelVO> datas = BeanUtils.toBean(list, ZkIssueExcelVO.class);
        ExcelUtils.write(response, "问题总结.xls", "数据", ZkIssueExcelVO.class, datas);
    }

}
