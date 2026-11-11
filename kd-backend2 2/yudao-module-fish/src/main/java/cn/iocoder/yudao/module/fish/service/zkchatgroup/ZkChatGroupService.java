package cn.iocoder.yudao.module.fish.service.zkchatgroup;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatgroup.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroup.ZkChatGroupDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 群聊组 Service 接口
 *
 * @author admin2
 */
public interface ZkChatGroupService {

    /**
     * 创建群聊组
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    ZkChatGroupDO createZkChatGroup(@Valid ZkChatGroupSaveReqVO createReqVO);

    /**
     * 更新群聊组
     *
     * @param updateReqVO 更新信息
     */
    void updateZkChatGroup(@Valid ZkChatGroupSaveReqVO updateReqVO);

    /**
     * 删除群聊组
     *
     * @param id 编号
     */
    void deleteZkChatGroup(Long id);

    /**
    * 批量删除群聊组
    *
    * @param ids 编号
    */
    void deleteZkChatGroupListByIds(List<Long> ids);

    /**
     * 获得群聊组
     *
     * @param id 编号
     * @return 群聊组
     */
    ZkChatGroupDO getZkChatGroup(Long id);

    /**
     * 获得群聊组分页
     *
     * @param pageReqVO 分页查询
     * @return 群聊组分页
     */
    PageResult<ZkChatGroupDO> getZkChatGroupPage(ZkChatGroupPageReqVO pageReqVO);

    List<ZkChatGroupDO> getByIds(Set<Long> ids);

}