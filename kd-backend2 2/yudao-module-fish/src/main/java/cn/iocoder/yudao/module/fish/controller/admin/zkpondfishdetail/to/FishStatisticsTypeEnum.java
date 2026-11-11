package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to;

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
public enum FishStatisticsTypeEnum {


    NOT_STATISTICS("0", "未统计")
    ,HAS_STATISTICS("1", "已统计")
    ;

    /**
     * 状态值
     */
    private final String statisticsType;
    /**
     * 状态名
     */
    private final String name;

}
