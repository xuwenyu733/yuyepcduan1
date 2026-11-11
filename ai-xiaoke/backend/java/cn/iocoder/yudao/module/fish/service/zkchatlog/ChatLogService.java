package cn.iocoder.yudao.module.fish.service.zkchatlog;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatlog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatlog.ChatLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 聊天日志 Service 接口
 *
 * @author 系统管理员
 */
public interface ChatLogService {

    /**
     * 创建聊天日志
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createChatLog(@Valid ChatLogCreateReqVO createReqVO);

    /**
     * 更新聊天日志
     *
     * @param updateReqVO 更新信息
     */
    void updateChatLog(@Valid ChatLogUpdateReqVO updateReqVO);

    /**
     * 删除聊天日志
     *
     * @param id 编号
     */
    void deleteChatLog(Long id);

    /**
     * 获得聊天日志
     *
     * @param id 编号
     * @return 聊天日志
     */
    ChatLogDO getChatLog(Long id);

    /**
     * 获得聊天日志列表
     *
     * @param ids 编号
     * @return 聊天日志列表
     */
    List<ChatLogDO> getChatLogList(Collection<Long> ids);

    /**
     * 获得聊天日志分页
     *
     * @param pageReqVO 分页查询
     * @return 聊天日志分页
     */
    PageResult<ChatLogDO> getChatLogPage(ChatLogPageReqVO pageReqVO);

    /**
     * 获得聊天日志列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 聊天日志列表
     */
    List<ChatLogDO> getChatLogList(ChatLogExportReqVO exportReqVO);

    Integer customSaveList(List<ChatLogDO> logDOS);

    void customBatchUpdate(List<ChatLogDO> asList);

    ChatLogDO getByUserIdAndFid(Long userId, String roleName, String fid);

    List<ChatLogDO> getByUserId(Long userId);
}
