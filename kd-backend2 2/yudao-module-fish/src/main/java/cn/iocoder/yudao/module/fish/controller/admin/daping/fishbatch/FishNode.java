package cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog.ZkFishOperationLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @创建人 lr
 * @创建时间 2025-09-19
 * @描述
 */
@Data
@NoArgsConstructor
public class FishNode {

    private String batchNumber; // 鱼苗批次
    private String originBatchNumber; // 原始鱼苗批次
    private Long originBatchNumberFishNum = 0L; // 原始鱼苗批次鱼数
    private Long workshopId; // 车间id
    private String workshopName; // 名称
    private Long pondSystemId; // 系统id
    private String pondSystemName; // 系统名称
    private String pondNum; // 系统编号
    private Long pondId; // 存塘id
    private String pondName; // 存塘名称
    private String pondDetailNum; // 存塘编号
    private BigDecimal pondScale; // 存塘规模
    private BigDecimal realTimeStoragePool; // 实时存池
    private BigDecimal survivalRate = new BigDecimal(1); // 成活率
    private String fishSpec; // 规格
    private BigDecimal fishDensity; // 密度
    private BigDecimal feedCount; // 今日投喂量
    private String feedCountTrend; // 今日投喂量 走势
    private String subtractFeedCount; // 今日投喂量 历史差值
    private BigDecimal deadCount; // 今日死亡量
    private String deadCountTrend; // 今日死亡量 走势
    private String subtractDeadCount; // 今日死亡量 历史差值

    // 后面新增的 信息
    private String batchNumberInfo; // 鱼苗批次信息
    private Long sevenDayMortalityRate = 0L; // 投放七日死亡量
    private BigDecimal sevenDayMortalityRatePer; // 投放七日死亡率
    private Long accumulativeDeathToll = 0L; // 累计死亡量
    private BigDecimal mortalityRate; // 累计死亡率
    private BigDecimal accumulatedFeedingAmount; // 累计投喂量
    private BigDecimal accumulatedWeightGain; // 累计增重
    private Long saleFishNum = 0L; // 销售数量
    private BigDecimal saleWeight; // 销售重量
    private BigDecimal avgFishSpec; // 平均规格，目前和fishSpec一个值
    private BigDecimal biologicalAssets; // 存塘生物资产
    private BigDecimal sumBiologicalAssets; // 总生物资产
    private BigDecimal avgBaitCoefficient; // 平均饵料系数
    private BigDecimal inputCost = new BigDecimal(0); // 投入总成本

    // 悬浮窗数据
    private List<FishNodeDetail> fishNodeDetailList = new ArrayList<>();
    private Integer nodeType = 2; // 节点类型(1:根节点;2:子节点;3:;)
    private Long parentPondId; // 父池子，分池涉及
    private List<ParentNodeItem> parentNodeList; // 父池子，分池涉及
    private Integer level; // 树中level
    private Integer parentNodeIndex = 0; // 父节点 序号
    private Integer nodeIndex = 0; // 当前节点序号

    public FishNode(String originBatchNumber, BigDecimal realTimeStoragePool, String fishSpec) {
        this.originBatchNumber = originBatchNumber;
        this.batchNumber = originBatchNumber.replaceAll("BZ", "")
                .replaceAll("CGRKI", "")
                .replaceAll("CGRK", "");
        this.realTimeStoragePool = realTimeStoragePool;
        this.fishSpec = fishSpec;
    }
    public FishNode build_location(Integer level, Integer nodeIndex) {
        this.level = level;
        this.nodeIndex = nodeIndex;
        return this;
    }
    public FishNode build_fishDensity() { // 设置密度
        if(fishDensity == null || !EasyBigDecimalUtil.ge0(fishDensity)) {
            if(!EasyBigDecimalUtil.gt0(pondScale)) return this;
            BigDecimal weight_g = EasyBigDecimalUtil.multiply(EasyBigDecimalUtil.toNumber(fishSpec), realTimeStoragePool);
            BigDecimal weight_kg = EasyBigDecimalUtil.divide(weight_g, new BigDecimal(1000));
            this.fishDensity = EasyBigDecimalUtil.divide(weight_kg, pondScale);
        }
        return this;
    }
    public FishNode build_sonInfo() { // 设置密度
        FishNodeDetail fishNodeDetail = this.getFishNodeDetailList().get(0);
        if(fishNodeDetail != null) {
            fishNodeDetail.setBiologicalAssets(this.getBiologicalAssets());
            fishNodeDetail.setSumBiologicalAssets(this.getSumBiologicalAssets());
            fishNodeDetail.setFeedCoefficient(this.getAvgBaitCoefficient());
            fishNodeDetail.setInputCost(this.getInputCost());
            fishNodeDetail.setAvgSpecifications(this.getAvgFishSpec());
        }
        return this;
    }
    public FishNode build_parent(Long parentPondId, Integer parentNodeIndex) {
        this.parentPondId = parentPondId;
        this.parentNodeIndex = parentNodeIndex;
        build_parentNode(List.of(new ParentNodeItem(parentPondId, parentNodeIndex)));
        return this;
    }
    public FishNode build_parentNode(List<ParentNodeItem> parentNodeList) {
        this.parentNodeList = parentNodeList;
        if(EasyListUtil.isNotEmpty(parentNodeList) && (this.parentPondId == null || this.parentNodeIndex == null)) {
            ParentNodeItem nodeItem = parentNodeList.get(0);
            this.parentPondId = nodeItem.getParentPondId();
            this.parentNodeIndex = nodeItem.getParentNodeIndex();
        }
        return this;
    }

    public FishNode(String originBatchNumber, Long workshopId, String workshopName, Long pondSystemId, String pondSystemName,
                    String pondNum, Long pondId, String pondName, String pondDetailNum, BigDecimal realTimeStoragePool, String fishSpec) {
        this.originBatchNumber = originBatchNumber;
        this.workshopId = workshopId;
        this.workshopName = workshopName;
        this.pondSystemId = pondSystemId;
        this.pondSystemName = pondSystemName;
        this.pondNum = pondNum;
        this.pondId = pondId;
        this.pondName = pondName;
        this.pondDetailNum = pondDetailNum;
        this.realTimeStoragePool = realTimeStoragePool;
        this.fishSpec = fishSpec;
        this.biologicalAssets = EasyBigDecimalUtil.multiply(this.realTimeStoragePool, EasyBigDecimalUtil.toNumber(this.fishSpec));
        this.biologicalAssets = EasyBigDecimalUtil.divide(this.biologicalAssets, new BigDecimal(1000));
    }

    public FishNode build_fishNodeDetailList(FishNodeDetail fishNodeDetail) {
        fishNodeDetailList.add(fishNodeDetail);
        return this;
    }
    public FishNode build_fishBase(BigDecimal realTimeStoragePool, String fishSpec) {
        this.realTimeStoragePool = EasyBigDecimalUtil.add(realTimeStoragePool, this.realTimeStoragePool);
        this.fishSpec = fishSpec;
        this.biologicalAssets = EasyBigDecimalUtil.multiply(this.realTimeStoragePool, EasyBigDecimalUtil.toNumber(this.fishSpec));
        this.biologicalAssets = EasyBigDecimalUtil.divide(this.biologicalAssets, new BigDecimal(1000));
        return this;
    }
    public FishNode build_pondBase(ZkPondDO pondDO) {
        this.workshopId = pondDO.getWorkshopId();
        this.workshopName = pondDO.getWorkshopName();
        this.pondSystemId = pondDO.getPondSystemId();
        this.pondNum = pondDO.getPondNum();
        this.pondSystemName = pondDO.getPondSystemName();
        this.pondId = pondDO.getId();
        this.pondDetailNum = pondDO.getPondDetailNum();
        this.pondName = pondDO.getPondName();
        this.pondScale = EasyBigDecimalUtil.toNumber(pondDO.getPondScale());
        return this;
    }

    public FishNode build_operationLog(ZkFishOperationLogDO item) {
        if(StringUtil.isNotEmptyWith0(item.getFishSpec())) this.setFishSpec(item.getFishSpec());
        this.setRealTimeStoragePool(new BigDecimal(item.getCurrFishNum()));
        this.fishDensity = item.getFishDensity();
        this.setDeadCount(new BigDecimal(item.getDayFishNum()));
        this.setSurvivalRate(item.getSurvivalRate());
        this.feedCount = item.getCurrFeedCount();
        this.feedCountTrend = "up";
        this.deadCountTrend = "up";
        this.deadCount = new BigDecimal(item.getDayFishNum());
        // 后面新增的 信息
        this.sevenDayMortalityRate = item.getRecentDayMortalityRate();
        this.sevenDayMortalityRatePer = item.getRecentDayMortalityRatePer();
        this.accumulativeDeathToll = item.getAccumulateFishNum();
        this.mortalityRate = EasyBigDecimalUtil.subtract(new BigDecimal(1), item.getSurvivalRate());
        this.accumulatedFeedingAmount = item.getAccumulateFeedCount();
        this.accumulatedWeightGain = item.getAccumulatedWeightGain();
        this.saleFishNum = item.getSaleFishNum();
        this.saleWeight = item.getSaleWeight();
        this.avgFishSpec = EasyBigDecimalUtil.getOrDefault(item.getFishSpec(), item.getAvgFishSpec()); // item.getAvgFishSpec()
        this.biologicalAssets = item.getBiologicalAssets();
        if(!EasyBigDecimalUtil.gt0(item.getBiologicalAssets())) this.biologicalAssets = new BigDecimal(0);
        this.sumBiologicalAssets = item.getSumBiologicalAssets();
        if(!EasyBigDecimalUtil.gt0(item.getSumBiologicalAssets())) this.sumBiologicalAssets = new BigDecimal(0);
        this.avgBaitCoefficient = item.getAvgBaitCoefficient();
        this.inputCost = EasyBigDecimalUtil.add(item.getCurrCost(), item.getBiologyCost());
        if(!EasyBigDecimalUtil.gt0(this.inputCost)) this.inputCost = new BigDecimal(0);

        FishNodeDetail fishNodeDetail = this.getFishNodeDetailList().get(0); // 目前只有一个
        fishNodeDetail.build_accumulate(item.getFishSpec(), item.getAccumulateFeedCount()+"", item.getAccumulateFishNum()+"",
                item.getActuralRadio()+"", item.getAverageInputCost()+"", item.getAverageBiologicalAssets()+"")
                .setSumBiologicalAssets(item.getSumBiologicalAssets());
        return this;
    }

    public FishNode build_rate_feed_dead(BigDecimal survivalRate, BigDecimal feedCount, BigDecimal deadCount) {
        this.survivalRate = survivalRate;
        this.feedCount = feedCount;
        this.deadCount = deadCount;
        return this;
    }
    public void build_upOrDown(String subtractFeedCount, String feedCountTrend, String subtractDeadCount, String deadCountTrend) {
        this.subtractFeedCount = subtractFeedCount;
        this.feedCountTrend = feedCountTrend;
        this.subtractDeadCount = subtractDeadCount;
        this.deadCountTrend = deadCountTrend;
    }
    public FishNode build_sevenDayMortalityRate(Long sevenDayMortalityRate, BigDecimal sevenDayMortalityRatePer) {
        this.sevenDayMortalityRate += sevenDayMortalityRate;
        this.sevenDayMortalityRatePer = EasyBigDecimalUtil.divide(new BigDecimal(this.sevenDayMortalityRate), new BigDecimal(this.originBatchNumberFishNum));
        // this.sevenDayMortalityRate = sevenDayMortalityRate;
        // this.sevenDayMortalityRatePer = sevenDayMortalityRatePer;
        return this;
    }
    public FishNode build_sevenDayMortalityRate3(Long sevenDayMortalityRate, BigDecimal realTimeStoragePool) {
        this.sevenDayMortalityRate += sevenDayMortalityRate;
        this.sevenDayMortalityRatePer = EasyBigDecimalUtil.divide(new BigDecimal(this.sevenDayMortalityRate),
                EasyBigDecimalUtil.add(realTimeStoragePool, new BigDecimal(this.sevenDayMortalityRate)));
        // this.sevenDayMortalityRate = sevenDayMortalityRate;
        // this.sevenDayMortalityRatePer = sevenDayMortalityRatePer;
        return this;
    }
    public FishNode build_inputCost(BigDecimal inputCost) {
        this.inputCost = EasyBigDecimalUtil.add(this.inputCost, inputCost);
        // this.sevenDayMortalityRate = sevenDayMortalityRate;
        // this.sevenDayMortalityRatePer = sevenDayMortalityRatePer;
        return this;
    }
    public FishNode build_accumulativeDeathToll(Long accumulativeDeathToll, BigDecimal mortalityRate) {
        this.accumulativeDeathToll += accumulativeDeathToll;
        this.mortalityRate = EasyBigDecimalUtil.divide(this.mortalityRate, new BigDecimal(this.originBatchNumberFishNum));
        // this.accumulativeDeathToll = accumulativeDeathToll;
        // this.mortalityRate = mortalityRate;
        return this;
    }
    public FishNode build_accumulatedFeedingAmount(BigDecimal accumulatedFeedingAmount, BigDecimal accumulatedWeightGain) {
        this.accumulatedFeedingAmount = EasyBigDecimalUtil.add(this.accumulatedFeedingAmount, accumulatedFeedingAmount);
        this.accumulatedWeightGain = EasyBigDecimalUtil.add(this.accumulatedWeightGain, accumulatedWeightGain);
        // this.accumulatedFeedingAmount = accumulatedFeedingAmount;
        // this.accumulatedWeightGain = accumulatedWeightGain;
        return this;
    }
    public FishNode build_saleFishNum(Long saleFishNum, BigDecimal saleWeight, BigDecimal avgFishSpec) {
        this.saleFishNum += saleFishNum;
        this.saleWeight = EasyBigDecimalUtil.add(this.saleWeight, saleWeight);
        // this.saleFishNum = saleFishNum;
        // this.saleWeight = saleWeight;
        this.avgFishSpec = avgFishSpec;
        return this;
    }
    public FishNode build_biologicalAssets(BigDecimal biologicalAssets, BigDecimal sumBiologicalAssets, BigDecimal avgBaitCoefficient) {
        this.biologicalAssets = EasyBigDecimalUtil.add(this.biologicalAssets, biologicalAssets);
        this.sumBiologicalAssets = EasyBigDecimalUtil.add(this.sumBiologicalAssets, sumBiologicalAssets);
        this.avgBaitCoefficient = EasyBigDecimalUtil.add(this.avgBaitCoefficient, avgBaitCoefficient);
        // this.biologicalAssets = biologicalAssets;
        // this.sumBiologicalAssets = sumBiologicalAssets;
        // this.avgBaitCoefficient = avgBaitCoefficient;
        return this;
    }
    public FishNode build_avgRes(Integer size) {
        this.avgBaitCoefficient = EasyBigDecimalUtil.divide(this.avgBaitCoefficient, new BigDecimal(size));
        // this.biologicalAssets = biologicalAssets;
        // this.sumBiologicalAssets = sumBiologicalAssets;
        // this.avgBaitCoefficient = avgBaitCoefficient;
        return this;
    }
    public FishNode build_clear() {
        this.sevenDayMortalityRate = 0L;
        this.accumulativeDeathToll = 0L;
        this.accumulatedFeedingAmount = new BigDecimal(0);
        this.saleFishNum = 0L;
        this.saleWeight = new BigDecimal(0);
        this.biologicalAssets = new BigDecimal(0);
        this.sumBiologicalAssets = new BigDecimal(0);
        this.avgBaitCoefficient = new BigDecimal(0);
        this.realTimeStoragePool = new BigDecimal(0);
        this.inputCost = new BigDecimal(0);
        this.accumulatedWeightGain = new BigDecimal(0);
        return this;
    }

    public FishNode build_Purchase(ErpPurchaseInItemDO item) {
        this.originBatchNumberFishNum = EasyBigDecimalUtil.toLong(item.getCount()+"");
        this.setBatchNumberInfo(item.selectProductInfo());
        return this;
    }
    public FishNode build_realTimeStoragePool(BigDecimal realTimeStoragePool) {
        this.realTimeStoragePool = EasyBigDecimalUtil.add(this.realTimeStoragePool, realTimeStoragePool);
        return this;
    }
}
