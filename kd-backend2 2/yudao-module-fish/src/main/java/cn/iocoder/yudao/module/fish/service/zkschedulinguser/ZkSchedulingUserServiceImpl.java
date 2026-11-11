package cn.iocoder.yudao.module.fish.service.zkschedulinguser;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkschedulinguser.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulinguser.ZkSchedulingUserDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkschedulinguser.ZkSchedulingUserConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkschedulinguser.ZkSchedulingUserMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 排班用户 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkSchedulingUserServiceImpl implements ZkSchedulingUserService {

    @Resource
    private ZkSchedulingUserMapper zkSchedulingUserMapper;

    @Override
    public Long createZkSchedulingUser(ZkSchedulingUserCreateReqVO createReqVO) {
        // 插入
        ZkSchedulingUserDO zkSchedulingUser = BeanUtils.toBean(createReqVO, ZkSchedulingUserDO.class);
        zkSchedulingUserMapper.insert(zkSchedulingUser);
        // 返回
        return zkSchedulingUser.getId();
    }

    @Override
    public void updateZkSchedulingUser(ZkSchedulingUserUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkSchedulingUserExists(updateReqVO.getId());
        // 更新
        ZkSchedulingUserDO updateObj = BeanUtils.toBean(updateReqVO, ZkSchedulingUserDO.class);
        zkSchedulingUserMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkSchedulingUser(Long id) {
        // 校验存在
        validateZkSchedulingUserExists(id);
        // 删除
        zkSchedulingUserMapper.deleteById(id);
    }

    private void validateZkSchedulingUserExists(Long id) {
        if (zkSchedulingUserMapper.selectById(id) == null) {
            throw exception(ZK_SCHEDULING_USER_NOT_EXISTS);
        }
    }

    @Override
    public ZkSchedulingUserDO getZkSchedulingUser(Long id) {
        return zkSchedulingUserMapper.selectById(id);
    }

    @Override
    public List<ZkSchedulingUserDO> getZkSchedulingUserList(Collection<Long> ids) {
        return zkSchedulingUserMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkSchedulingUserDO> getZkSchedulingUserPage(ZkSchedulingUserPageReqVO pageReqVO) {
        return zkSchedulingUserMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkSchedulingUserDO> getZkSchedulingUserList(ZkSchedulingUserExportReqVO exportReqVO) {
        return zkSchedulingUserMapper.selectList(exportReqVO);
    }

}
