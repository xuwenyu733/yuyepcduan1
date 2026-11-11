package cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @创建人 lr
 * @创建时间 2025-03-27
 * @描述
 */
@Data
@NoArgsConstructor
public class ReceiveOperateVo {

    // 这个是聊天记录 存储的key，不是userId
    private String uid;
    // 这个才是系统用户id，对应表 system_users
    private String userId;
    /**
     * 操作类型: 1:页面跳转;2:跳出添加弹窗;3:;4:;5:;6:;
     * ReceiveOperateEnum
     */
    private String operateKey;
    // 操作的目标页面路径
    private String targetPath;
    // 操作的目标页面名称
    private String targetPageName;
    // 操作参数
    private String content;
    private String think;
    private String version;
    private String deviceType; // pc app
    private String appPath; // app 进行跳转的路径
    private String key; // 一次交互对应唯一key，配合 zk_chat_log.role_name 就能够找到唯一交互任意消息
    private String otherParams; // 其他业务参数


}
