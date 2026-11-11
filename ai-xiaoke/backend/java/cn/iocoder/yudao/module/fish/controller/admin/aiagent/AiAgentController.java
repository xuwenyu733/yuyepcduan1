package cn.iocoder.yudao.module.fish.controller.admin.aiagent;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.infra.websocket.WebSocketUsers;
import cn.iocoder.yudao.module.infra.websocket.vo.ReceiveOperateEnum;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.constants.AgentConstant;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.to.AgentMsgHandler;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatlog.vo.ChatLogBuilder;
import cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.ZkKonwledgeTreeController;
import cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.to.KnowLedgeSummaryVo;
import cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.to.TreeGetInfoByStr;
import cn.iocoder.yudao.module.fish.service.aiagent.AiAgentService;
import cn.iocoder.yudao.module.fish.service.zkchatlog.ChatLogService;
import cn.iocoder.yudao.module.fish.service.zkkonwledgetree.ZkKonwledgeTreeService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * @创建人 lr
 * @创建时间 2025-03-25
 * @描述
 */
@Tag(name = "智能体agent")
@RestController
@RequestMapping("/fish/zk-ai-agent")
@Validated
@Slf4j
public class AiAgentController {

    @Autowired
    private AiAgentService aiAgentService;

    @Autowired
    private ChatLogService chatLogService;

    @Autowired
    private ThreadPoolExecutor executor;
    @Resource
    private TreeGetInfoByStr treeGetInfoByStr;
    @Resource
    private ZkKonwledgeTreeController zkKonwledgeTreeController;
    @Resource
    private AgentMsgHandler agentMsgHandler;

    @Autowired
    private AdminUserApi adminUserApi;

    // 聊天替换符
    public static Map<String, String> ChatReplace = new HashMap<>();
    static {
        ChatReplace.put("f336d398-50d1-296b-26c1-d8f024e1f78f", "执行物料入库第二步");
        ChatReplace.put("f136d398-50d1-296b-26c1-d8f024e1f78f", "执行培训知识入库第二步");
        ChatReplace.put("f136d398-50d1-296b-26c1-d8f024e1f99f", "执行数据入库第二步");
    }

    @PostMapping("/chat")
    @Operation(summary = "聊天接口")
    public CommonResult<ChatRespVo> chat(@Valid @RequestBody ChatVo chatVo) {
        // 1. 开始聊天
        ChatRespVo chatRespVo = aiAgentService.chat(chatVo);
        // if(chatRespVo != null) chatRespVo.enhancer();
        // 2. 异步存储聊天内容
        CompletableFuture.runAsync(() -> chatLogService.customSaveList(ChatLogBuilder.build(chatVo)), executor);
        return success(chatRespVo);
    }

    @PostMapping("/chatPy")
    @Operation(summary = "聊天接口（对齐 Python /api/chat）")
    public CommonResult<PyChatRespVo> chatPy(@Valid @RequestBody PyChatReqVo req, HttpServletRequest request) {
        // 1. 调用 Python 接口
        PyChatRespVo resp = aiAgentService.chatPy(req, ForwardHeaderUtil.extractForwardHeaders(request));
        // 2. 异步存储聊天内容（仅存用户问题，结构复用 ChatVo + ChatLogBuilder）
        CompletableFuture.runAsync(() -> {
            ChatVo chatVo = new ChatVo();
            chatVo.setUserId(req.getUserId());
            chatVo.setQuery(req.getMessage());
            // 这里沿用 conversation_id 作为聊天 key，便于前端按会话聚合
            chatVo.setKey(req.getConversationId());
            chatLogService.customSaveList(ChatLogBuilder.build(chatVo));
        }, executor);
        return success(resp);
    }

    @PostMapping(value = "/chatPyStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "聊天接口（流式，Java 中转到 Python SSE）")
    public SseEmitter chatPyStream(@Valid @RequestBody PyChatReqVo req, HttpServletResponse response, HttpServletRequest request) {
        // 避免代理/浏览器缓冲导致“攒一波再显示”
        try {
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("X-Accel-Buffering", "no");
            response.setHeader("Connection", "keep-alive");
        } catch (Exception ignore) {
        }
        // 异步存储聊天内容（仅存用户问题）
        CompletableFuture.runAsync(() -> {
            ChatVo chatVo = new ChatVo();
            chatVo.setUserId(req.getUserId());
            chatVo.setQuery(req.getMessage());
            chatVo.setKey(req.getConversationId());
            chatLogService.customSaveList(ChatLogBuilder.build(chatVo));
        }, executor);
        return aiAgentService.chatPyStream(req, ForwardHeaderUtil.extractForwardHeaders(request));
    }

    @PostMapping("/dataEntrySubmit")
    @Operation(summary = "数据录入提交（Java 中转到 Python /api/data-entry/submit）")
    public CommonResult<Map<String, Object>> dataEntrySubmit(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        Map<String, Object> resp = aiAgentService.dataEntrySubmit(req, ForwardHeaderUtil.extractForwardHeaders(request));
        return success(resp);
    }

    @PostMapping("/asyncChat")
    @Operation(summary = "异步聊天接口")
    public CommonResult<ChatRespVo> asyncChat(@Valid @RequestBody ChatVo chatVo) {
        CompletableFuture.runAsync(() -> {
            // 1. 开始聊天
            ChatRespVo chatRespVo = aiAgentService.chat(chatVo);
            if(chatRespVo != null) {
                // chatRespVo.enhancer();
                // ReceiveOperateRespVo res = new ReceiveOperateRespVo(chatRespVo);
                // String resStr = JSON.toJSONString(res);
                // WebSocketUsers.baseSendMsg(chatVo.getUserId(), resStr, res.getVersion(), res.getDeviceType(), false);
                // 2. 异步存储聊天内容
                chatLogService.customSaveList(ChatLogBuilder.build(chatVo));
            }
        }, executor);
        return success(new ChatRespVo());
    }


    @PostMapping("/receiveOperate")
    @Operation(summary = "后台接受ai智能体操作")
    public CommonResult<String> receiveOperate(@Valid @RequestBody ReceiveOperateVo vo) {
        String userInfo = TokenUtil.userInfo();
        log.warn("【后台接受ai智能体操作-{}】middle1,agent通过websocket通知前台参数:{}", userInfo, JSON.toJSONString(vo));
        if(StringUtils.isEmpty(vo.getUserId()) || vo.getUserId().length()<=2) vo.setUserId(SecurityFrameworkUtils.getLoginUserId()+"");
        CommonResult<String> res = receiveOperateHandler(vo);
        log.warn("【后台接受ai智能体操作-{}】middle2,前台websocket返回的操作执行结果:{}", userInfo, JSON.toJSONString(vo));
        return res;
    }

    /**
     * 根据智能体操作类型，执行操作
     * @param vo
     */
    private CommonResult<String> receiveOperateHandler(ReceiveOperateVo vo) {
        CommonResult<String> res = CommonResult.success("操作成功！");
        if(StringUtils.equals(ReceiveOperateEnum.PAGE_TO.getOperateKey(), vo.getOperateKey())) {
            log.warn("【后台接受ai智能体操作-{}】middle,操作类型为跳转页面:{}", TokenUtil.userInfo(), JSON.toJSONString(vo));
            if(StringUtils.isNotBlank(vo.getUserId()) && StringUtils.isNotBlank(vo.getContent())) {
                res = exePageTo(vo);
            }
        } else if(StringUtils.equals(ReceiveOperateEnum.ADD_DATA.getOperateKey(), vo.getOperateKey())) {
            log.warn("【后台接受ai智能体操作-{}】middle,操作类型为弹出添加数据弹窗:{}", TokenUtil.userInfo(), JSON.toJSONString(vo));
            if(StringUtils.isNotBlank(vo.getUserId()) && StringUtils.isNotBlank(vo.getContent())) {
                res = exeAddData(vo);
            }
        } else if(StringUtils.equals(ReceiveOperateEnum.SURE_ADD.getOperateKey(), vo.getOperateKey())) {
            log.warn("【后台接受ai智能体操作-{}】middle,操作类型为数据入库:{}", TokenUtil.userInfo(), JSON.toJSONString(vo));
            if(StringUtils.isNotBlank(vo.getUserId()) && StringUtils.isNotBlank(vo.getContent())) {
                res = exeSureAdd(vo);
            }
        } else if(StringUtils.equals(ReceiveOperateEnum.GET_ILL_HANDLE.getOperateKey(), vo.getOperateKey())) {
            log.warn("【后台接受ai智能体操作-{}】middle,获取疾病解决办法:{}", TokenUtil.userInfo(), JSON.toJSONString(vo));
            if(StringUtils.isNotBlank(vo.getUserId()) && StringUtils.isNotBlank(vo.getContent())) {
                String temp = treeGetInfoByStr.exe_find(vo.getContent());
                res = CommonResult.success(temp);
            }
        } else {
            res = agentMsgHandler.receiveOperateHandler(vo);
        }
        return res;
    }

    /**
     * 通知前台跳转页面
     * @param vo
     */
    public CommonResult<String> exePageTo(ReceiveOperateVo vo) {
        ReceiveOperateRespVo res = new ReceiveOperateRespVo(vo);
        String resStr = JSON.toJSONString(res);
        log.warn("【后台接受ai智能体操作-{}】middle,操作类型为跳转页面，当前正在通知前台:{}", TokenUtil.userInfo(), resStr);
        CommonResult<String> clientRes = WebSocketUsers.baseSendMsg(vo.getUserId(), resStr, vo.getVersion(), vo.getDeviceType(), true);
        return clientRes;
    }
    public CommonResult<String> exeAddData(ReceiveOperateVo vo) {
        ReceiveOperateRespVo res = new ReceiveOperateRespVo(vo);
        String resStr = JSON.toJSONString(res);
        log.warn("【后台接受ai智能体操作-{}】middle,操作类型为添加数据，当前正在通知前台:{}", TokenUtil.userInfo(), resStr);
        CommonResult<String> clientRes = WebSocketUsers.baseSendMsg(vo.getUserId(), resStr, vo.getVersion(), vo.getDeviceType(), true);
        return clientRes;
    }
    public CommonResult<String> exeSureAdd(ReceiveOperateVo vo) {
        ReceiveOperateRespVo res = new ReceiveOperateRespVo(vo);
        String resStr = JSON.toJSONString(res);
        log.warn("【后台接受ai智能体操作-{}】middle,操作类型为数据入库，当前正在通知前台:{}", TokenUtil.userInfo(), resStr);
        CommonResult<String> clientRes = WebSocketUsers.baseSendMsg(vo.getUserId(), resStr, vo.getVersion(), vo.getDeviceType(), true);
        return clientRes;
    }

    @PostMapping("/testWs")
    @Operation(summary = "测试WebSocket")
    public CommonResult<String> testWs(@Valid @RequestBody ReceiveOperateVo vo) {
        log.warn("【测试WebSocket-{}】middle,参数:{}", TokenUtil.userInfo(), JSON.toJSONString(vo));
        List<String> list = Arrays.asList("好的，关于短时间内水温下降超过3度的问题，但气压和溶氧正常的情况，我们需要进一步排查。请问：",
                "1. **鱼群是否有异常行为**？比如浮头、游动迟缓或集群异常？",
                "2. **水温下降的持续时间**？是短暂几小时还是持续一整天？",
                "3. **是否有鱼体表或鳃部症状**？比如黏液增多、充血或鳃丝肿胀？",
                "请先提供这些信息，我们可以更精准地分析影响并给出解决方案。");
        String version = new Date().getTime()+"";
        // for(int i=0;i<list.size();i++) {
        //     ReceiveOperateRespVo res = new ReceiveOperateRespVo(ReceiveOperateEnum.APP_CHAT_THINK.getOperateKey(), list.get(i),
        //             "", version, vo.getDeviceType()).build_key(version);
        //     String resStr = JSON.toJSONString(res);
        //     CommonResult<String> sendRes = WebSocketUsers.baseSendMsg(vo.getUserId(), resStr, version, vo.getDeviceType(), false);
        //     log.warn("【测试WebSocket-{}】middle,发送给前台的结果是:{}", TokenUtil.userInfo(), JSON.toJSONString(sendRes));
        // }
        AdminUserRespDTO user = adminUserApi.getUser(Long.valueOf(vo.getUserId()));
        ReceiveOperateRespVo res = new ReceiveOperateRespVo(ReceiveOperateEnum.MAC_NOTIFY_MSG.getOperateKey(), String.format("尊敬的%s您好！有人在科德App中呼叫您！", user.getNickname()),
                "", version, vo.getDeviceType()).build_key(version);
         String resStr = JSON.toJSONString(res);
         log.warn("【测试WebSocket-{}】middle,发送给前台的参数是:{}", TokenUtil.userInfo(), resStr);
        CommonResult<String> stringCommonResult = WebSocketUsers.baseSendMsg(vo.getUserId(), resStr, version, vo.getDeviceType(), false);
        return stringCommonResult;
        // agentMsgHandler.common_handler(vo);
        // Integer resCount = 10;
        // if(resCount > 0)
        //     return success("已成功发送消息！");
        // else if(Objects.equals(-1, resCount))
        //     return CommonResult.error(403, "当前用户没有websocket连接。");
        // else
        //     return CommonResult.error(403, "websocket其他错误。");
    }
    @PostMapping("/testWs_sync")
    @Operation(summary = "测试WebSocket同步消息")
    public CommonResult<String> testWs_sync(@Valid @RequestBody ReceiveOperateVo vo) {
        log.warn("【测试WebSocket-{}】middle,参数:{}", TokenUtil.userInfo(), JSON.toJSONString(vo));
        ReceiveOperateRespVo res = new ReceiveOperateRespVo(vo);
        String resStr = JSON.toJSONString(res);
        log.warn("【测试WebSocket-{}】middle,发送给前台的参数是:{}", TokenUtil.userInfo(), resStr);
        CommonResult<String> clientRes = WebSocketUsers.commonSendMsg(vo.getUserId(), resStr, vo.getVersion(), vo.getDeviceType());
        log.warn("【测试WebSocket-{}】middle,接受到前台的参数是:{}", TokenUtil.userInfo(), clientRes);
        if(clientRes.isSuccess())
            return clientRes;
        else if(Objects.equals(-1, clientRes.getCode()))
            return CommonResult.error(403, "当前用户没有websocket连接。");
        else
            return CommonResult.error(403, "websocket其他错误。");
    }

    @GetMapping("/getModelInfo")
    @Operation(summary = "汇总Agent调用工具modelName-接口")
    @PermitAll
    public CommonResult<AgentToolModel> getModelInfo() {
        // 1. 先集成部分 model
        List<Long> modelIds = AgentConstant.MENU_IDS;
        // 2. 获取列表
        AgentToolModel agentToolModel = aiAgentService.getByIds(modelIds);
        // 3. 获取 疾病智能体描述 信息
        CommonResult<KnowLedgeSummaryVo> knowledgeSummary = zkKonwledgeTreeController.getKnowledgeInfo();
        log.warn("【汇总Agent调用工具modelName-{}】middle,agent能够操作的模块名为： {}", JSON.toJSONString(agentToolModel));
        return CommonResult.success(agentToolModel).put("knowledgeSummary", knowledgeSummary.getData());
    }
}
