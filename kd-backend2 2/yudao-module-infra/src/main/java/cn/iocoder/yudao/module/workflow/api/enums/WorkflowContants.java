package cn.iocoder.yudao.module.workflow.api.enums;

public class WorkflowContants {

    public final static String INITIATOR = "__initiator__";

    //队列通知事件名称
    public final static String EVENTNAME_PROCESS_START = "processStart";//流程开始
    public final static String EVENTNAME_PROCESS_END = "processEnd";//流程正常结束
    public final static String EVENTNAME_PROCESS_TERMINATED = "processTerminated";//流程被终止
    public final static String EVENTNAME_TASK_CREATE = "taskCreate";//任务生成

    public final static String APPROVE_STATUS_0 = "0";//暂存
    public final static String APPROVE_STATUS_1 = "1";//审批中
    public final static String APPROVE_STATUS_2 = "2";//审批结束
    public final static String APPROVE_STATUS_3 = "3";//终止
    /**
     * 任务处理结果
     */
    public enum ProcessResult {
        /**
         * 发起
         */
        START("0"),
        /**
         * 同意
         */
        AGREE("1"),
        /**
         * 退回
         */
        BACK("2"),
        /**
         * 撤回
         */
        RECALL("3"),
        /**
         * 终止
         */
        TERMINATE("5");

        private String value;

        ProcessResult(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 任务处理状态
     */
    public enum TaskState {
        /**
         * 审批中
         */
        APPROVE("0"),
        /**
         * 同意
         */
        AGREE("1"),
        /**
         * 退回
         */
        BACK("2"),
        /**
         * 撤回
         */
        RECALL("3");

        private String value;

        TaskState(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 任务处理情况
     */
    public enum ProcessRecordState {
        /**
         * 处理中
         */
        PROCESSING("0"),
        /**
         * 已处理
         */
        PROCESSED("1");
        private String value;

        ProcessRecordState(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 流程处理结果
     */
    public enum ProcessInstanceState {
        /**
         * 运行中
         */
        RUN("1"),
        /**
         * 已结束
         */
        END("2"),
        /**
         * 终止
         */
        TERMINATED("3");
        private String value;

        ProcessInstanceState(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
