package cn.iocoder.yudao.module.fish.service.zkchatmsg;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatmsg.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsg.ZkChatMsgDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 聊天消息 Service 接口
 *
 * @author admin2
 */
public interface ZkChatMsgService {

    /**
     * 创建聊天消息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    ZkChatMsgDO createZkChatMsg(@Valid ZkChatMsgSaveReqVO createReqVO);

    /**
     * 更新聊天消息
     *
     * @param updateReqVO 更新信息
     */
    void updateZkChatMsg(@Valid ZkChatMsgSaveReqVO updateReqVO);

    /**
     * 删除聊天消息
     *
     * @param id 编号
     */
    void deleteZkChatMsg(Long id);

    /**
    * 批量删除聊天消息
    *
    * @param ids 编号
    */
    void deleteZkChatMsgListByIds(List<Long> ids);

    /**
     * 获得聊天消息
     *
     * @param id 编号
     * @return 聊天消息
     */
    ZkChatMsgDO getZkChatMsg(Long id);

    /**
     * 获得聊天消息分页
     *
     * @param pageReqVO 分页查询
     * @return 聊天消息分页
     */
    PageResult<ZkChatMsgDO> getZkChatMsgPage(ZkChatMsgPageReqVO pageReqVO);

    List<ZkChatMsgDO> getByIds(Set<Long> chatMsgIdSet);

}