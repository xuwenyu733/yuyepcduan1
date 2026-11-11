package cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @创建人 lr
 * @创建时间 2025-09-01
 * @描述
 */
@Data
@NoArgsConstructor
public class MeetMsgItem {

    private Long userId;
    private String userName;
    private String chatMsg;
    private String version;
    private String deviceType = "pc"; // pc app

    public MeetMsgItem(Long userId, String userName, String chatMsg) {
        this.userId = userId;
        this.userName = userName;
        this.chatMsg = chatMsg;
    }

    public MeetMsgItem(Long userId, String userName, String chatMsg, String version, String deviceType) {
        this.userId = userId;
        this.userName = userName;
        this.chatMsg = chatMsg;
        this.version = version;
        this.deviceType = deviceType;
    }

    public MeetMsgItem build_firstMsg(MeetMsgItem lastItem) {
        String newMsg = null;
        if(lastItem == null) {
            newMsg = String.format("%s说: %s", this.getUserName(), this.getChatMsg());
        } else {
            if(!Objects.equals(lastItem.getUserId(), this.getUserId())) {
                newMsg = String.format("%s说: %s", this.getUserName(), this.getChatMsg());
            } else if(
                    StringUtil.longStrReduceGT(lastItem.getVersion(), this.getVersion(), 3*60*1000L) //
            ) {
                // 超过3分钟 需要重新署名
                newMsg = String.format("%s说: %s", this.getUserName(), this.getChatMsg());
            }
        }
        if(StringUtils.isNotBlank(newMsg)) this.setChatMsg(newMsg);
        return this;
    }

    public static String selectStrContent(List<MeetMsgItem> list) {
        if(EasyListUtil.isEmpty(list)) return "";
        String userNameStr = list.stream().map(obj -> obj.getUserName()).collect(Collectors.toSet()).stream().collect(Collectors.joining("、"));
        String strContent = list.stream().map(obj -> obj.getChatMsg()).collect(Collectors.joining("\n"));
        String res = String.format("参会人员： %s\n" +
                "会议记录： %s", userNameStr, strContent);
        return res;
    }
}
