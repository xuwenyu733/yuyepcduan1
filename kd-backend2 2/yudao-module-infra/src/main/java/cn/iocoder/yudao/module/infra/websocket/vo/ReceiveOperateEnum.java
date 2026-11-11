package cn.iocoder.yudao.module.infra.websocket.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @创建人 lr
 * @创建时间 2024-05-13
 * @描述
 */
@Getter
@AllArgsConstructor
public enum ReceiveOperateEnum {

    PAGE_TO("页面跳转", "1", "PAGE_TO")
    ,ADD_DATA("弹出添加数据弹窗", "2", "ADD_DATA")
    ,TRANSFER_PARAMS("传输参数", "4", "TRANSFER_PARAMS")
    ,SURE_ADD("执行添加操作，数据入库", "5", "SURE_ADD")
    ,CANCEL_OPERATE("取消操作", "6", "CANCEL_OPERATE")

    ,CHAT_MSG("Agent给用户A发送的消息", "7", "CHAT_MSG")
    ,ASK_CHAT_LOG_LIST("获取最新在线人员及消息", "8", "ASK_CHAT_LOG_LIST")
    ,CHAT_MSG_FOR_USER("用户A给用户B发送的消息", "9", "CHAT_MSG_FOR_USER")
    ,CHAT_MSG_FOR_DELETE("用户收到该消息，后台可以删除该消息", "10", "CHAT_MSG_FOR_DELETE")
    ,CHAT_MSG_FOR_GROUP("用户A在群组发送的消息", "19", "CHAT_MSG_FOR_GROUP")
    ,GET_ILL_HANDLE("获取疾病解决办法", "100", "GET_ILL_HANDLE")
    ,APP_CHAT_THINK("app聊天思考过程", "150", "APP_CHAT_THINK")
    ,APP_CHAT_MSG("app聊天结果", "160", "APP_CHAT_MSG")
    ,RECEIVE_INSPECTION_REPORT("Java服务收到Agent发过来的巡检报告", "165", "RECEIVE_INSPECTION_REPORT")

    ,PC_MEET_CHAT_MSG("pc会议聊天结果", "260", "PC_MEET_CHAT_MSG")
    ,APP_MEET_CHAT_MSG("app会议聊天结果", "261", "APP_MEET_CHAT_MSG")

    ,GLOBAL_NOTIFY_MSG("通用消息通知", "270", "GLOBAL_NOTIFY_MSG")
    ,MAC_NOTIFY_MSG("mac消息通知", "280", "MAC_NOTIFY_MSG")

    ;

    private final String operateName;
    private final String operateType;
    private final String operateKey;
}
