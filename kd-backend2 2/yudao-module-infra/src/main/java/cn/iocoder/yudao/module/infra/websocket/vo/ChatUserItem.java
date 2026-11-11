package cn.iocoder.yudao.module.infra.websocket.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建人 lr
 * @创建时间 2025-06-13
 * @描述
 */
@Data
@NoArgsConstructor
public class ChatUserItem {

    private String oppositeUserId;
    private String oppositeUserName;
    private String oppositeUserAvatar; // 头像
    private String currUserId;
    private String currUserName;
    private String currUserAvatar;
    private String lastLog;
    private String lastLogTime;
    private String onLineStatus; // 0:不在线;1:pc在线;2:app在线;3:pc、app都在线;
    private String onLineInfo;
    private List<MsgItem> msgItemList = new ArrayList<>(); // 最新消息

    public ChatUserItem(String oppositeUserId, String onLineStatus) {
        this.oppositeUserId = oppositeUserId;
        this.onLineStatus = onLineStatus;
    }

    public ChatUserItem buildOnLineInfo() {
        String onLineStatus = this.getOnLineStatus();
        String onLineInfo = "不在线";
        switch (onLineStatus){
            case "0":
                onLineInfo = "不在线";
                break;
            case "1":
                onLineInfo = "pc在线";
                break;
            case "2":
                onLineInfo = "app在线";
                break;
            case "3":
                onLineInfo = "pc|app都在线";
                break;
        }
        this.setOnLineInfo(onLineInfo);
        return this;
    }

    public ChatUserItem build_lastMsg(MsgItem lastMsg) {
        this.setLastLog(lastMsg.getMsg());
        this.setLastLogTime(lastMsg.getChatTime());
        return this;
    }
}
