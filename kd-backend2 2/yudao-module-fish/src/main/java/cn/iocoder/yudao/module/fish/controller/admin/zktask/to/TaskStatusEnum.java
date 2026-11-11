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
public enum TaskStatusEnum {


    NEED_READ("待接收", "1", "")
    ,NEED_HANDLE("已接收待完成", "2", "")
    ,NEED_EVALUATE("已完成待评价", "3", "")
    ,EVALUATED("已评价", "4", "")
    ;

    private final String name;
    private final String value;
    private final String remark;

}
