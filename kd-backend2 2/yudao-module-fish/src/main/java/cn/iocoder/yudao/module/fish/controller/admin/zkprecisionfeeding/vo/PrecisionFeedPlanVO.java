package cn.iocoder.yudao.module.fish.controller.admin.zkprecisionfeeding.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "智能/实际投喂计划")
@Data
public class PrecisionFeedPlanVO {

    private Long id;

    /** 鱼池 ID；也可只传 pondDetailNum，由后端解析 */
    private Long pondId;
    private String pondName;
    private LocalDate planDate;

    /** smart / actual */
    @NotNull
    private String planType;

    private BigDecimal totalAmountKg;
    private Integer frequency;
    private BigDecimal singleAmountKg;
    private String feedType;

    /** draft / confirmed */
    private String status;

    /** 存塘编号（前端可传此字段代替 pondId） */
    private String pondDetailNum;

    private String remark;
}
