package cn.iocoder.yudao.module.fish.controller.admin.zkschedulinguser.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 排班用户创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkSchedulingUserCreateReqVO extends ZkSchedulingUserBaseVO {

}
