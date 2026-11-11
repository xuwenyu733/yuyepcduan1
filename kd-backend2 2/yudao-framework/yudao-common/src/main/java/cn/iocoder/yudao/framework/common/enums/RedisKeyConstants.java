package cn.iocoder.yudao.framework.common.enums;

/**
 * 通用redis key
 */
public interface RedisKeyConstants {

    // 需要通知的 notify key , 存储是需要通知的消息列表 List<NotifyMessageDO>
    public static final String NEED_NOTIFY_KEY = "NOTIFY:%s:NEED_NOTIFY";
    // 已经通知的 notify id ， 存储是已经通知消息id列表 List<Long>
    public static final String HAD_NOTIFY_KEY = "NOTIFY:HAD_NOTIFY";
    // 消息保存多久
    public static final Long NOTIFY_EXPIRE = 60*24*30L;

    // 工作流 审批单 待审批的 已经通过的
    public static final String HAD_NOTIFY_TODO_TASK_KEY = "NOTIFY:HAD_NOTIFY_TODO_TASK";
    // fish模块任务未读的已经通知过的
    public static final String HAD_NOTIFY_FISH_TASK_KEY = "NOTIFY:HAD_NOTIFY_FISH_TASK";


}
