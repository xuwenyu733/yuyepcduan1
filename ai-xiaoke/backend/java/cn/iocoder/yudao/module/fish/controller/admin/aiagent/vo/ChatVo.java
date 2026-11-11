package cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo;

import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @创建人 lr
 * @创建时间 2025-03-25
 * @描述
 */
@Data
@NoArgsConstructor
public class ChatVo {

    // 这个是聊天记录 存储的key，不是userId
    private String uid;
    // 这个才是系统用户id，对应表 system_users
    private String userId;
    private String query;
    private String token;
    private String deviceType = "pc";
    // 是否深度思考 deepseek-r1 、 deepseek-v3
    private String modelName = "deepseek-r1";
    // 开启知识库 ill other
    private String agentType = "ill";
    // 开启联网 true 、 false
    private String searchType = "false";
    // self.version = str(now.timestamp()).replace(".", "")
    private String version;
    // self.key = f"{self.userId}AAA{self.version}"
    private String key;
    private String otherParams; // 其他业务参数

    public ChatVo formatQuery() {
        if(StringUtil.isNotEmptyWith0(query)) {
            this.query = this.query.replaceAll("<br>", "\\n");
        }
        return this;
    }
    public ChatVo build_otherParams(String otherParams) {
        this.otherParams = otherParams;
        return this;
    }

    public ChatVo(String uid, String userId, String query, String token, String deviceType,
                  String modelName, String agentType, String searchType, String version, String key) {
        this.uid = uid;
        this.userId = userId;
        this.query = query;
        this.token = token;
        this.deviceType = deviceType;
        this.modelName = modelName;
        this.agentType = agentType;
        this.searchType = searchType;
        this.version = version;
        this.key = key;
    }

    public static String selectUid(String username, String nickname, Long userId) {
        String uid = String.format("AiAgent:ChatLog:%s_%s_%s", username, nickname, userId);
        return uid;
    }

}
