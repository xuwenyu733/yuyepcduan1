package cn.iocoder.yudao.module.infra.websocket.vo;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @创建人 lr
 * @创建时间 2025-06-13
 * @描述
 */
@Data
@NoArgsConstructor
public class ChatUserList {
    private String userId;
    // 用户列表
    private List<ChatUserItem> userItemList;
    private String version;

    public ChatUserList(String userId, List<ChatUserItem> userItemList) {
        this.userId = userId;
        this.userItemList = userItemList;
    }

    public ChatUserList(String userId, List<ChatUserItem> userItemList, String version) {
        this.userId = userId;
        this.userItemList = userItemList;
        this.version = version;
    }

    public ChatUserList build_userItemList() {
        if(EasyListUtil.isEmpty(userItemList)) return this;
        for(ChatUserItem item : userItemList) {
            List<MsgItem> msgItemList = item.getMsgItemList();
            if(EasyListUtil.isEmpty(msgItemList)) continue;
            MsgItem msgItem = msgItemList.get(msgItemList.size() - 1);
            item.setLastLogTime(msgItem.getChatTime());
            item.setLastLog(msgItem.getMsg());
        }
        return this;
    }
}
