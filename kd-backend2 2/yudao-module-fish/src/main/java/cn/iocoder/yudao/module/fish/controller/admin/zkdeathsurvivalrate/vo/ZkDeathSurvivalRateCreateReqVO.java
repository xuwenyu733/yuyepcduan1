package cn.iocoder.yudao.module.fish.controller.admin.zkdeathsurvivalrate.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 死亡和存活率创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDeathSurvivalRateCreateReqVO extends ZkDeathSurvivalRateBaseVO {

}
