package cn.iocoder.yudao.module.infra.websocket.vo;

import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.infra.websocket.WebSocketConfig;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @创建人 lr
 * @创建时间 2025-06-13
 * @描述
 */
@Data
@Builder
@AllArgsConstructor
public class MsgItem {

    private Long groupId;
    private Long chatMsgId;
    private Long chatMsgReceiveId;

    private String currUserId; // 当前用户id
    private String currUserName; // 当前用户id
    private String currUserAvatar; // 当前用户id
    private String oppositeUserId; // 对面用户id
    private String oppositeUserName; // 对面用户id
    private String oppositeUserAvatar; // 对面用户id
    private String chatTime;
    private String msg;
    private String fid;

    @JSONField(name = "isSelf")
    private boolean isSelf = false; // 是否是自己的消息
    /**
     * 消息类型   cn.kbsys.kbs.module.infra.websocket.vo.ReceiveOperateEnum
     */
    private String operateKey;
    private String deviceType = "app"; // pc app
    private String version;
    private String sendType = "0"; // 0:待发送;1:已发送;2:不存在的状态，因为此时该消息已经被销毁了;

    public MsgItem() {
    }

    public MsgItem(String currUserId, String oppositeUserId, String chatTime, String msg, String operateKey, String version, String sendType) {
        this.currUserId = currUserId;
        this.oppositeUserId = oppositeUserId;
        this.chatTime = chatTime;
        this.msg = msg;
        this.operateKey = operateKey;
        this.version = version;
        this.sendType = sendType;
    }

    public MsgItem(String oppositeUserId, String deviceType) {
        this.oppositeUserId = oppositeUserId;
        this.deviceType = deviceType;
    }

    /**
     * 用户A给用户B发送消息，目前虽然消息没有任何处理，但是后面可能有处理，开始就留一个口子，方便后续处理
     * @return 处理过后的消息对象
     */
    public MsgItem cacheMsg() {
        // String currUserId, String oppositeUserId, String chatTime, String msg, String operateKey, String version, String sendType
        MsgItem target = new MsgItem();
        CommonUtil.copyPropertiesIgnoreNull(this, target);
        if(StringUtils.isEmpty(target.getCurrUserAvatar())) {
            WebSocketConfig.baseUserService.byKey(target.getCurrUserId(), (map) -> {
                target.setCurrUserAvatar((String) map.get("avatar"));
                target.setCurrUserName((String) map.get("nickname"));
            });
        }
        if(StringUtils.isEmpty(target.getOppositeUserAvatar())) {
            WebSocketConfig.baseUserService.byKey(target.getOppositeUserId(), (map) -> {
                target.setOppositeUserAvatar((String) map.get("avatar"));
                target.setOppositeUserName((String) map.get("nickname"));
            });
        }
        return target;
    }

    public String build_sendInfo() {
        return String.format("%s_%s===》%s_%s", currUserId, currUserName, oppositeUserId, oppositeUserName);
    }

    public String build_version() {
        if(StringUtils.isEmpty(this.getVersion())) {
            this.setVersion(new Date().getTime()+"");
        }
        return this.getVersion();
    }

    public MsgItem(String msg, String operateKey, String version, String deviceType) {
        this.msg = msg;
        this.operateKey = operateKey;
        this.version = version;
        this.deviceType = deviceType;
    }


    public MsgItem(Long currUserId, Long oppositeUserId, String msg, String operateKey, String version, String chatTime) {
        this.currUserId = currUserId+"";
        this.oppositeUserId = oppositeUserId+"";
        this.msg = msg;
        this.operateKey = operateKey;
        this.version = version;
        this.chatTime = chatTime;
    }

    public MsgItem build_deviceType(String deviceType) {
        MsgItem msgItem = new MsgItem();
        CommonUtil.copyPropertiesIgnoreNull(this, msgItem);
        msgItem.setDeviceType(deviceType);
        return msgItem;
    }

}
