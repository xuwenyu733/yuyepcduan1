package cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch.po.OutPo;
import cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch.po.SalePo;
import cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch.po.TransferPo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @创建人 lr
 * @创建时间 2025-09-19
 * @描述
 */
@Data
@NoArgsConstructor
public class FishNodeDetail {

    private String transferTime; // 转入时间
    private Long transferFishNum; // 转入数量
    private BigDecimal transferFishWeight; // 转入重量
    private BigDecimal transferFishSpec; // 转入规格
    private BigDecimal transferBaitCoefficient; // 转入饵料系数
    private Long transferSevenDayMortalityRate; // 投放七日死亡量
    private BigDecimal transferSevenDayMortalityRatePer; // 投放七日死亡率
    private List<TransferPo> transferPoList; // 转入信息列表

    private String outTime; // 转出时间
    private Long outQuantity; // 转出数量
    private BigDecimal outFishWeight; // 转出重量
    private BigDecimal outFishSpec; // 转出规格
    private BigDecimal outBaitCoefficient; // 转出饵料系数
    private List<OutPo> outPoList; // 转出信息列表
    // 养殖统计
    private BigDecimal dailySpecifications; // 当日规格
    private BigDecimal avgSpecifications; // 平均规格
    private BigDecimal accumulatedFeedingAmount; // 累计投喂量
    private Long accumulativeDeathToll; // 累计死亡量
    private BigDecimal feedCoefficient; // 饵料系数
    private BigDecimal inputCost; // 投入成本
    private BigDecimal biologicalAssets; // 生物资产
    private BigDecimal sumBiologicalAssets; // 总生物资产
    // 打样
    private String proofingTime; // 打样时间
    private Long proofingFishNum; // 打样数量
    private BigDecimal proofingFishWeight; // 打样重量

    // 销售信息
    private String saleTime; // 销售时间
    private Long saleFishNum; // 销售数量
    private BigDecimal saleFishSpec; // 销售规格
    private BigDecimal saleFishWeight; // 销售重量
    private String purchaseDirection; // 销售方向
    private List<SalePo> salePosList; // 销售信息

    public FishNodeDetail(String transferTime, String transferFishNum, String transferFishSpec) {
        try {
            this.transferTime = transferTime;
            this.transferFishNum = EasyBigDecimalUtil.toLong(transferFishNum);
            this.transferFishSpec = EasyBigDecimalUtil.toNumber(transferFishSpec);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
    }
    public FishNodeDetail(String transferTime, String transferFishNum, String transferFishSpec, String transferBaitCoefficient) {
        try {
            this.transferTime = transferTime;
            this.transferFishNum = EasyBigDecimalUtil.toLong(transferFishNum);
            this.transferFishSpec = EasyBigDecimalUtil.toNumber(transferFishSpec);
            this.transferBaitCoefficient = EasyBigDecimalUtil.toNumber(transferBaitCoefficient);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
    }
    public FishNodeDetail build_fishBase(String transferTime, String transferFishNum, String transferFishSpec) {
        try {
            this.transferTime = transferTime;
            this.transferFishNum = EasyBigDecimalUtil.toLong(transferFishNum);
            this.transferFishSpec = EasyBigDecimalUtil.toNumber(transferFishSpec);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
        return this;
    }
    public FishNodeDetail build_fishBase(String transferTime, String transferFishNum, String transferFishSpec, String transferBaitCoefficient) {
        try {
            this.transferTime = transferTime;
            this.transferFishNum = EasyBigDecimalUtil.toLong(transferFishNum);
            this.transferFishSpec = EasyBigDecimalUtil.toNumber(transferFishSpec);
            this.transferBaitCoefficient = EasyBigDecimalUtil.toNumber(transferBaitCoefficient);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
        return this;
    }
    public FishNodeDetail build_accumulate(String dailySpecifications, String accumulatedFeedingAmount, String accumulativeDeathToll,
                                           String feedCoefficient, String inputCost, String biologicalAssets) {
        try {
            BigDecimal currSpec = EasyBigDecimalUtil.toNumber(dailySpecifications);
            this.dailySpecifications = currSpec;
            // this.avgSpecifications = currSpec;
            this.accumulatedFeedingAmount = EasyBigDecimalUtil.toNumber(accumulatedFeedingAmount);
            this.accumulativeDeathToll = EasyBigDecimalUtil.toLong(accumulativeDeathToll);

            this.feedCoefficient = EasyBigDecimalUtil.toNumber(feedCoefficient);
            this.inputCost = EasyBigDecimalUtil.toNumber(inputCost);
            this.biologicalAssets = EasyBigDecimalUtil.toNumber(biologicalAssets);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
        return this;
    }


    public FishNodeDetail build_proofing(LocalDateTime createTime, Long currNum, BigDecimal proofingFishWeight) {
        this.proofingTime = EasyDateUtil.format(createTime, "yyyy-MM-dd HH:mm:ss");
        this.proofingFishNum = currNum;
        this.proofingFishWeight = proofingFishWeight;
        return this;
    }

    public FishNodeDetail build_transferPoList(List<TransferPo> transferPoList) {
        if(EasyListUtil.isEmpty(transferPoList)) return this;
        if(this.getTransferPoList() == null) {
            this.setTransferPoList(new ArrayList<>());
        }
        this.getTransferPoList().addAll(transferPoList);
        return this;
    }
}
