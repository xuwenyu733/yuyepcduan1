package cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo;

import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.DetailJsonItem;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 存塘创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkPondCreateReqVO extends ZkPondBaseVO {

    // zk_pond_fish 数量信息
    private List<DetailJsonItem> detailJson;

}
