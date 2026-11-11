package cn.iocoder.yudao.module.fish.controller.admin.zkprecisionfeeding.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "投喂 AI 智能分析")
@Data
public class PrecisionFeedAiAnalysisRespVO {

    private Long pondId;
    private String pondName;
    /** 良好 / 关注 / 介入 */
    private String healthLevel;
    private String summary;
    private BigDecimal smartTotalKg;
    private BigDecimal actualTotalKg;
    private BigDecimal deviationPercent;
    private List<String> deviationReasons;
    private List<String> suggestions;
    private Boolean needAlarm;
}
