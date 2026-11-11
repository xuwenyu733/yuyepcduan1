package cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.to;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统计类型
 * @创建人 lr
 * @创建时间 2025-04-17
 * @描述
 */
@Getter
@AllArgsConstructor
public enum FishOperationLogTypeEnum {


    BATCH("-2.1", "批次")
    ,WORKSHOP("-2.2", "车间")
    ,SYSTEM("-2.3", "系统")
    ,POND("-2.4", "存塘")
    ;

    /**
     * 状态值
     */
    private final String logType;
    /**
     * 状态名
     */
    private final String name;

}
