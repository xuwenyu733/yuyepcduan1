package cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VariousFryStagesCostRespVO {
    @JsonProperty("first_day")
    private LocalDateTime firstDay;
    
    @JsonProperty("workshop_name")
    private String workshopName;
    
    @JsonProperty("pond_detail_num")
    private String pondDetailNum;
    
    @JsonProperty("pond_num")
    private String pondNum;
    
    @JsonProperty("pond_id")
    private Long pondId;
    
    @JsonProperty("fish_spec")
    private String fishSpec;
    
    @JsonProperty("seedling_stage_cost")
    private BigDecimal seedlingStageCost;
    
    @JsonProperty("juvenile_stage_cost")
    private BigDecimal juvenileStageCost;
    
    @JsonProperty("early_adult_stage_cost")
    private BigDecimal earlyAdultStageCost;
    
    @JsonProperty("late_adult_stage_cost")
    private BigDecimal lateAdultStageCost;
    
    @JsonProperty("total_feeding_cost")
    private BigDecimal totalFeedingCost;
    
    @JsonProperty("cost_per_kg")
    private BigDecimal costPerKg;
}
