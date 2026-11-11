package cn.iocoder.yudao.module.fish.controller.admin.zkmedicinelogpond.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 用药存塘关联新增/修改 Request VO")
@Data
public class ZkMedicineLogPondSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2091")
    private Long id;

    @Schema(description = "存塘id", example = "5492")
    private Long pondId;

    @Schema(description = "用药表id( 表: zk_use_medicine_log.id)", example = "1330")
    private Long medicineLogId;

}