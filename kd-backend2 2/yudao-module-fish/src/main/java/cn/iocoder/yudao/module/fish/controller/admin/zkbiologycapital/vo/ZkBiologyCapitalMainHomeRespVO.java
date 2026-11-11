package cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologycapital.MainHomeStatDO.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Schema(description = "管理后台 - 首页统计数据 Response VO")
@Data
public class ZkBiologyCapitalMainHomeRespVO {

    @Schema(description = "生物资产评估")
    private List<BiologyCapital> biologyCapitals;

    @Schema(description = "死亡量存活率")
    private List<SurvivalRate> survivalRates;

    @Schema(description = "精准投喂系统")
    private List<PrecisionFeeding> precisionFeedings;

    @Schema(description = "鱼生长大模型")
    private List<FishGrowth> fishGrowths;

    @Schema(description = "饵料系数")
    private List<BaitCoefficient> baitCoefficients;
}
