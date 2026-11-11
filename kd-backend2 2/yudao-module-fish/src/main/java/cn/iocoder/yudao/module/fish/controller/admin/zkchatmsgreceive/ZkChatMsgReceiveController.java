package cn.iocoder.yudao.module.fish.controller.admin.zkchatmsgreceive;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsg.ZkChatMsgDO;
import cn.iocoder.yudao.module.fish.service.zkchatmsg.ZkChatMsgService;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkchatmsgreceive.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsgreceive.ZkChatMsgReceiveDO;
import cn.iocoder.yudao.module.fish.service.zkchatmsgreceive.ZkChatMsgReceiveService;

@Tag(name = "管理后台 - 聊天消息接受")
@RestController
@RequestMapping("/fish/zk-chat-msg-receive")
@Validated
public class ZkChatMsgReceiveController {

    @Resource
    private ZkChatMsgReceiveService zkChatMsgReceiveService;

    @Resource
    private ZkChatMsgService zkChatMsgService;

    @PostMapping("/create")
    @Operation(summary = "创建聊天消息接受")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-msg-receive:create')")
    public CommonResult<Long> createZkChatMsgReceive(@Valid @RequestBody ZkChatMsgReceiveSaveReqVO createReqVO) {
        return success(zkChatMsgReceiveService.createZkChatMsgReceive(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新聊天消息接受")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-msg-receive:update')")
    public CommonResult<Boolean> updateZkChatMsgReceive(@Valid @RequestBody ZkChatMsgReceiveSaveReqVO updateReqVO) {
        zkChatMsgReceiveService.updateZkChatMsgReceive(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除聊天消息接受")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-msg-receive:delete')")
    public CommonResult<Boolean> deleteZkChatMsgReceive(@RequestParam("id") Long id) {
        zkChatMsgReceiveService.deleteZkChatMsgReceive(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除聊天消息接受")
                @PreAuthorize("@ss.hasPermission('fish:zk-chat-msg-receive:delete')")
    public CommonResult<Boolean> deleteZkChatMsgReceiveList(@RequestParam("ids") List<Long> ids) {
        zkChatMsgReceiveService.deleteZkChatMsgReceiveListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得聊天消息接受")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-msg-receive:query')")
    public CommonResult<ZkChatMsgReceiveRespVO> getZkChatMsgReceive(@RequestParam("id") Long id) {
        ZkChatMsgReceiveDO zkChatMsgReceive = zkChatMsgReceiveService.getZkChatMsgReceive(id);
        return success(BeanUtils.toBean(zkChatMsgReceive, ZkChatMsgReceiveRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得聊天消息接受分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-msg-receive:query')")
    public CommonResult<PageResult<ZkChatMsgReceiveRespVO>> getZkChatMsgReceivePage(@Valid ZkChatMsgReceivePageReqVO pageReqVO) {
        PageResult<ZkChatMsgReceiveDO> pageResult = zkChatMsgReceiveService.getZkChatMsgReceivePage(pageReqVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkChatMsgReceiveRespVO.class));
    }

    public void enhancerList(List<ZkChatMsgReceiveDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        Set<Long> msgIds = list.stream().map(ZkChatMsgReceiveDO::getChatMsgId).collect(Collectors.toSet());
        List<ZkChatMsgDO> byIds = zkChatMsgService.getByIds(msgIds);
        if(EasyListUtil.isEmpty(byIds)) return;
        CommonUtil.origins2Targets4(byIds, ZkChatMsgDO::getId, list, ZkChatMsgReceiveDO::getChatMsgId, (o, t) -> t.setZkChatMsgDO(o.build_msgInfo()));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出聊天消息接受 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-msg-receive:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkChatMsgReceiveExcel(@Valid ZkChatMsgReceivePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkChatMsgReceiveDO> list = zkChatMsgReceiveService.getZkChatMsgReceivePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "聊天消息接受.xls", "数据", ZkChatMsgReceiveRespVO.class,
                        BeanUtils.toBean(list, ZkChatMsgReceiveRespVO.class));
    }

}