package cn.iocoder.yudao.module.fish.controller.admin.zkdayrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 时间字典更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDayRecordUpdateReqVO extends ZkDayRecordBaseVO {

    @Schema(description = "编号", required = true, example = "23138")
    @NotNull(message = "编号不能为空")
    private Long id;

}
