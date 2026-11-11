package cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo;

import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.AiAgentController;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @创建人 lr
 * @创建时间 2025-03-25
 * @描述
 */
@Data
@NoArgsConstructor
@Slf4j
public class ChatRespVo {

    // 这个是聊天记录 存储的key，不是userId
    private String uid;
    // 这个才是系统用户id，对应表 system_users
    private String userId;
    private String msg;
    private String data;
    private String chatTime;
    private String deviceType = "pc";


    public ChatRespVo(String uid, String msg) {
        this.uid = uid;
        this.msg = msg;
        this.build_msg();
    }

    public ChatRespVo build_msg() {
        if(StringUtil.isNotEmptyWith0(this.msg)) {
            this.msg = this.msg
                    .replaceAll("\n", "<br>")
                    .replaceAll("\\\\n", "<br>")
                    .replaceAll("\\n", "<br>");
        }
        return this;
    }

    public ChatRespVo enhancer() {
        if(StringUtil.isNotEmptyWith0(this.msg) && StringUtils.contains(this.msg, "</think>")) {
            this.setMsg(this.msg.split("</think>")[1].replaceFirst("<br><br>", ""));
        }
        String temp = this.msg;
        for(String key : AiAgentController.ChatReplace.keySet()) {
            String value = AiAgentController.ChatReplace.get(key);
            // log.warn("替换消息中间键,消息:{}，替换key:{},是否包含:{}，替换value：{}", temp, key, temp.contains(key), value);
            if(temp.contains(key)) {
                temp = temp.replaceAll(key, value);
                break;
            }
        }
        this.setMsg(temp);
        return this;
    }

    public static ChatRespVo build(String res) {
        if(StringUtil.isEmpty(res) || !res.contains("id")) {
            return new ChatRespVo("暂无消息", res);
        } else {
            ChatRespVo chatRespVo = JSON.parseObject(res, ChatRespVo.class);
            if(chatRespVo != null) chatRespVo.build_msg();
            return chatRespVo;
        }
    }

    public ChatRespVo buildChatVo(ChatVo chatVo) {
        this.setDeviceType(chatVo.getDeviceType());
        if(StringUtils.isEmpty(this.getUserId())) {
            this.setUserId(chatVo.getUserId());
        }
        if(StringUtils.isEmpty(this.getChatTime())) {
            this.setChatTime(EasyDateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        return this;
    }
}
