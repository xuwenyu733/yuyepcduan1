package cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 鱼苗统计创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkFishOperationLogCreateReqVO extends ZkFishOperationLogBaseVO {

}
