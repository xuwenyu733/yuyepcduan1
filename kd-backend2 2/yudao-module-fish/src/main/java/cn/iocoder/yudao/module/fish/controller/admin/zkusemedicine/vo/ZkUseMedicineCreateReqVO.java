package cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine.vo;

import cn.iocoder.yudao.module.workflow.api.dto.TaskParam;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 用药记录创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkUseMedicineCreateReqVO extends ZkUseMedicineBaseVO {

    TaskParam taskParam;
}
