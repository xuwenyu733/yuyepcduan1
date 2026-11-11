package cn.iocoder.yudao.module.fish.controller.admin.aiagent.to;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.infra.websocket.WebSocketUsers;
import cn.iocoder.yudao.module.infra.websocket.vo.ReceiveOperateEnum;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ReceiveOperateRespVo;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ReceiveOperateVo;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatlog.vo.ChatLogBuilder;
import cn.iocoder.yudao.module.fish.service.zkchatlog.ChatLogService;
import cn.iocoder.yudao.module.fish.service.zkinspectionreport.ZkInspectionReportService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * @创建人 lr
 * @创建时间 2025-08-18
 * @描述
 */
@Component
@Slf4j
public class AgentMsgHandler {

    @Autowired
    private ChatLogService chatLogService;
    @Resource
    private ZkInspectionReportService zkInspectionReportService;
    /**
     * 后台接受agent的消息
     * @param vo
     * @return
     */
    public CommonResult<String> receiveOperateHandler(ReceiveOperateVo vo) {
        CommonResult<String> res = CommonResult.success("操作成功！");
        if(
                StringUtils.equals(ReceiveOperateEnum.APP_CHAT_THINK.getOperateKey(), vo.getOperateKey())
        ) {
            String sendRes = common_handler(vo);
            log.warn("【后台接受ai智能体操作-{}】middle,操作类型为 通知app思考过程,发送结果：{},思考内容:{}", TokenUtil.userInfo(), sendRes, JSON.toJSONString(vo));
        } else if(StringUtils.equals(ReceiveOperateEnum.APP_CHAT_MSG.getOperateKey(), vo.getOperateKey())) {
            String sendRes = common_handler(vo);
            log.warn("【后台接受ai智能体操作-{}】middle,操作类型为 通知app聊天结果,发送结果：{},回答内容:{}", TokenUtil.userInfo(), sendRes, JSON.toJSONString(vo));
            // 2. 异步存储聊天内容
            chatLogService.customSaveList(ChatLogBuilder.build(vo));
        } else if(StringUtils.equals(ReceiveOperateEnum.RECEIVE_INSPECTION_REPORT.getOperateKey(), vo.getOperateKey())) {
            Integer count = zkInspectionReportService.receiveTaskDetailReport(vo);
            log.warn("【后台接受ai智能体操作-{}】middle,操作类型为 Java服务收到Agent发过来的巡检报告,最终添加:{},报告内容:{}", TokenUtil.userInfo(), count, JSON.toJSONString(vo));
        }
        return res;
    }

    public String common_handler(ReceiveOperateVo vo) {
        // String operateKey, String content, String version, String deviceType
        ReceiveOperateRespVo socketRes = new ReceiveOperateRespVo(vo.getOperateKey(), vo.getContent(), vo.getThink(), vo.getVersion(), vo.getDeviceType());
        socketRes.build_key(vo.getKey());
        String resStr = JSON.toJSONString(socketRes);
        CommonResult<String> res = WebSocketUsers.baseSendMsg(vo.getUserId(), resStr, vo.getVersion(), vo.getDeviceType(), false);
        return res.getMsg();
    }

}
