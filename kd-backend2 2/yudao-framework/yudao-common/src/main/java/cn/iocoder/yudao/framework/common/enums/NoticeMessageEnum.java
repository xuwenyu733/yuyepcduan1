package cn.iocoder.yudao.framework.common.enums;

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
public enum NoticeMessageEnum {


    APP_TODO_PAGE("/pages/index_son/mine_task/index", "app待审批")
    ,APP_TODO_TASK("/pages/workbenches/need_handle_task/need_handle_task?userId=", "任务待办")
    ;

    /**
     * 状态值
     */
    private final String pageUrl;
    /**
     * 状态名
     */
    private final String name;

}
