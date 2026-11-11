package cn.iocoder.yudao.module.fish.service.zkexperimentalpermission;

import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpermission.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpermission.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import java.util.List;

public interface ZkExperimentalPermissionService {

    List<ZkExperimentalPermissionRoleDO> getRoleList();

    Long createUser(@Valid ZkExperimentalPermissionUserSaveReqVO createReqVO);

    void updateUser(@Valid ZkExperimentalPermissionUserSaveReqVO updateReqVO);

    void deleteUser(Long id);

    ZkExperimentalPermissionUserDO getUser(Long id);

    PageResult<ZkExperimentalPermissionUserDO> getUserPage(ZkExperimentalPermissionUserPageReqVO pageReqVO);

    List<ZkExperimentalPermissionUserDO> getUserList(ZkExperimentalPermissionUserPageReqVO pageReqVO);

    PageResult<ZkExperimentalPermissionLogDO> getLogPage(ZkExperimentalPermissionLogPageReqVO pageReqVO);

    List<ZkExperimentalPermissionLogDO> getLogList(ZkExperimentalPermissionLogPageReqVO pageReqVO);

}
