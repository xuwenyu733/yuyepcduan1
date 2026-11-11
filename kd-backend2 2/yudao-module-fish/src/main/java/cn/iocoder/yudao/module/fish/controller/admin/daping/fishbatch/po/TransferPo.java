package cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch.po;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.PondFishDetailAddTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TransferPo {

    private String transferTime; // 转入时间
    private Long transferFishNum; // 转入数量
    private BigDecimal transferFishWeight; // 转入重量
    private BigDecimal transferFishSpec; // 转入规格
    private BigDecimal transferBaitCoefficient; // 转入饵料系数
    private Long transferSevenDayMortalityRate; // 转入七日/三日死亡量
    private BigDecimal transferSevenDayMortalityRatePer; // 转入七日/三日死亡率
    private String addType = PondFishDetailAddTypeEnum.ADD.getAddType(); // 转入 类型
    private Long splitPondId; // 转入源存塘id
    private String splitPondDetailNum; // 转入源存塘编号

    public TransferPo(String transferTime, Long transferFishNum, BigDecimal transferFishWeight, BigDecimal transferFishSpec,
                      BigDecimal transferBaitCoefficient, Long transferSevenDayMortalityRate,
                      BigDecimal transferSevenDayMortalityRatePer, String addType, Long splitPondId) {
        this.transferTime = transferTime;
        this.transferFishNum = transferFishNum;
        this.transferFishWeight = transferFishWeight;
        if(EasyBigDecimalUtil.compare0(this.transferFishWeight, "LE")) {
            BigDecimal weight4g = EasyBigDecimalUtil.multiply(transferFishSpec, new BigDecimal(transferFishNum));
            this.transferFishWeight = EasyBigDecimalUtil.divide(weight4g, new BigDecimal(1000));
        }
        this.transferFishSpec = transferFishSpec;
        this.transferBaitCoefficient = transferBaitCoefficient;
        this.transferSevenDayMortalityRate = transferSevenDayMortalityRate;
        this.transferSevenDayMortalityRatePer = transferSevenDayMortalityRatePer;
        this.addType = addType;
        this.splitPondId = splitPondId;
    }
}
