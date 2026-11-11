package cn.iocoder.yudao.module.fish.controller.admin.zkchatlog;

import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkchatlog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatlog.ChatLogDO;
// import cn.iocoder.yudao.module.fish.convert.zkchatlog.ChatLogConvert;
import cn.iocoder.yudao.module.fish.service.zkchatlog.ChatLogService;

@Tag(name = "管理后台 - 聊天日志")
@RestController
@RequestMapping("/fish/chat-log")
@Validated
public class ChatLogController {

    @Resource
    private ChatLogService chatLogService;

    @PostMapping("/create")
    @Operation(summary = "创建聊天日志")
    public CommonResult<Long> createChatLog(@Valid @RequestBody ChatLogCreateReqVO createReqVO) {
        return success(chatLogService.createChatLog(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新聊天日志")
    public CommonResult<Boolean> updateChatLog(@Valid @RequestBody ChatLogUpdateReqVO updateReqVO) {
        if(updateReqVO.getId() == null && updateReqVO.getUserId()!=null && StringUtils.isNotBlank(updateReqVO.getFid())) {
            ChatLogDO chatLogDO = chatLogService.getByUserIdAndFid(updateReqVO.getUserId(), ChatLogBuilder.AI_NAME, updateReqVO.getFid());
            if(chatLogDO == null)
                return success(false);
            CommonUtil.copyPropertiesIgnoreNull(updateReqVO, chatLogDO);
            chatLogService.customBatchUpdate(Arrays.asList(chatLogDO));
        } else {
            chatLogService.updateChatLog(updateReqVO);
        }
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除聊天日志")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteChatLog(@RequestParam("id") Long id) {
        chatLogService.deleteChatLog(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得聊天日志")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ChatLogRespVO> getChatLog(@RequestParam("id") Long id) {
        ChatLogDO chatLog = chatLogService.getChatLog(id);
        return success(BeanUtils.toBean(chatLog, ChatLogRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得聊天日志列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ChatLogRespVO>> getChatLogList(@RequestParam("ids") Collection<Long> ids) {
        List<ChatLogDO> list = chatLogService.getChatLogList(ids);
        return success(BeanUtils.toBean(list, ChatLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得聊天日志分页")
    public CommonResult<PageResult<ChatLogRespVO>> getChatLogPage(@Valid ChatLogPageReqVO pageVO) {
        PageResult<ChatLogDO> pageResult = chatLogService.getChatLogPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ChatLogRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出聊天日志 Excel")
    // @OperateLog(type = EXPORT)
    public void exportChatLogExcel(@Valid ChatLogExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ChatLogDO> list = chatLogService.getChatLogList(exportReqVO);
        // 导出 Excel
        List<ChatLogExcelVO> datas = BeanUtils.toBean(list, ChatLogExcelVO.class);
        ExcelUtils.write(response, "聊天日志.xls", "数据", ChatLogExcelVO.class, datas);
    }

}
