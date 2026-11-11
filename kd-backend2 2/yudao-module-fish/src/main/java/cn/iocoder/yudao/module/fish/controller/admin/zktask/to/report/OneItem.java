package cn.iocoder.yudao.module.fish.controller.admin.zktask.to.report;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OneItem {

    private String name;
    private List<TwoItem> listTwo;

    public OneItem(String name, List<TwoItem> listTwo) {
        this.name = name;
        this.listTwo = listTwo;
    }
}
