package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 存塘鱼苗明细更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkPondFishDetailUpdateReqVO extends ZkPondFishDetailBaseVO {

    @Schema(description = "编号", required = true, example = "24646")
    @NotNull(message = "编号不能为空")
    private Long id;


    private Long originData; // 原数据
    private Long differenceData; // 差值
    private BigDecimal changeRatio; // 变化比例
    private String reason; // 原因

}
