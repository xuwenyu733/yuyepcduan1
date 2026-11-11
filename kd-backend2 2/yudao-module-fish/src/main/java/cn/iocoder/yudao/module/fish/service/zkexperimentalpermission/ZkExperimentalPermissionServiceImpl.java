package cn.iocoder.yudao.module.fish.service.zkexperimentalpermission;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpermission.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpermission.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentalpermission.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

@Service
@Validated
public class ZkExperimentalPermissionServiceImpl implements ZkExperimentalPermissionService {

    /** 勿命名为 roleMapper：会与 system 模块 RoleMapper Bean 名冲突导致启动失败 */
    @Resource
    private ZkExperimentalPermissionRoleMapper zkExperimentalPermissionRoleMapper;
    @Resource
    private ZkExperimentalPermissionUserMapper zkExperimentalPermissionUserMapper;
    @Resource
    private ZkExperimentalPermissionLogMapper zkExperimentalPermissionLogMapper;

    @Override
    public List<ZkExperimentalPermissionRoleDO> getRoleList() {
        return zkExperimentalPermissionRoleMapper.selectList(new LambdaQueryWrapper<ZkExperimentalPermissionRoleDO>()
                .eq(ZkExperimentalPermissionRoleDO::getDeleted, false)
                .orderByAsc(ZkExperimentalPermissionRoleDO::getId));
    }

    @Override
    public Long createUser(ZkExperimentalPermissionUserSaveReqVO createReqVO) {
        fillRoleName(createReqVO);
        ZkExperimentalPermissionUserDO user = BeanUtils.toBean(createReqVO, ZkExperimentalPermissionUserDO.class);
        if (user.getStatus() == null) {
            user.setStatus(0);
        }
        zkExperimentalPermissionUserMapper.insert(user);
        appendLog("新增用户", user.getUserName(), "分配角色: " + user.getRoleName());
        return user.getId();
    }

    @Override
    public void updateUser(ZkExperimentalPermissionUserSaveReqVO updateReqVO) {
        validateUserExists(updateReqVO.getId());
        fillRoleName(updateReqVO);
        ZkExperimentalPermissionUserDO updateObj = BeanUtils.toBean(updateReqVO, ZkExperimentalPermissionUserDO.class);
        zkExperimentalPermissionUserMapper.updateById(updateObj);
        appendLog("更新用户", updateObj.getUserName(), "更新权限配置");
    }

    @Override
    public void deleteUser(Long id) {
        ZkExperimentalPermissionUserDO user = zkExperimentalPermissionUserMapper.selectById(id);
        if (user == null) {
            throw exception(ZK_EXPERIMENTAL_PERMISSION_USER_NOT_EXISTS);
        }
        zkExperimentalPermissionUserMapper.deleteById(id);
        appendLog("删除用户", user.getUserName(), "移除实验系统权限");
    }

    @Override
    public ZkExperimentalPermissionUserDO getUser(Long id) {
        return zkExperimentalPermissionUserMapper.selectById(id);
    }

    @Override
    public PageResult<ZkExperimentalPermissionUserDO> getUserPage(ZkExperimentalPermissionUserPageReqVO pageReqVO) {
        return zkExperimentalPermissionUserMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkExperimentalPermissionUserDO> getUserList(ZkExperimentalPermissionUserPageReqVO pageReqVO) {
        return zkExperimentalPermissionUserMapper.selectPage(pageReqVO).getList();
    }

    @Override
    public PageResult<ZkExperimentalPermissionLogDO> getLogPage(ZkExperimentalPermissionLogPageReqVO pageReqVO) {
        return zkExperimentalPermissionLogMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkExperimentalPermissionLogDO> getLogList(ZkExperimentalPermissionLogPageReqVO pageReqVO) {
        return zkExperimentalPermissionLogMapper.selectPage(pageReqVO).getList();
    }

    private void fillRoleName(ZkExperimentalPermissionUserSaveReqVO reqVO) {
        if (reqVO.getRoleId() == null) {
            return;
        }
        ZkExperimentalPermissionRoleDO role = zkExperimentalPermissionRoleMapper.selectById(reqVO.getRoleId());
        if (role != null) {
            reqVO.setRoleName(role.getRoleName());
        }
    }

    private void validateUserExists(Long id) {
        if (zkExperimentalPermissionUserMapper.selectById(id) == null) {
            throw exception(ZK_EXPERIMENTAL_PERMISSION_USER_NOT_EXISTS);
        }
    }

    private void appendLog(String action, String target, String detail) {
        ZkExperimentalPermissionLogDO log = new ZkExperimentalPermissionLogDO();
        log.setAction(action);
        log.setTarget(target);
        log.setDetail(detail);
        try {
            Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
            log.setUserId(loginUserId);
        } catch (Exception ignored) {}
        zkExperimentalPermissionLogMapper.insert(log);
    }

}
