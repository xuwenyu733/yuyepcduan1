package cn.iocoder.yudao.module.fish.service.zkbiologicalasset.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存塘出池天数
 */
@Data
@NoArgsConstructor
public class OutPoolVo {

    private Long pondId; // 存塘id
    private Long releaseDays; // 已投放天数
    private Long remainingDays; // 剩余出池天数

}
