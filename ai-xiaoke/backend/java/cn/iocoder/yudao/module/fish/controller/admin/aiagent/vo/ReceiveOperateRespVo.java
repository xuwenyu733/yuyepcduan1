package cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo;

import cn.iocoder.yudao.module.infra.websocket.vo.ReceiveOperateEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @创建人 lr
 * @创建时间 2025-03-27
 * @描述
 */
@Data
@NoArgsConstructor
public class ReceiveOperateRespVo {

    // 操作的目标页面路径
    private String targetPath;
    // 操作的目标页面名称
    private String targetPageName;
    // 操作类型  cn.iocoder.yudao.module.infra.websocket.vo.ReceiveOperateEnum
    private String operateKey;
    // 操作参数
    private String content;
    private String think;
    private String key;
    private String version;
    private String deviceType; // pc app
    private String appPath; // app 跳转路径

    public ReceiveOperateRespVo(String operateKey, String content) {
        this.operateKey = operateKey;
        this.content = content;
    }

    public ReceiveOperateRespVo(String targetPath, String operateKey, String content) {
        this.targetPath = targetPath;
        this.operateKey = operateKey;
        this.content = content;
    }

    public ReceiveOperateRespVo(ReceiveOperateVo vo) {
        this.targetPath = vo.getTargetPath();
        this.targetPageName = vo.getTargetPageName();
        this.operateKey = vo.getOperateKey();
        this.content = vo.getContent();
        this.version = vo.getVersion();
        this.deviceType = vo.getDeviceType();
        this.appPath = vo.getAppPath();
        if(StringUtils.isEmpty(this.version)) {
            this.version = String.format("%s", System.currentTimeMillis());
        }
    }
    public ReceiveOperateRespVo(ChatRespVo vo) {
        this.operateKey = ReceiveOperateEnum.CHAT_MSG.getOperateKey();
        this.content = vo.getMsg();
        this.version = String.format("%s", System.currentTimeMillis());
        this.deviceType = vo.getDeviceType();
        if(StringUtils.isEmpty(this.version)) {
            this.version = String.format("%s", System.currentTimeMillis());
        }
    }

    public ReceiveOperateRespVo(String operateKey, String content, String think, String version, String deviceType) {
        this.operateKey = operateKey;
        this.content = content;
        this.think = think;
        this.version = version;
        this.deviceType = deviceType;
    }

    public ReceiveOperateRespVo build_key(String key) {
        this.key = key;
        return this;
    }

}
