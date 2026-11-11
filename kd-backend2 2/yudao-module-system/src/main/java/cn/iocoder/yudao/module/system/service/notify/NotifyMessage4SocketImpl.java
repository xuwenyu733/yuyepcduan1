package cn.iocoder.yudao.module.system.service.notify;

import cn.iocoder.yudao.framework.common.base_api.BaseWebSocketHandle;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyMap3;
import cn.iocoder.yudao.module.infra.websocket.WebSocketUsers;
import cn.iocoder.yudao.module.infra.websocket.vo.MsgItem;
import cn.iocoder.yudao.module.infra.websocket.vo.ReceiveOperateEnum;
import cn.iocoder.yudao.module.system.dal.dataobject.notify.NotifyMessageDO;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 每次心跳，需要通知 用户 未处理的消息
 */
@Service
@Slf4j
public class NotifyMessage4SocketImpl implements BaseWebSocketHandle {

    @Resource
    private NotifySendService notifySendService;

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> heartCheckHandle(String userId, String deviceType) {
        EasyMap3<Object> map = new EasyMap3<Object>();
        // 1. 获取需要处理的消息
        List<NotifyMessageDO> notifyMessageDOS = notifySendService.selectNeedNotify3(Long.parseLong(userId));
        if(EasyListUtil.isEmpty(notifyMessageDOS)) return map.put("msg", "没有通知消息").value();
        notifyMessageDOS.forEach(obj -> obj.setUpdateTime(LocalDateTime.now()));
        // 2. 执行通知
        MsgItem msgItem = new MsgItem(JSON.toJSONString(notifyMessageDOS), ReceiveOperateEnum.GLOBAL_NOTIFY_MSG.getOperateKey(), new Date().getTime()+"", deviceType);

        String resStr = JSON.toJSONString(msgItem);
        CommonResult<String> res = WebSocketUsers.baseSendMsg(userId, resStr, msgItem.getVersion(), msgItem.getDeviceType(), false);
        log.info("【每次心跳，需要通知 用户 未处理的消息-{}-{}】middle,通知结果:{}, 消息内容:{}", userId, deviceType, JSON.toJSONString(res), resStr);
        return map.put("msgItem", resStr).put("noticeRes", JSON.toJSONString(res)).value();
    }
}
