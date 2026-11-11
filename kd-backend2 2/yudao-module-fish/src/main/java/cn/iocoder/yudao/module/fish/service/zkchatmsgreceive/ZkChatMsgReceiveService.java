package cn.iocoder.yudao.module.fish.service.zkchatmsgreceive;

import java.util.*;

import cn.iocoder.yudao.framework.common.base_api.fish.BaseChatMsgReceive;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsg.ZkChatMsgDO;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatmsgreceive.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsgreceive.ZkChatMsgReceiveDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 聊天消息接受 Service 接口
 *
 * @author admin2
 */
public interface ZkChatMsgReceiveService extends BaseChatMsgReceive {

    /**
     * 创建聊天消息接受
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkChatMsgReceive(@Valid ZkChatMsgReceiveSaveReqVO createReqVO);

    /**
     * 更新聊天消息接受
     *
     * @param updateReqVO 更新信息
     */
    void updateZkChatMsgReceive(@Valid ZkChatMsgReceiveSaveReqVO updateReqVO);

    /**
     * 删除聊天消息接受
     *
     * @param id 编号
     */
    void deleteZkChatMsgReceive(Long id);

    /**
    * 批量删除聊天消息接受
    *
    * @param ids 编号
    */
    void deleteZkChatMsgReceiveListByIds(List<Long> ids);

    /**
     * 获得聊天消息接受
     *
     * @param id 编号
     * @return 聊天消息接受
     */
    ZkChatMsgReceiveDO getZkChatMsgReceive(Long id);

    /**
     * 获得聊天消息接受分页
     *
     * @param pageReqVO 分页查询
     * @return 聊天消息接受分页
     */
    PageResult<ZkChatMsgReceiveDO> getZkChatMsgReceivePage(ZkChatMsgReceivePageReqVO pageReqVO);

    List<ZkChatMsgReceiveDO> customInsert(ZkChatMsgDO zkChatMsg);

    /**
     * websocket通知用户消息
     * @param zkChatMsgReceiveDOS 用户消息接受情况
     */
    boolean notifyMsgList(List<ZkChatMsgReceiveDO> zkChatMsgReceiveDOS);

    List<ZkChatMsgReceiveDO> getNeedNoticeMsgList(String userId);
}