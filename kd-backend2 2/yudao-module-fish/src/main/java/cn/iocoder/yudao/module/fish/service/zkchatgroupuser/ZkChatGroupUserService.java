package cn.iocoder.yudao.module.fish.service.zkchatgroupuser;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatgroupuser.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroupuser.ZkChatGroupUserDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 群聊成员 Service 接口
 *
 * @author admin2
 */
public interface ZkChatGroupUserService {

    /**
     * 创建群聊成员
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkChatGroupUser(@Valid ZkChatGroupUserSaveReqVO createReqVO);

    /**
     * 更新群聊成员
     *
     * @param updateReqVO 更新信息
     */
    void updateZkChatGroupUser(@Valid ZkChatGroupUserSaveReqVO updateReqVO);

    /**
     * 删除群聊成员
     *
     * @param id 编号
     */
    void deleteZkChatGroupUser(Long id);

    /**
    * 批量删除群聊成员
    *
    * @param ids 编号
    */
    void deleteZkChatGroupUserListByIds(List<Long> ids);

    /**
     * 获得群聊成员
     *
     * @param id 编号
     * @return 群聊成员
     */
    ZkChatGroupUserDO getZkChatGroupUser(Long id);

    /**
     * 获得群聊成员分页
     *
     * @param pageReqVO 分页查询
     * @return 群聊成员分页
     */
    PageResult<ZkChatGroupUserDO> getZkChatGroupUserPage(ZkChatGroupUserPageReqVO pageReqVO);

    void customSaveOrUpdate(Long groupId, List<ZkChatGroupUserDO> groupUserDOS);
    void customUpdate(List<ZkChatGroupUserDO> groupUserDOS);

    List<ZkChatGroupUserDO> getByIds(Set<Long> ids);

    List<ZkChatGroupUserDO> getByGroupIds(Set<Long> groupId);
}