package cn.iocoder.yudao.module.fish.service.aiagent;

import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.AgentToolModel;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ChatRespVo;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ChatVo;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.PyChatReqVo;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.PyChatRespVo;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.TextVo;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

/**
 * @创建人 lr
 * @创建时间 2025-03-25
 * @描述
 */
public interface AiAgentService {
    ChatRespVo chat(ChatVo chatVo);
    PyChatRespVo chatPy(PyChatReqVo req);
    PyChatRespVo chatPy(PyChatReqVo req, Map<String, String> forwardHeaders);
    SseEmitter chatPyStream(PyChatReqVo req);
    SseEmitter chatPyStream(PyChatReqVo req, Map<String, String> forwardHeaders);
    Map<String, Object> dataEntrySubmit(Map<String, Object> req, Map<String, String> forwardHeaders);
    ChatRespVo add_text(TextVo vo);
    ChatRespVo delete_text(TextVo vo);

    AgentToolModel getByIds(List<Long> modelIds);
}
