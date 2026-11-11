package cn.iocoder.yudao.module.fish.controller.admin.zktask.to.report;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TwoItem {

    private String key;
    private String value;

    public TwoItem(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
