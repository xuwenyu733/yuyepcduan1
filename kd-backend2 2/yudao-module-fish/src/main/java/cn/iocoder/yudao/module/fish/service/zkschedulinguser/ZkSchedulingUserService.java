package cn.iocoder.yudao.module.fish.service.zkschedulinguser;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkschedulinguser.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulinguser.ZkSchedulingUserDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 排班用户 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkSchedulingUserService {

    /**
     * 创建排班用户
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkSchedulingUser(@Valid ZkSchedulingUserCreateReqVO createReqVO);

    /**
     * 更新排班用户
     *
     * @param updateReqVO 更新信息
     */
    void updateZkSchedulingUser(@Valid ZkSchedulingUserUpdateReqVO updateReqVO);

    /**
     * 删除排班用户
     *
     * @param id 编号
     */
    void deleteZkSchedulingUser(Long id);

    /**
     * 获得排班用户
     *
     * @param id 编号
     * @return 排班用户
     */
    ZkSchedulingUserDO getZkSchedulingUser(Long id);

    /**
     * 获得排班用户列表
     *
     * @param ids 编号
     * @return 排班用户列表
     */
    List<ZkSchedulingUserDO> getZkSchedulingUserList(Collection<Long> ids);

    /**
     * 获得排班用户分页
     *
     * @param pageReqVO 分页查询
     * @return 排班用户分页
     */
    PageResult<ZkSchedulingUserDO> getZkSchedulingUserPage(ZkSchedulingUserPageReqVO pageReqVO);

    /**
     * 获得排班用户列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 排班用户列表
     */
    List<ZkSchedulingUserDO> getZkSchedulingUserList(ZkSchedulingUserExportReqVO exportReqVO);

}
