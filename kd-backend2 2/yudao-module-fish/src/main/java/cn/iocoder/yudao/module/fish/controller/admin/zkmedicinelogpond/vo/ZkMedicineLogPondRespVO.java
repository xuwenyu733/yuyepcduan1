package cn.iocoder.yudao.module.fish.controller.admin.zkmedicinelogpond.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 用药存塘关联 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkMedicineLogPondRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2091")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "存塘id", example = "5492")
    @ExcelProperty("存塘id")
    private Long pondId;

    @Schema(description = "用药表id( 表: zk_use_medicine_log.id)", example = "1330")
    @ExcelProperty("用药表id( 表: zk_use_medicine_log.id)")
    private Long medicineLogId;

}