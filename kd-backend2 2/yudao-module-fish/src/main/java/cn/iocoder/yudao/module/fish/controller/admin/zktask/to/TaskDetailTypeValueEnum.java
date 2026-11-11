package cn.iocoder.yudao.module.fish.controller.admin.zktask.to;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @创建人 lr
 * @创建时间 2025-04-02
 * @描述
 */
@Getter
@AllArgsConstructor
public enum TaskDetailTypeValueEnum {


    NORMAL("普通", "198", "199以下")
    ,NOTICE("提示", "199", "提示")
    ,FORM("表单", "299", "表单填写，eg: 打样")
    ;

    private final String name;
    private final String value;
    private final String remark;

}
