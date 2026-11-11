package cn.iocoder.yudao.module.fish.controller.admin.zkchatgroupuser;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.Supplier2;
import cn.iocoder.yudao.framework.common.util.easy.EasyDataMapper;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasySetter;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroup.ZkChatGroupDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkchatgroup.ZkChatGroupMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkchatgroupuser.ZkChatGroupUserMapper;
import cn.iocoder.yudao.module.fish.service.zkchatgroup.ZkChatGroupService;
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
import java.util.function.Consumer;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkchatgroupuser.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroupuser.ZkChatGroupUserDO;
import cn.iocoder.yudao.module.fish.service.zkchatgroupuser.ZkChatGroupUserService;

@Tag(name = "管理后台 - 群聊成员")
@RestController
@RequestMapping("/fish/zk-chat-group-user")
@Validated
public class ZkChatGroupUserController {

    @Resource
    private ZkChatGroupUserService zkChatGroupUserService;

    @Resource
    private ZkChatGroupService zkChatGroupService;

    @Resource
    private ZkChatGroupUserMapper zkChatGroupUserMapper;

    @Resource
    private ZkChatGroupMapper zkChatGroupMapper;

    @PostMapping("/create")
    @Operation(summary = "创建群聊成员")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-group-user:create')")
    public CommonResult<Long> createZkChatGroupUser(@Valid @RequestBody ZkChatGroupUserSaveReqVO createReqVO) {
        return success(zkChatGroupUserService.createZkChatGroupUser(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新群聊成员")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-group-user:update')")
    public CommonResult<Boolean> updateZkChatGroupUser(@Valid @RequestBody ZkChatGroupUserSaveReqVO updateReqVO) {
        zkChatGroupUserService.updateZkChatGroupUser(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除群聊成员")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-group-user:delete')")
    public CommonResult<Boolean> deleteZkChatGroupUser(@RequestParam("id") Long id) {
        zkChatGroupUserService.deleteZkChatGroupUser(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除群聊成员")
                @PreAuthorize("@ss.hasPermission('fish:zk-chat-group-user:delete')")
    public CommonResult<Boolean> deleteZkChatGroupUserList(@RequestParam("ids") List<Long> ids) {
        zkChatGroupUserService.deleteZkChatGroupUserListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得群聊成员")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-group-user:query')")
    public CommonResult<ZkChatGroupUserRespVO> getZkChatGroupUser(@RequestParam("id") Long id) {
        ZkChatGroupUserDO zkChatGroupUser = zkChatGroupUserService.getZkChatGroupUser(id);
        return success(BeanUtils.toBean(zkChatGroupUser, ZkChatGroupUserRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得群聊成员分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-group-user:query')")
    public CommonResult<PageResult<ZkChatGroupUserRespVO>> getZkChatGroupUserPage(@Valid ZkChatGroupUserPageReqVO pageReqVO) {
        PageResult<ZkChatGroupUserDO> pageResult = zkChatGroupUserService.getZkChatGroupUserPage(pageReqVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkChatGroupUserRespVO.class));
    }

    public void enhancerList(List<ZkChatGroupUserDO> list) {
        // 1. 获取 关联 值
        Set<Long> groupIds = EasyListUtil.listToIds(list, ZkChatGroupUserDO::getGroupId);
        // 2. 利用 关联值 查询
        List<ZkChatGroupDO> zkChatGroupDOS = EasyDataMapper.listByValueList(zkChatGroupMapper, ZkChatGroupDO::getId, groupIds);
        // 3. 赋值
        Func1<ZkChatGroupDO, String> getGroupName = ZkChatGroupDO::getGroupName;
        BiConsumer<ZkChatGroupUserDO, String> setGroupName = ZkChatGroupUserDO::setGroupName;

        CommonUtil.origins2Targets4(zkChatGroupDOS, ZkChatGroupDO::getId, List.of(getGroupName),
                list, ZkChatGroupUserDO::getGroupId, List.of(setGroupName));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出群聊成员 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-group-user:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkChatGroupUserExcel(@Valid ZkChatGroupUserPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkChatGroupUserDO> list = zkChatGroupUserService.getZkChatGroupUserPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "群聊成员.xls", "数据", ZkChatGroupUserRespVO.class,
                        BeanUtils.toBean(list, ZkChatGroupUserRespVO.class));
    }

}