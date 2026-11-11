package cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot.warn;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @创建人 lr
 * @创建时间 2025-09-10
 * @描述
 */
@Data
@NoArgsConstructor
public class WarnMessage4IOT {

    private Long timestamp; // 时间戳
    private String messageId; // 消息id
    private MessageDetail4IOT data; // 消息明细

}
