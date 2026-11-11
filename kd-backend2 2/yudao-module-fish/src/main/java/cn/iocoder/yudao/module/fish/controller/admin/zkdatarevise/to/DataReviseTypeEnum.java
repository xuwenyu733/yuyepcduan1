package cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.to;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据修正操作类型   zk_data_revise_revise_type   ZK_DATA_REVISE_REVISE_TYPE
 * @创建人 lr
 * @创建时间 2025-04-17
 * @描述
 */
@Getter
@AllArgsConstructor
public enum DataReviseTypeEnum {


    PUSH("1", "投放")
    ,DEAD("2", "死亡")
    ,SPLIT("3", "分池")
    ,FEED("4", "投喂")
    ,POND("5", "存塘");

    /**
     * 状态值
     */
    private final String reviseType;
    /**
     * 状态名
     */
    private final String name;

}
