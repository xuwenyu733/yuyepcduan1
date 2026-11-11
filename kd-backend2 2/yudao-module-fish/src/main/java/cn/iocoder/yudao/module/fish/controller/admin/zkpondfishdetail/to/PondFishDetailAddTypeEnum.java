package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @创建人 lr
 * @创建时间 2025-04-17
 * @描述
 */
@Getter
@AllArgsConstructor
public enum PondFishDetailAddTypeEnum {


    DEAD("-2", "死亡")
    ,SALE("-1", "销售")
    ,ADD("1", "投放")
    ,SPLIT("-3", "分池");

    /**
     * 状态值
     */
    private final String addType;
    /**
     * 状态名
     */
    private final String name;

}
