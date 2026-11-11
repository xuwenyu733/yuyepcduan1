package cn.iocoder.yudao.module.fish.controller.admin.zkchatmsg;

import cn.hutool.core.lang.func.Func1;
import cn.iocoder.yudao.framework.common.util.easy.EasyDataMapper;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroup.ZkChatGroupDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroupuser.ZkChatGroupUserDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsgreceive.ZkChatMsgReceiveDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkchatgroup.ZkChatGroupMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkchatmsg.ZkChatMsgMapper;
import cn.iocoder.yudao.module.fish.service.zkchatmsgreceive.ZkChatMsgReceiveService;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
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
import java.util.function.BiConsumer;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkchatmsg.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsg.ZkChatMsgDO;
import cn.iocoder.yudao.module.fish.service.zkchatmsg.ZkChatMsgService;

@Tag(name = "管理后台 - 聊天消息")
@RestController
@RequestMapping("/fish/zk-chat-msg")
@Validated
public class ZkChatMsgController {

    @Resource
    private ZkChatMsgService zkChatMsgService;

    @Resource
    private ZkChatGroupMapper zkChatGroupMapper;

    @Resource
    private AdminUserMapper adminUserMapper;

    @Resource
    private ZkChatMsgReceiveService zkChatMsgReceiveService;

    @PostMapping("/create")
    @Operation(summary = "创建聊天消息")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-msg:create')")
    @DataPermission(enable = false)
    public CommonResult<Long> createZkChatMsg(@Valid @RequestBody ZkChatMsgSaveReqVO createReqVO) {
        // 1. 添加 消息
        ZkChatMsgDO zkChatMsg = zkChatMsgService.createZkChatMsg(createReqVO);
        // 2. 同步 消息 receive
        List<ZkChatMsgReceiveDO> zkChatMsgReceiveDOS = zkChatMsgReceiveService.customInsert(zkChatMsg);
        // 3. 消息 通知
        zkChatMsgReceiveService.notifyMsgList(zkChatMsgReceiveDOS);
        return success(zkChatMsg.getId());
    }

    @PutMapping("/update")
    @Operation(summary = "更新聊天消息")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-msg:update')")
    @DataPermission(enable = false)
    public CommonResult<Boolean> updateZkChatMsg(@Valid @RequestBody ZkChatMsgSaveReqVO updateReqVO) {
        zkChatMsgService.updateZkChatMsg(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除聊天消息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-msg:delete')")
    public CommonResult<Boolean> deleteZkChatMsg(@RequestParam("id") Long id) {
        zkChatMsgService.deleteZkChatMsg(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除聊天消息")
                @PreAuthorize("@ss.hasPermission('fish:zk-chat-msg:delete')")
    public CommonResult<Boolean> deleteZkChatMsgList(@RequestParam("ids") List<Long> ids) {
        zkChatMsgService.deleteZkChatMsgListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得聊天消息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-msg:query')")
    public CommonResult<ZkChatMsgRespVO> getZkChatMsg(@RequestParam("id") Long id) {
        ZkChatMsgDO zkChatMsg = zkChatMsgService.getZkChatMsg(id);
        return success(BeanUtils.toBean(zkChatMsg, ZkChatMsgRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得聊天消息分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-msg:query')")
    public CommonResult<PageResult<ZkChatMsgRespVO>> getZkChatMsgPage(@Valid ZkChatMsgPageReqVO pageReqVO) {
        PageResult<ZkChatMsgDO> pageResult = zkChatMsgService.getZkChatMsgPage(pageReqVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkChatMsgRespVO.class));
    }

    @SuppressWarnings("unchecked")
    public void enhancerList(List<ZkChatMsgDO> list) {
        // 1. 获取 关联 值
        Set<Long> groupIds = EasyListUtil.listToIds(list, ZkChatMsgDO::getGroupId);
        // 2. 利用 关联值 查询
        List<ZkChatGroupDO> zkChatGroupDOS = EasyDataMapper.listByValueList(zkChatGroupMapper, ZkChatGroupDO::getId, groupIds);
        // 3. 赋值
        CommonUtil.origins2Targets4(zkChatGroupDOS, ZkChatGroupDO::getId, EasyListUtil.getList(ZkChatGroupDO::getGroupName),
                list, ZkChatMsgDO::getGroupId, EasyListUtil.setList(ZkChatMsgDO::setGroupName));
        enhancerUserInfo(list);
    }
    @SuppressWarnings("unchecked")
    public void enhancerUserInfo(List<ZkChatMsgDO> tList) {
        // 1. 获取 关联 值
        Set<Long> oIds = EasyListUtil.listToIds(tList, ZkChatMsgDO::getTargetUserId);
        // 2. 利用 关联值 查询
        List<AdminUserDO> oList = EasyDataMapper.listByValueList(adminUserMapper, AdminUserDO::getId, oIds);
        // 3. 赋值
        CommonUtil.origins2Targets3(oList, "id", List.of("nickname", "deptId"), tList, "targetUserId",
                List.of("targetUserName", "targetDeptId"));
        CommonUtil.origins2Targets4(oList, AdminUserDO::getId, EasyListUtil.getList(AdminUserDO::getNickname),
                tList, ZkChatMsgDO::getUserId, EasyListUtil.setList(ZkChatMsgDO::setUserName));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出聊天消息 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-msg:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkChatMsgExcel(@Valid ZkChatMsgPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkChatMsgDO> list = zkChatMsgService.getZkChatMsgPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "聊天消息.xls", "数据", ZkChatMsgRespVO.class,
                        BeanUtils.toBean(list, ZkChatMsgRespVO.class));
    }

}