package cn.iocoder.yudao.module.fish.controller.admin.zkillcontrol.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 疾病诊断与防治记录 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkIllControlRespVO extends ZkIllControlBaseVO {

    @Schema(description = "编号", required = true, example = "1637")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;
    private String creator;

}
