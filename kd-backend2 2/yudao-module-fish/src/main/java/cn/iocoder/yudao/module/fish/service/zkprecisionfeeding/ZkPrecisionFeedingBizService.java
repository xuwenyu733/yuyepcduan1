package cn.iocoder.yudao.module.fish.service.zkprecisionfeeding;

import cn.iocoder.yudao.module.fish.controller.admin.zkprecisionfeeding.vo.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 精准投喂业务能力（推荐量、计划、消耗、AI 分析）
 */
public interface ZkPrecisionFeedingBizService {

    PrecisionFeedRecommendRespVO recommend(Long pondId);

    PrecisionFeedPlanVO savePlan(PrecisionFeedPlanVO plan);

    PrecisionFeedPlanVO confirmPlan(Long pondId, String planType, LocalDate planDate);

    List<PrecisionFeedPlanVO> listPlans(Long pondId, LocalDate planDate);

    PrecisionFeedConsumptionRespVO feedConsumption(Long workshopId, Long pondSystemId, Long pondId,
                                                   LocalDate beginDate, LocalDate endDate);

    PrecisionFeedAiAnalysisRespVO aiAnalysis(Long pondId);

    List<PrecisionFeedAffectingTaskVO> affectingTasks(Long pondId);

    /** 批量补齐鱼池健康/水质/死亡/密度等扩展字段 */
    void enrichPondList(List<cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo.PrecisionFeedingPondRespVO> ponds);
}
