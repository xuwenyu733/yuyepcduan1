package cn.iocoder.yudao.module.fish.controller.admin.zkmedicinelogpond.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

@Schema(description = "管理后台 - 用药存塘关联分页 Request VO")
@Data
public class ZkMedicineLogPondPageReqVO extends PageParam {

    @Schema(description = "存塘id", example = "5492")
    private Long pondId;

    @Schema(description = "用药表id( 表: zk_use_medicine_log.id)", example = "1330")
    private Long medicineLogId;

}