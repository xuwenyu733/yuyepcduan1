package cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 投喂记录更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkFeedingLogUpdateReqVO extends ZkFeedingLogBaseVO {

    @Schema(description = "编号", required = true, example = "6281")
    @NotNull(message = "编号不能为空")
    private Long id;

    private Long originData; // 原数据
    private Long differenceData; // 差值
    private BigDecimal changeRatio; // 变化比例
    private String reason; // 原因

}
