package cn.iocoder.yudao.module.fish.controller.admin.zktask.to;

import cn.iocoder.yudao.framework.common.base_api.BaseWebSocketHandle;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyMap3;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.ZkTaskController;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 如果当前 用户 有没有完成的任务，则需要进行通知
 */
@Component
@Slf4j
public class WebSocketHandle4FishTask implements BaseWebSocketHandle {

    @Resource
    private ZkTaskController zkTaskController;

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> heartCheckHandle(String userId, String deviceType) {
        String msg = zkTaskController.notifyNeedReadTask(EasyBigDecimalUtil.toLong(userId));

        return new EasyMap3<>().put("msg", msg).value();
    }

}
