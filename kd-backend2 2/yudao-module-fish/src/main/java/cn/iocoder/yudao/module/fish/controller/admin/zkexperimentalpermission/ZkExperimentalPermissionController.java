package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpermission;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.*;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpermission.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpermission.*;
import cn.iocoder.yudao.module.fish.service.zkexperimentalpermission.ZkExperimentalPermissionService;

@Tag(name = "管理后台 - 实验权限")
@RestController
@RequestMapping("/fish/zk-experimental-permission")
@Validated
public class ZkExperimentalPermissionController {

    @Resource
    private ZkExperimentalPermissionService zkExperimentalPermissionService;

    @GetMapping("/roles")
    @Operation(summary = "获得实验权限角色列表")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-permission:query')")
    public CommonResult<List<ZkExperimentalPermissionRoleRespVO>> getRoles() {
        List<ZkExperimentalPermissionRoleDO> list = zkExperimentalPermissionService.getRoleList();
        return success(BeanUtils.toBean(list, ZkExperimentalPermissionRoleRespVO.class));
    }

    @GetMapping("/users")
    @Operation(summary = "获得实验权限用户列表")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-permission:query')")
    public CommonResult<List<ZkExperimentalPermissionUserRespVO>> getUsers(@Valid ZkExperimentalPermissionUserPageReqVO pageReqVO) {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkExperimentalPermissionUserDO> list = zkExperimentalPermissionService.getUserList(pageReqVO);
        return success(BeanUtils.toBean(list, ZkExperimentalPermissionUserRespVO.class));
    }

    @GetMapping("/users/page")
    @Operation(summary = "获得实验权限用户分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-permission:query')")
    public CommonResult<PageResult<ZkExperimentalPermissionUserRespVO>> getUserPage(@Valid ZkExperimentalPermissionUserPageReqVO pageReqVO) {
        PageResult<ZkExperimentalPermissionUserDO> pageResult = zkExperimentalPermissionService.getUserPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkExperimentalPermissionUserRespVO.class));
    }

    @PostMapping("/users")
    @Operation(summary = "创建实验权限用户")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-permission:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody ZkExperimentalPermissionUserSaveReqVO createReqVO) {
        return success(zkExperimentalPermissionService.createUser(createReqVO));
    }

    @PutMapping({"/users", "/users/{id}"})
    @Operation(summary = "更新实验权限用户")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-permission:update')")
    public CommonResult<Boolean> updateUser(@PathVariable(value = "id", required = false) Long pathId,
                                            @Valid @RequestBody ZkExperimentalPermissionUserSaveReqVO updateReqVO) {
        if (pathId != null) {
            updateReqVO.setId(pathId);
        }
        zkExperimentalPermissionService.updateUser(updateReqVO);
        return success(true);
    }

    @DeleteMapping({"/users", "/users/{id}"})
    @Operation(summary = "删除实验权限用户")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-permission:delete')")
    public CommonResult<Boolean> deleteUser(@PathVariable(value = "id", required = false) Long pathId,
                                            @RequestParam(value = "id", required = false) Long id) {
        Long userId = pathId != null ? pathId : id;
        zkExperimentalPermissionService.deleteUser(userId);
        return success(true);
    }

    @GetMapping("/users/get")
    @Operation(summary = "获得实验权限用户")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-permission:query')")
    public CommonResult<ZkExperimentalPermissionUserRespVO> getUser(@RequestParam("id") Long id) {
        ZkExperimentalPermissionUserDO user = zkExperimentalPermissionService.getUser(id);
        return success(BeanUtils.toBean(user, ZkExperimentalPermissionUserRespVO.class));
    }

    @GetMapping("/logs")
    @Operation(summary = "获得实验权限操作日志分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-permission:query')")
    public CommonResult<PageResult<ZkExperimentalPermissionLogRespVO>> getLogs(@Valid ZkExperimentalPermissionLogPageReqVO pageReqVO) {
        PageResult<ZkExperimentalPermissionLogDO> pageResult = zkExperimentalPermissionService.getLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkExperimentalPermissionLogRespVO.class));
    }

    @GetMapping("/logs/list")
    @Operation(summary = "获得实验权限操作日志列表（兼容 BFF 直出数组）")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-permission:query')")
    public CommonResult<List<ZkExperimentalPermissionLogRespVO>> getLogList(@Valid ZkExperimentalPermissionLogPageReqVO pageReqVO) {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkExperimentalPermissionLogDO> list = zkExperimentalPermissionService.getLogList(pageReqVO);
        return success(BeanUtils.toBean(list, ZkExperimentalPermissionLogRespVO.class));
    }

}
