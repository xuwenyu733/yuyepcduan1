package cn.iocoder.yudao.module.fish.service.evaluation;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.evaluation.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.evaluation.OaEvaluationDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * OA考核评分 Service 接口
 *
 * @author 系统管理员
 */
public interface OaEvaluationService {

    /**
     * 获得OA考核评分
     *
     * @param id 编号
     * @return OA考核评分
     */
    OaEvaluationDO getOaEvaluation(Long id);

    /**
     * 获得OA考核评分分页
     *
     * @param pageReqVO 分页查询
     * @return OA考核评分分页
     */
    PageResult<OaEvaluationDO> getOaEvaluationPage(OaEvaluationPageReqVO pageReqVO);

    /**
     * 获得OA考核评分列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return OA考核评分列表
     */
    List<OaEvaluationDO> getOaEvaluationList(OaEvaluationExportReqVO exportReqVO);

    /**
     * 获取个人考核数据
     *
     * @param period 考核期（格式：yyyy-MM）
     * @return 个人考核数据
     */
    Map<String, Object> getPersonalEvaluation(String period);

    /**
     * 获取个人考核趋势数据（近6个月）
     *
     * @return 趋势数据列表
     */
    List<Map<String, Object>> getPersonalEvaluationTrend();

    /**
     * 获取个人考核统计摘要
     *
     * @return 统计摘要数据
     */
    Map<String, Object> getPersonalEvaluationSummary();

    /**
     * 获取部门排名数据
     *
     * @param period 考核期（格式：yyyy-MM）
     * @return 部门排名数据列表
     */
    List<Map<String, Object>> getDepartmentRanking(String period);

    /**
     * 获取历史考核记录
     *
     * @param params 查询参数
     * @return 历史记录列表
     */
    List<Map<String, Object>> getEvaluationHistory(Map<String, Object> params);

    /**
     * 执行考核评分计算（定时任务调用）
     *
     * @param evaluationPeriod 考核期（格式：yyyy-MM）
     */
    void calculateEvaluation(String evaluationPeriod);

    /**
     * 录入实操考试得分（0-5），并重算该用户当期考核
     */
    void setPracticeExamScore(Long userId, String evaluationPeriod, java.math.BigDecimal practiceScore);

    /**
     * 考核维度满分配置（对齐《已有模块-OA考核指标》）
     */
    Map<String, Object> getDimensionWeights();

}



