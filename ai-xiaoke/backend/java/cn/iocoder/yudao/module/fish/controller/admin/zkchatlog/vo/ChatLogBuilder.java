package cn.iocoder.yudao.module.fish.controller.admin.zkchatlog.vo;

import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ChatRespVo;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ChatVo;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ReceiveOperateVo;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatlog.ChatLogDO;

import java.util.Arrays;
import java.util.List;

/**
 * @创建人 lr
 * @创建时间 2025-03-28
 * @描述
 */
public class ChatLogBuilder {

    public static String AI_NAME = "ai";
    public static String USER_NAME = "用户";

    public static List<ChatLogDO> build(ChatVo chatVo, ChatRespVo chatRespVo) {
        // String roleName, String chatContent, Long userId, String userName, Long deptId
        long userId = Long.parseLong(chatVo.getUserId());
        ChatLogDO userDo = new ChatLogDO(USER_NAME, chatVo.getQuery(), userId, "", null).build_fid(chatVo.getKey());
        ChatLogDO aiDo = new ChatLogDO(AI_NAME, chatRespVo.getMsg(), userId, "", null).build_fid(chatVo.getKey());
        return Arrays.asList(userDo, aiDo);
    }

    public static List<ChatLogDO> build(ChatVo chatVo) {
        // String roleName, String chatContent, Long userId, String userName, Long deptId
        long userId = Long.parseLong(chatVo.getUserId());
        ChatLogDO userDo = new ChatLogDO(USER_NAME, chatVo.getQuery(), userId, "", null).build_fid(chatVo.getKey());
        return Arrays.asList(userDo);
    }

    public static List<ChatLogDO> build(ReceiveOperateVo aiVo) {
        // String roleName, String chatContent, Long userId, String userName, Long deptId
        long userId = Long.parseLong(aiVo.getUserId());
        String msg = new StringBuilder("<think>")
                .append(aiVo.getThink())
                .append("</think>")
                .append(aiVo.getContent()).toString();
        ChatLogDO aiDo = new ChatLogDO(AI_NAME, msg, userId, "", null).build_fid(aiVo.getKey());
        return Arrays.asList(aiDo);
    }

}
