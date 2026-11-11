package cn.iocoder.yudao.module.fish.controller.admin.zktask.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @创建人 lr
 * @创建时间 2025-04-02
 * @描述
 */
@Getter
@AllArgsConstructor
public enum TaskTypeEnum {


    SHANG_BAN("上班", "1")
    ,XIA_BAN("下班", "2")
    ,XUN_CHA("巡查", "3")
    ;

    private final String name;
    private final String value;

}
