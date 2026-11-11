package cn.iocoder.yudao.module.fish.controller.admin.zkfcrmodel.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 饵料系数FCR模型创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkFcrModelCreateReqVO extends ZkFcrModelBaseVO {

}
