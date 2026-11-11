package cn.iocoder.yudao.module.fish.controller.admin.zkpondgainlog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 鱼生长新增/修改 Request VO")
@Data
public class ZkPondGainLogSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "4537")
    private Long id;

    @Schema(description = "车间id", example = "16632")
    private Long workshopId;

    @Schema(description = "系统id", example = "854")
    private Long systemId;

    @Schema(description = "存塘id", example = "6795")
    private Long pondId;

    @Schema(description = "批次编号")
    private String batchNumber;

    @Schema(description = "统计日期")
    private String staticDay;

    @Schema(description = "鱼苗数量")
    private Long fishNum;

    @Schema(description = "鱼苗规格")
    private String fishSpec;

    @Schema(description = "今日鱼增加重量", example = "10407")
    private BigDecimal fishGainCount;

    @Schema(description = "所属部门", example = "32733")
    private Long deptId;

}