package cn.iocoder.yudao.module.fish.controller.admin.zkworkplan.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 工作目标与计划创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkWorkPlanCreateReqVO extends ZkWorkPlanBaseVO {

}
