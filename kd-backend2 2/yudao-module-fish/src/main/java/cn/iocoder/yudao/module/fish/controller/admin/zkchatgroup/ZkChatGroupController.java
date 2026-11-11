package cn.iocoder.yudao.module.fish.controller.admin.zkchatgroup;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatgroup.vo.ZkChatGroupPageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatgroup.vo.ZkChatGroupRespVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatgroup.vo.ZkChatGroupSaveReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroup.ZkChatGroupDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroupuser.ZkChatGroupUserDO;
import cn.iocoder.yudao.module.fish.service.zkchatgroup.ZkChatGroupService;
import cn.iocoder.yudao.module.fish.service.zkchatgroupuser.ZkChatGroupUserService;
import cn.iocoder.yudao.module.infra.websocket.WebSocketConfig;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 群聊组")
@RestController
@RequestMapping("/fish/zk-chat-group")
@Validated
public class ZkChatGroupController {

    @Resource
    private ZkChatGroupService zkChatGroupService;

    @Resource
    private ZkChatGroupUserService zkChatGroupUserService;

    @PostMapping("/create")
    @Operation(summary = "创建群聊组")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-group:create')")
    public CommonResult<Long> createZkChatGroup(@Valid @RequestBody ZkChatGroupSaveReqVO createReqVO) {
        // 1. 创建 群聊
        ZkChatGroupDO zkChatGroup = zkChatGroupService.createZkChatGroup(createReqVO);
        if(EasyListUtil.isEmpty(createReqVO.getUserList())) return success(zkChatGroup.getId());
        // 2. 创建 关联成员
        List<ZkChatGroupUserDO> groupUserDOS = createReqVO.getUserList().stream().map(obj -> {
            return new ZkChatGroupUserDO(zkChatGroup.getId(), obj.getUserName(), 0, obj.getId(), obj.getDeptId());
        }).toList();
        zkChatGroupUserService.customSaveOrUpdate(zkChatGroup.getId(), groupUserDOS);
        return success(zkChatGroup.getId());
    }

    @PutMapping("/update")
    @Operation(summary = "更新群聊组")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-group:update')")
    public CommonResult<Boolean> updateZkChatGroup(@Valid @RequestBody ZkChatGroupSaveReqVO updateReqVO) {
        // 1. 执行更新操作
        zkChatGroupService.updateZkChatGroup(updateReqVO);
        if(EasyListUtil.isEmpty(updateReqVO.getUserList())) success(true);
        // 2. 同步对应关联成员
        List<ZkChatGroupUserDO> groupUserDOS = updateReqVO.getUserList().stream().map(obj -> {
            return new ZkChatGroupUserDO(updateReqVO.getId(), obj.getUserName(), 0, obj.getId(), obj.getDeptId());
        }).toList();
        zkChatGroupUserService.customSaveOrUpdate(updateReqVO.getId(), groupUserDOS);

        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除群聊组")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-group:delete')")
    public CommonResult<Boolean> deleteZkChatGroup(@RequestParam("id") Long id) {
        zkChatGroupService.deleteZkChatGroup(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除群聊组")
                @PreAuthorize("@ss.hasPermission('fish:zk-chat-group:delete')")
    public CommonResult<Boolean> deleteZkChatGroupList(@RequestParam("ids") List<Long> ids) {
        zkChatGroupService.deleteZkChatGroupListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得群聊组")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-group:query')")
    public CommonResult<ZkChatGroupRespVO> getZkChatGroup(@RequestParam("id") Long id) {
        ZkChatGroupDO zkChatGroup = zkChatGroupService.getZkChatGroup(id);
        enhancerList(List.of(zkChatGroup));
        return success(BeanUtils.toBean(zkChatGroup, ZkChatGroupRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得群聊组分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-group:query')")
    public CommonResult<PageResult<ZkChatGroupRespVO>> getZkChatGroupPage(@Valid ZkChatGroupPageReqVO pageReqVO) {
        // 1. 设置 当前 用户的 id
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        pageReqVO.setGroupUserId(loginUserId);
        PageResult<ZkChatGroupDO> pageResult = zkChatGroupService.getZkChatGroupPage(pageReqVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkChatGroupRespVO.class));
    }


    @GetMapping("/page4manager")
    @Operation(summary = "获得群聊组分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-group:query')")
    public CommonResult<PageResult<ZkChatGroupRespVO>> page4manager(@Valid ZkChatGroupPageReqVO pageReqVO) {
        PageResult<ZkChatGroupDO> pageResult = zkChatGroupService.getZkChatGroupPage(pageReqVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkChatGroupRespVO.class));
    }

    // 分组 求 List<map>， 然后再汇总
    @SuppressWarnings("unchecked")
    public void enhancerList(List<ZkChatGroupDO> tList) {
        enhancerListUserName(tList);
        // 1. 获取 关联 值
        Set<Long> oIds = EasyListUtil.listToIds(tList, ZkChatGroupDO::getId);
        // 2. 利用 关联值 查询
        List<ZkChatGroupUserDO> groupUserDOS = zkChatGroupUserService.getByGroupIds(oIds);
        groupUserDOS = removeRepeatUser(oIds, groupUserDOS); // 去掉重复用户
        List<Map<String, Object>> oList = EasyListUtil.one2manyToList(groupUserDOS, ZkChatGroupUserDO::getGroupId,
                EasyListUtil.getList(ZkChatGroupUserDO::getUserName), List.of("userNames")); // 分组汇总
        List<Map<String, Object>> o_userListMap = EasyListUtil.one2manyToList3(groupUserDOS, ZkChatGroupUserDO::getGroupId, "userList");
        // 3. 赋值
        CommonUtil.originList2Targets(oList, "groupId", List.of("userNames"), tList, "id", List.of("userNames"));
        CommonUtil.originList2Targets3(o_userListMap, "groupId", tList, "id", (Map<String, Object> o, ZkChatGroupDO t) -> {
            t.setUserList((List<ZkChatGroupUserDO>) o.get("userList"));
        });
    }

    public List<ZkChatGroupUserDO> removeRepeatUser(Set<Long> oIds, List<ZkChatGroupUserDO> groupUserDOS) {
        if(EasyListUtil.isEmpty(groupUserDOS)) return groupUserDOS;
        // 1. 获取 重复的
        Map<Long, List<ZkChatGroupUserDO>> collect = groupUserDOS.stream().collect(Collectors.groupingBy(ZkChatGroupUserDO::getGroupId));
        List<Long> needRemove = new ArrayList<>();
        for(Long groupId : collect.keySet()) {
            List<ZkChatGroupUserDO> tempUserList = collect.get(groupId);
            List<Long> repeat = EasyListUtil.getRepeat(tempUserList, ZkChatGroupUserDO::getUserId, ZkChatGroupUserDO::getId);
            if(EasyListUtil.isEmpty(repeat)) continue;
            needRemove.addAll(repeat);
        }
        // 2. 执行删除逻辑
        if(EasyListUtil.isEmpty(needRemove)) return groupUserDOS;
        zkChatGroupUserService.deleteZkChatGroupUserListByIds(needRemove);
        return zkChatGroupUserService.getByGroupIds(oIds);
    }

    @SuppressWarnings("unchecked")
    public void enhancerListUserName(List<ZkChatGroupDO> tList) {
        // 1. 获取 关联 值
        Set<Long> oIds = EasyListUtil.listToIds(tList, ZkChatGroupDO::getId);
        // 2. 利用 关联值 查询
        List<ZkChatGroupUserDO> zkChatGroupDOS = zkChatGroupUserService.getByGroupIds(oIds);
        if(EasyListUtil.isEmpty(zkChatGroupDOS)) return;
        Set<Long> userIds = zkChatGroupDOS.stream().filter(obj -> StringUtil.isEmptyWith0(obj.getUserName())).map(ZkChatGroupUserDO::getUserId).collect(Collectors.toSet());
        if(CollectionUtils.isAnyEmpty(userIds)) return;
        Map<Long, Map<String, Object>> fullUserInfo = WebSocketConfig.baseUserService.getFullUserInfo(new ArrayList<>(userIds));
        // 3. 赋值 并 设置值
        List<ZkChatGroupUserDO> groupUserDOS = zkChatGroupDOS.stream().filter(obj -> userIds.contains(obj.getUserId())).map(obj -> {
            MapUtils.findAndThen(fullUserInfo, obj.getUserId(), o -> obj.setUserName(o.get("nickname") + ""));
            return obj;
        }).toList();
        zkChatGroupUserService.customUpdate(groupUserDOS);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出群聊组 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-chat-group:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkChatGroupExcel(@Valid ZkChatGroupPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkChatGroupDO> list = zkChatGroupService.getZkChatGroupPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "群聊组.xls", "数据", ZkChatGroupRespVO.class,
                        BeanUtils.toBean(list, ZkChatGroupRespVO.class));
    }

}