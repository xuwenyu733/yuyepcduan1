package cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog.vo;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 投喂记录创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkFeedingLogCreateReqVO extends ZkFeedingLogBaseVO {


    public ZkFeedingLogBaseVO build_differenceBefore(BigDecimal avg) {
        BigDecimal sub = EasyBigDecimalUtil.subtract(this.getFeedCount(), avg);
        // if(EasyBigDecimalUtil.gt0(sub)) this.setDifferenceBefore(String.format("与前五天对比多喂%s", sub));
        // else this.setDifferenceBefore(String.format("与前五天对比少喂%s", EasyBigDecimalUtil.subtract(new BigDecimal(0), sub)));
        if(EasyBigDecimalUtil.gt0(sub)) this.setDifferenceBefore(String.format("+%s", sub));
        else this.setDifferenceBefore(String.format("-%s", EasyBigDecimalUtil.subtract(new BigDecimal(0), sub)));
        return this;
    }

}
