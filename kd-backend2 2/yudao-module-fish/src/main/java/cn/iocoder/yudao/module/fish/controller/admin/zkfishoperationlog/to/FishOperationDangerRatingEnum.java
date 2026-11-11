package cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.to;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 危险评级
 * @创建人 lr
 * @创建时间 2025-04-17
 * @描述
 */
@Getter
@AllArgsConstructor
public enum FishOperationDangerRatingEnum {


    NORMAL("1", "正常")
    ,WARN("2", "持续关注")
    ,ERROR("3", "立刻介入")
    ;

    /**
     * 状态值
     */
    private final String ratingType;
    /**
     * 状态名
     */
    private final String name;

}
