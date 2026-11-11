package cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @创建人 lr
 * @创建时间 2025-06-06
 * @描述
 */
@Data
@NoArgsConstructor
public class TextVo {
    private String q_id;
    private String text;

    public TextVo(String q_id, String text) {
        this.q_id = q_id;
        this.text = text;
    }
}
