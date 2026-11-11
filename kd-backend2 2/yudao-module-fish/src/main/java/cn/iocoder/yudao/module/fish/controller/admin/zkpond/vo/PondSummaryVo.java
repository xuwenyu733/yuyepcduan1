package cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @创建人 lr
 * @创建时间 2025-05-19
 * @描述
 */
@Data
@NoArgsConstructor
public class PondSummaryVo {

    private Long fishNum; // 鱼苗数
    private Long pondCount; // 存塘系统数
    private Long fishTypeCount; // 鱼苗种类

    public PondSummaryVo(Long fishNum, Long pondCount, Long fishTypeCount) {
        this.fishNum = fishNum;
        this.pondCount = pondCount;
        this.fishTypeCount = fishTypeCount;
    }
}
