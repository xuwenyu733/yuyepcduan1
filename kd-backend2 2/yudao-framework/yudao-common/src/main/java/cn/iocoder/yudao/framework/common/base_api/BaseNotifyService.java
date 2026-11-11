package cn.iocoder.yudao.framework.common.base_api;

import java.util.List;
import java.util.Map;

/**
 * 站内信 通用接口
 */
public interface BaseNotifyService {

    Map<String, Object> insertNotify(Long userId, String msg);

    /**
     * 添加 通知
     * @param map 通知参数 userId , msg , pageUrl
     * @return 通知的内容
     */
    Map<String, Object> insertNotifyByMap(Map<String, String> map);

    /**
     * 避免类过多，使用Map通信，do位置： cn.iocoder.yudao.module.system.dal.dataobject.notify.NotifyMessageDO
     * @param userId
     * @return
     */
    List<Map<String, Object>> selectNeedNotify(Long userId);

     /**
     * 判断用户是否在线
     * @param userId 用户id
     * @return  true 在线，false 不在线
     */
    boolean judgeUserIsOnLine (Long userId);

     /**
     * 发送 App 离线通知
     * @param userId 用户id
     * @param msgId 消息id
     * @param title 标题
     * @param msg 内容
     * @return 通知结果
     */
    String sendAppOfflineNotify(Long userId, String msgId, String title, String msg);

}
