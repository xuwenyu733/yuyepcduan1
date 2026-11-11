package cn.iocoder.yudao.module.fish.controller.admin.zkscheduling.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @创建人 lr
 * @创建时间 2025-04-02
 * @描述
 */
@Getter
@AllArgsConstructor
public enum SchedulingTypeEnum {


    DA_KA("打卡", "1", "1,2")
    ,XUN_CHA("巡查", "3", "3")
    ,QI_TA("其他", "4", "4")
    ;

    private final String name;
    private final String value;
    private final String details;

}
