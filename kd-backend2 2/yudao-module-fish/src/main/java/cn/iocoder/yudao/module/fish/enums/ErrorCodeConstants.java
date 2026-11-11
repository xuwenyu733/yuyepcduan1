package cn.iocoder.yudao.module.fish.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;

/**
 * Member 错误码枚举类
 * <p>
 * member 系统，使用 1-004-000-000 段
 */
public interface ErrorCodeConstants {
    // ========== zk jidi 模块==========   start
    ErrorCode ZK_ISSUE_NOT_EXISTS = new ErrorCode(2025032003, "问题总结不存在");
    ErrorCode ZK_ILL_CONTROL_NOT_EXISTS = new ErrorCode(2025032004, "疾病诊断与防治记录不存在");
    ErrorCode ZK_USE_MEDICINE_NOT_EXISTS = new ErrorCode(2025032006, "用药记录不存在");
    ErrorCode ZK_WORKSHOP_NOT_EXISTS = new ErrorCode(2025032007, "车间不存在");
    ErrorCode ZK_POND_NOT_EXISTS = new ErrorCode(2025032008, "存塘不存在");
    ErrorCode ZK_DEVICE_NOT_EXISTS = new ErrorCode(2025032009, "设备不存在");
    ErrorCode ZK_POND_FISH_NOT_EXISTS = new ErrorCode(2025032010, "存塘鱼苗不存在");
    ErrorCode ZK_POND_FISH_DETAIL_NOT_EXISTS = new ErrorCode(2025032011, "存塘鱼苗明细不存在");
    ErrorCode ZK_POND_WARN_NOT_EXISTS = new ErrorCode(2025032012, "告警不存在");
    ErrorCode ZK_MANUAL_INSPECTION_NOT_EXISTS = new ErrorCode(2025032013, "人工巡检不存在");
    ErrorCode TRAINING_KNOWLEDGE_NOT_EXISTS = new ErrorCode(2025032014, "培训知识不存在");
    ErrorCode ZK_PAPER_NOT_EXISTS = new ErrorCode(2025032015, "试卷不存在");
    ErrorCode ZK_PAPER_ANSWER_NOT_EXISTS = new ErrorCode(2025032016, "答题记录不存在");
    ErrorCode ZK_PAPER_QUESTION_NOT_EXISTS = new ErrorCode(2025032017, "试题不存在");
    ErrorCode ZK_ASKFOR_LEAVE_NOT_EXISTS = new ErrorCode(2025032018, "请假不存在");
    ErrorCode FISH_SYSTEM_LOG_NOT_EXISTS = new ErrorCode(2025032018, "fish系统日志不存在");
    ErrorCode ZK_MEETING_NOT_EXISTS = new ErrorCode(2025032019, "会议不存在");
    ErrorCode ZK_MEETING_RECORD_NOT_EXISTS = new ErrorCode(2025032020, "会议记录不存在");
    ErrorCode ZK_MEETING_USER_NOT_EXISTS = new ErrorCode(2025032021, "会议用户不存在");
    ErrorCode ZK_SCHEDULING_NOT_EXISTS = new ErrorCode(2025032022, "排班不存在");
    ErrorCode ZK_SCHEDULING_USER_NOT_EXISTS = new ErrorCode(2025032023, "排班用户不存在");
    ErrorCode ZK_TASK_NOT_EXISTS = new ErrorCode(2025032024, "任务不存在");
    ErrorCode CHAT_LOG_NOT_EXISTS = new ErrorCode(2025032025, "聊天日志不存在");
    ErrorCode ZK_DAY_RECORD_NOT_EXISTS = new ErrorCode(2025032026, "时间字典不存在");
    ErrorCode ZK_BREED_SPECIES_NOT_EXISTS = new ErrorCode(2025032027, "养殖品种不存在");
    ErrorCode ZK_ANNUAL_CAPACITY_PLAN_NOT_EXISTS = new ErrorCode(2025032028, "年度产能计划不存在");
    ErrorCode ZK_POND_GAIN_LOG_NOT_EXISTS = new ErrorCode(2025032029, "鱼生长不存在");

    ErrorCode ZK_PRODUCT_COST_NOT_EXISTS = new ErrorCode(2025032027, "成本不存在");
    ErrorCode ZK_FEEDING_LOG_NOT_EXISTS = new ErrorCode(2025032028, "投喂记录不存在");
    ErrorCode ZK_USE_MEDICINE_LOG_NOT_EXISTS = new ErrorCode(2025032029, "用药明细不存在");
    ErrorCode ZK_APP_LOG_NOT_EXISTS = new ErrorCode(2025032030, "app记录不存在");
    ErrorCode ZK_CHAT_GROUP_NOT_EXISTS = new ErrorCode(2025032031, "群聊组不存在");
    ErrorCode ZK_CHAT_GROUP_USER_NOT_EXISTS = new ErrorCode(2025032032, "群聊成员不存在");
    ErrorCode ZK_CHAT_MSG_NOT_EXISTS = new ErrorCode(2025032033, "聊天消息不存在");
    ErrorCode ZK_CHAT_MSG_RECEIVE_NOT_EXISTS = new ErrorCode(2025032034, "聊天消息接受不存在");
    ErrorCode ZK_USER_RELATION_NOT_EXISTS = new ErrorCode(2025032035, "用户信息关联不存在");
    ErrorCode ZK_EXPERIMENTAL_NOT_EXISTS = new ErrorCode(2025032036, "实验不存在");
    ErrorCode ZK_EXPERIMENTAL_DATA_NOT_EXISTS = new ErrorCode(2025032037, "实验数据采集不存在");
    ErrorCode ZK_EXPERIMENTAL_MEDIA_NOT_EXISTS = new ErrorCode(2025032038, "实验多媒体不存在");
    ErrorCode ZK_GROW_BAIT_INGREDIENTS_NOT_EXISTS = new ErrorCode(2025042038, "饵料成分对比不存在");
    ErrorCode ZK_GROW_DAILY_RECORD_NOT_EXISTS = new ErrorCode(2025042038, "每日生长与环境记录不存在");
    ErrorCode ZK_GROW_FARMING_BATCH_NOT_EXISTS = new ErrorCode(2025042038, "养殖批次不存在");
    ErrorCode ZK_GROW_FISH_SPECIES_NOT_EXISTS = new ErrorCode(2025042038, "鱼类品种不存在");
    ErrorCode ZK_GROW_MODEL_CONFIG_NOT_EXISTS = new ErrorCode(2025042038, "生长模型配置不存在");
    ErrorCode ZK_GROW_NUTRITIONAL_REQUIREMENTS_NOT_EXISTS = new ErrorCode(2025042038, "各阶段营养需求不存在");
    ErrorCode PURIFY_ARCHIVE_NOT_EXISTS = new ErrorCode(2026020501, "入池档案不存在");
    ErrorCode PURIFY_COST_DETAIL_NOT_EXISTS = new ErrorCode(2026020502, "成本明细记录不存在");
    ErrorCode PURIFY_DAILY_MONITORING_NOT_EXISTS = new ErrorCode(2026020503, "日常监测记录不存在");
    ErrorCode PURIFY_DICT_DATA_NOT_EXISTS = new ErrorCode(2026020504, "业务字典明细不存在");
    ErrorCode PURIFY_OUT_POOL_NOT_EXISTS = new ErrorCode(2026020505, "出池登记不存在");
    ErrorCode PURIFY_POOL_NOT_EXISTS = new ErrorCode(2026020506, "鱼池管理不存在");
    ErrorCode PURIFY_PROCESS_NOT_EXISTS = new ErrorCode(2026020507, "优化流程记录不存在");
    ErrorCode PURIFY_PURCHASE_NOT_EXISTS = new ErrorCode(2026020508, "采购记录不存在");
    ErrorCode PURIFY_TREATMENT_NOT_EXISTS = new ErrorCode(2026020509, "处理措施记录不存在");
    ErrorCode ZK_GROW_POOL_NOT_EXISTS = new ErrorCode(2026020510, "生长模型鱼池不存在");
    ErrorCode ZK_EXPERIMENTAL_POOL_NOT_EXISTS = new ErrorCode(2026020511, "实验鱼池不存在");
    ErrorCode ZK_POND_FISH_PARENT_NOT_EXISTS = new ErrorCode(2026020512, "鱼池操作父不存在");
    ErrorCode ZK_POND_HEALTH_NOT_EXISTS = new ErrorCode(2026020513, "鱼池健康不存在");
    ErrorCode ZK_MEDICINE_LOG_POND_NOT_EXISTS = new ErrorCode(2026020514, "用药存塘关联不存在");
    ErrorCode ZK_GROW_BAIT_NUTRITION_NOT_EXISTS = new ErrorCode(2026020515, "饵料营养成分不存在");
    ErrorCode ZK_EXPERIMENTAL_TEMPLATE_NOT_EXISTS = new ErrorCode(2026020516, "实验模板不存在");
    ErrorCode ZK_EXPERIMENTAL_PERMISSION_USER_NOT_EXISTS = new ErrorCode(2026020517, "实验权限用户不存在");

    ErrorCode DOCUMENT_NOT_EXISTS = new ErrorCode(2025032035, "文档不存在");
    ErrorCode OA_WORK_PLAN_NOT_EXISTS = new ErrorCode(2025032036, "OA工作计划不存在");
    ErrorCode OA_ATTENDANCE_SETTING_NOT_EXISTS = new ErrorCode(2025032037, "OA考勤设置不存在");
    ErrorCode DEPT_WORK_PLAN_NOT_EXISTS = new ErrorCode(2025032038, "部门工作计划不存在");
    ErrorCode ATTENDANCE_LOCATION_REQUIRED = new ErrorCode(2025032039, "位置信息必填，请开启定位权限");
    ErrorCode ATTENDANCE_LOCATION_OUT_OF_RANGE = new ErrorCode(2025032040, "不在打卡范围内");
    ErrorCode ZK_SCATTERED_INVENTORY_NOT_EXISTS = new ErrorCode(2025032041, "零星库存不存在");
    ErrorCode ZK_POND_FISH_TYPE_NOT_EXISTS = new ErrorCode(2025032042, "鱼苗种类不存在");

    // ========== zk jidi 模块========== end
    ErrorCode CUST_NOT_EXISTS = new ErrorCode(2023032001, "客户不存在");

    ErrorCode CUST_TEL_EXISTS = new ErrorCode(2023032001, "客户电话已存在");

    ErrorCode PRODUCT_CATEGORY_NOT_EXISTS = new ErrorCode(2023032002, "商品分类不存在");

    ErrorCode SUPPLIER_NOT_EXISTS = new ErrorCode(2023032002, "供应商不存在");
    ErrorCode SUPPLIER_EXISTS = new ErrorCode(2023032002, "供应商已存在");
    ErrorCode SUPPLIER_NAME_EXISTS = new ErrorCode(2023032002, "供应商名称已存在");

    ErrorCode SUPPLIER_NO_NOT_EMPTY = new ErrorCode(2023032002, "供应商编号不能为空");
    ErrorCode SUPPLIERNAME_NO_NOT_EMPTY = new ErrorCode(2023032002, "供应商名称不能为空");

    ErrorCode SUPPLIE_DATA_NO_NOT_EMPTY = new ErrorCode(2023032002, "供应商还没有创建，请先创建供应商信息");

    ErrorCode PRODUCT_NOT_EXISTS = new ErrorCode(2023032002, "商品不存在");

    ErrorCode PRODUCT_NO_EXISTS = new ErrorCode(1002003000, "商品编号已经存在");

    ErrorCode ORDER_NOT_EXISTS = new ErrorCode(2023032002, "标准订单不存在");

    ErrorCode STORE_HOUSE_NOT_EXISTS = new ErrorCode(2023032002, "仓库不存在");

    ErrorCode STANDARD_PURCHASE_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "标准采购订单不存在");

    ErrorCode ORDER_PRODUCT_NOT_EXISTS = new ErrorCode(2023032002, "订单商品不存在");

    ErrorCode STANDARD_OUT_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "标准出库订单不存在");

    ErrorCode STANDARD_PURCHASE_ORDER_PRODUCT_NOT_EXISTS = new ErrorCode(2023032002, "采购订单商品不存在");

    ErrorCode STANDARD_OUT_ORDER_PRODUCT_NOT_EXISTS = new ErrorCode(2023032002, "标准出库订单商品不存在");

    ErrorCode STOCK_PURCHASE_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "库存采购订单不存在");

    ErrorCode STOCK_PURCHASE_ORDER_PRODUCT_NOT_EXISTS = new ErrorCode(2023032002, "库存采购订单商品不存在");

    ErrorCode ORDER_INVOICE_NOT_EXISTS = new ErrorCode(2023032002, "销售订单发票不存在");

    ErrorCode ORDER_INVOICE_PRODUCT_NOT_EXISTS = new ErrorCode(2023032002, "订单发票商品不存在");

    ErrorCode REVENUE_NOT_EXISTS = new ErrorCode(2023032002, "收入不存在");

    ErrorCode REVENUE_CLAIM_NOT_EXISTS = new ErrorCode(2023032002, "认领不存在");

    ErrorCode DEBT_RECEIVABLE_NOT_EXISTS = new ErrorCode(2023032002, "应收账款不存在");

    ErrorCode PETTY_CASH_DETAIL_NOT_EXISTS = new ErrorCode(2023032002, "备用金明细不存在");

    ErrorCode PRESTORE_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "预存单不存在");

    ErrorCode RESERVE_FUND_ACCOUNT_NOT_EXISTS = new ErrorCode(2023032002, "备用金账户不存在");

    ErrorCode RESERVE_FUND_DETAIL_NOT_EXISTS = new ErrorCode(2023032002, "备用金明细不存在");

    ErrorCode PURCHASE_PAY_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "日常采购付款不存在");
    ErrorCode CUST_ACCOUNT_NOT_EXISTS = new ErrorCode(2023032002, "客户账户不存在");
    ErrorCode CUST_PRESTORE_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "预存单不存在");
    ErrorCode CUST_PRESTORE_DETAIL_NOT_EXISTS = new ErrorCode(2023032002, "客户预存明细不存在");
    ErrorCode CUST_UNIT_NOT_EXISTS = new ErrorCode(2023032002, "客户单位不存在");
    ErrorCode UNSTANDARD_PURCHASE_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "非标采购订单不存在");
    ErrorCode UNSTANDARD_PURCHASE_ORDER_PRODUCT_NOT_EXISTS = new ErrorCode(2023032002, "采购订单商品不存在");
    ErrorCode PURCHASE_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "采购订单不存在");
    ErrorCode PURCHASE_ORDER_PRODUCT_NOT_EXISTS = new ErrorCode(2023032002, "采购订单商品不存在");
    ErrorCode OUT_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "标准出库订单不存在");
    ErrorCode OUT_ORDER_PRODUCT_NOT_EXISTS = new ErrorCode(2023032002, "标准出库订单商品不存在");
    ErrorCode OUT_ORDER_PRODUCT_NOT_ENOUGH = new ErrorCode(2023032002, "商品库存不够");
    ErrorCode RETURN_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "退货订单不存在");
    ErrorCode REIMBURSE_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "日常报销不存在");
    ErrorCode IN_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "入库订单不存在");
    ErrorCode ALLOWANCE_NOT_EXISTS = new ErrorCode(2023032002, "津贴不存在");
    ErrorCode PERFORMANCE_NOT_EXISTS = new ErrorCode(2023032002, "销售业绩不存在");
    ErrorCode PRODUCT_STOCK_NOT_EXISTS = new ErrorCode(2023032002, "商品库存不存在");
    ErrorCode SELL_LEVEL_NOT_EXISTS = new ErrorCode(2023032002, "销售职级不存在");
    ErrorCode COMMISSION_RATIO_NOT_EXISTS = new ErrorCode(2023032002, "提成比例不存在");
    ErrorCode COMMENT_NOT_EXISTS = new ErrorCode(2023032002, "评论不存在");
    ErrorCode BUSINESS_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "商务费订单不存在");
    ErrorCode PRESTORE_WITHDRAW_NOT_EXISTS = new ErrorCode(2023032002, "预存提现单不存在");
    ErrorCode PURCHASE_RELATION_NOT_EXISTS = new ErrorCode(2023032002, "采购订单关联不存在");
    ErrorCode ORDER_OPERATE_LOG_NOT_EXISTS = new ErrorCode(2023032002, "订单操作记录不存在");
    ErrorCode ORDER_QUOTE_NOT_EXISTS = new ErrorCode(2023032002, "报价单不存在");
    ErrorCode ORDER_QUOTE_WRITE_ERROR = new ErrorCode(2023032002, "报价单写入异常");
    ErrorCode SCHOOL_UNIT_NOT_EXISTS = new ErrorCode(2023032002, "单位名称不存在");
    ErrorCode SCHOOL_UNIT_NAME_EXISTS = new ErrorCode(2023032002, "客户单位/学院实验室/课题组已存在");
    ErrorCode INVOICE_PRODUCT_NOT_EXISTS = new ErrorCode(2023032002, "订单商品不存在");
    ErrorCode PURCHASE_PAY_ORDER_PRODUCT_NOT_EXISTS = new ErrorCode(2023032002, "订单商品不存在");
    ErrorCode RETURN_ORDER_PRODUCT_NOT_EXISTS = new ErrorCode(2023032002, "订单商品不存在");
    ErrorCode BUSINESS_ORDER_DETAIL_NOT_EXISTS = new ErrorCode(2023032002, "商务费明细不存在");
    ErrorCode PRODUCT_MODIFY_RECORD_NOT_EXISTS = new ErrorCode(2023032002, "商品修改记录不存在");
    ErrorCode CUST_NEED_NOT_EXISTS = new ErrorCode(2023032002, "客户需求不存在");
    ErrorCode USTD_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "非标订单不存在");
    ErrorCode LEAVE_APPLY_NOT_EXISTS = new ErrorCode(2023032002, "离职申请不存在");
    ErrorCode ONLINE_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "标准订单不存在");
    ErrorCode USER_LEVEL_NOT_EXISTS = new ErrorCode(2023032002, "销售级别不存在");
    ErrorCode USER_EXAMINE_NOT_EXISTS = new ErrorCode(2023032002, "销售职级不存在");
    ErrorCode PRESTORE_REGISTER_NOT_EXISTS = new ErrorCode(2023032002, "预存登记不存在");
    ErrorCode ALLOWANCE_DEDUCT_REISSUE_NOT_EXISTS = new ErrorCode(2023032002, "应收暂扣补发不存在");
    ErrorCode OUT_RELATION_NOT_EXISTS = new ErrorCode(2023032002, "出库订单关联不存在");
    ErrorCode STOCK_ALLOCATE_NOT_EXISTS = new ErrorCode(2023032002, "库存调拨不存在");
    ErrorCode TRANSFER_APPLY_NOT_EXISTS = new ErrorCode(2023032002, "转正申请不存在");
    ErrorCode REVENUE_ANALYSIS_NOT_EXISTS = new ErrorCode(2023032002, "营收分析不存在");
    ErrorCode ORDER_QUOTE_PRODUCT_NOT_EXISTS = new ErrorCode(2023032002, "报价单商品关联不存在");
    ErrorCode CUST_VISIT_NOT_EXISTS = new ErrorCode(2023032002, "客户拜访不存在");

    ErrorCode PRODUCT_BRAND_NOT_EXISTS = new ErrorCode(2023032002, "商品品牌不存在");

    ErrorCode AFTER_SALES_ORDER_NOT_EXISTS = new ErrorCode(2023032002, "售后不存在");
    ErrorCode ZK_SCHEDULING_TYPE_NOT_EXISTS = new ErrorCode(2023032003, "检测配置不存在");
    ErrorCode TASK_DETAIL_NOT_EXISTS = new ErrorCode(2023032004, "任务明细不存在");
    ErrorCode ZK_POND_SYSTEM_NOT_EXISTS = new ErrorCode(2023032005, "存塘系统不存在");
    ErrorCode ZK_FISH_OPERATION_LOG_NOT_EXISTS = new ErrorCode(2023032006, "鱼苗统计不存在");
    ErrorCode ZK_BIOLOGY_CAPITAL_NOT_EXISTS = new ErrorCode(2023032007, "生物性资产不存在");
    ErrorCode ZK_DATA_REVISE_NOT_EXISTS = new ErrorCode(2023032008, "数据修正记录不存在");
    // 缺少ZK_NET_BOX_NOT_EXISTS报错常量定义临时添加
    ErrorCode ZK_NET_BOX_NOT_EXISTS = new ErrorCode(2023032009, "数据修正记录不存在");
    ErrorCode ZK_SPECS_LOG_NOT_EXISTS = new ErrorCode(2023032010, "规格记录不存在");

    ErrorCode ZK_BIOLOGICAL_ASSET_NOT_EXISTS = new ErrorCode(2023033001, "生物资产评估不存在");
    ErrorCode ZK_FISH_HEALTH_NOT_EXISTS = new ErrorCode(2023033002, "鱼类健康评估不存在");
    ErrorCode ZK_WORK_PLAN_NOT_EXISTS = new ErrorCode(2023033003, "工作目标与计划不存在");

    ErrorCode ZK_PRECISION_FEEDING_NOT_EXISTS = new ErrorCode(2023033004, "精准投喂不存在");
    ErrorCode ZK_INSPECTION_REPORT_NOT_EXISTS = new ErrorCode(2023033005, "巡检智能分析报告不存在");
    ErrorCode ZK_DEATH_SURVIVAL_RATE_NOT_EXISTS = new ErrorCode(2023033006, "死亡和存活率不存在");
    ErrorCode ZK_GROWTH_MODEL_NOT_EXISTS = new ErrorCode(2023033007, "生长模型不存在");

    ErrorCode ZK_DEVICE_ALARM_NOT_EXISTS = new ErrorCode(2023033008, "设备报警不存在");
    ErrorCode ZK_INTELLIGENT_DIAGNOSIS_NOT_EXISTS = new ErrorCode(2023033009, "智能诊断不存在");
    ErrorCode ZK_FISH_PROOFING_NOT_EXISTS = new ErrorCode(2023033010, "打样记录不存在");
    ErrorCode ZK_DEVICE_METRIC_NOT_EXISTS = new ErrorCode(2023033011, "设备指标不存在");
    ErrorCode ZK_DEVICE_RECORD_NOT_EXISTS = new ErrorCode(2023033012, "设备数据记录不存在");
    ErrorCode ZK_THRESHOLD_RULE_NOT_EXISTS = new ErrorCode(2023033013, "阈值规则不存在");
    ErrorCode ZK_DEVICE_PROPERTIES_NOT_EXISTS = new ErrorCode(2023033014, "设备属性记录不存在");
    ErrorCode ZK_DEVICE_EVENT_NOT_EXISTS = new ErrorCode(2023033015, "设备事件记录不存在");
    ErrorCode ZK_KONWLEDGE_TREE_NOT_EXISTS = new ErrorCode(2023033016, "知识树不存在");
    ErrorCode ZK_FCR_MODEL_NOT_EXISTS = new ErrorCode(2023033017, "饵料系数FCR模型不存在");
    ErrorCode OA_USER_RATING_NOT_EXISTS = new ErrorCode(2023033018, "OA他人打分不存在");
    ErrorCode OA_WORK_PERFORMANCE_NOT_EXISTS = new ErrorCode(2023033019, "OA工作绩效评分不存在");

}
