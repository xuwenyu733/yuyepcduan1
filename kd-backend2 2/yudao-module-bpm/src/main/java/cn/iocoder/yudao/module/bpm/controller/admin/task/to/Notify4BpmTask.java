package cn.iocoder.yudao.module.bpm.controller.admin.task.to;

import cn.iocoder.yudao.framework.common.base_api.BaseWebSocketHandle;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyMap3;
import cn.iocoder.yudao.module.bpm.controller.admin.task.BpmTaskController;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskRespVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 流程实例代码，方便有待审批执行 通知
 */
@Component
@Slf4j
public class Notify4BpmTask implements BaseWebSocketHandle {

    @Resource
    private BpmTaskController bpmTaskController;

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> heartCheckHandle(String userId, String deviceType) {
        String msg = bpmTaskController.notifyTodoList(EasyBigDecimalUtil.toLong(userId));

        return new EasyMap3<>().put("msg", msg).value();
    }
}
