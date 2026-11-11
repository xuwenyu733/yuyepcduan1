package cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import org.apache.commons.lang3.StringUtils;

/**
 * 鱼苗统计 DO
 *
 * @author 系统管理员
 */
@TableName("zk_fish_operation_log")
@KeySequence("zk_fish_operation_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkFishOperationLogDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 鱼苗批次
     */
    private String batchNumber;
    /**
     * 所属车间id
     */
    private Long workshopId;
    /**
     * 所属系统id
     */
    private Long pondSystemId;
    /**
     * 存塘系统编号
     */
    private String pondNum;
    /**
     * 所属存塘id
     */
    private Long pondId;
    private String pondDetailNum; // 存塘编号(区别于pond_num系统编号)
    /**
     * 车间名称
     */
    private String workshopName;
    /**
     * 存塘系统名称
     */
    private String pondSystemName;
    /**
     * 存塘名称
     */
    private String pondName;
    /**
     * 统计时间(yyyy-MM-dd)
     */
    private String logDay;
    /**
     * 鱼苗编码(pl_product.product_no)
     */
    private String productNo;
    /**
     * 鱼苗种类
     */
    private String fishTypes;
    /**
     * 鱼苗规格
     */
    private String fishSpec;
    /**
     * 操作数量
     */
    private Long dayFishNum = 0L;
    /**
     * 累计操作数量
     */
    private Long accumulateFishNum = 0L;
    /**
     * 投放数量
     */
    private Long pushFishNum = 0L;
    private Long currFishNum = 0L; // 当前存量
    /**
     * 当日操作率
     */
    private BigDecimal dayRate;
    /**
     * 累计操作率
     */
    private BigDecimal accumulateRate;
    /**
     * 成活率
     */
    private BigDecimal survivalRate;
    /**
     * 修正后操作数量
     */
    private Long newDayFishNum = 0L;
    /**
     * 修正后累计操作数量
     */
    private Long newAccumulateFishNum = 0L;
    /**
     * 修正后投放数量
     */
    private Long newPushFishNum = 0L;
    private Long newCurrFishNum = 0L; // 修正后当前存量
    /**
     * 修正后当日操作率
     */
    private BigDecimal newDayRate;
    /**
     * 修正后累计操作率
     */
    private BigDecimal newAccumulateRate;
    /**
     * 修正后成活率
     */
    private BigDecimal newSurvivalRate;
    /**
     * 修正依据
     */
    private String correctionBasis;
    /**
     * 差值率
     */
    private BigDecimal differencesRate;
    /**
     * 差异原因
     */
    private String reasonsForDifferences;
    /**
     * 具体描述
     */
    private String detailedDescription;
    /**
     * 危险评级(1:正常;2:持续关注;3:立刻介入;)
     */
    private String dangerRating = "1";
    /**
     * 统计类型(-2.1:批次;-2.2:车间;2.3:系统;2.4:存塘;)
     */
    private String logType;
    /**
     * 评级人id
     */
    private Long userId;
    /**
     * 评级人
     */
    private String userName;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;


    private BigDecimal currFishWeight; // 当前存量重量(Kg)
    private BigDecimal accumulateFishWeight; // 累计存量重量(Kg)
    private BigDecimal pondScale; // 存塘规模
    private BigDecimal fishDensity; // 密度(Kg/平方米)

    private BigDecimal currFeedCount; // 当日投喂量(Kg)
    private BigDecimal accumulateFeedCount; // 累计投喂量(Kg)
    private BigDecimal acturalRadio; // 实际饵料系数
    private BigDecimal currCost; // 当前总成本(元)
    private BigDecimal biologyCost; // 生物总资产(元)
    private BigDecimal averageInputCost; // 当前平均成本(元/尾)
    private BigDecimal averageBiologicalAssets; // 平均生物资产(元/尾)

    private Long recentDayMortalityRate = 0L; // 投放几日死亡量
    private BigDecimal recentDayMortalityRatePer; // 投放几日死亡率
    private BigDecimal accumulatedWeightGain; // 累计增重
    private Long saleFishNum = 0L; // 销售数量
    private BigDecimal saleWeight; // 销售重量
    private BigDecimal avgFishSpec; // 平均规格，目前和fishSpec一个值
    private BigDecimal biologicalAssets; // 存塘生物资产
    private BigDecimal sumBiologicalAssets; // 总生物资产
    private BigDecimal avgBaitCoefficient; // 平均饵料系数

    @TableField(exist = false)
    private Long pondFishDetailId; // zk_pond_fish_detail 操作明细id

    @TableField(exist = false)
    private List<String> batchNumberList; // 批次编号
    private BigDecimal fishPrice = new BigDecimal(0); // 鱼苗价格

    public ZkFishOperationLogDO(String batchNumber, String logDay) {
        this.batchNumber = batchNumber;
        this.logDay = logDay;
    }

    public ZkFishOperationLogDO build_currFishNum() {
        this.currFishNum = this.pushFishNum - this.accumulateFishNum;
        return this;
    }

    public ZkFishOperationLogDO(Long workshopId, Long pondSystemId, String pondNum, Long pondId, String workshopName, String pondSystemName, String pondName, String logDay, String logType) {
        this.workshopId = workshopId;
        this.pondSystemId = pondSystemId;
        this.pondNum = pondNum;
        this.pondId = pondId;
        this.workshopName = workshopName;
        this.pondSystemName = pondSystemName;
        this.pondName = pondName;
        this.logDay = logDay;
        this.logType = logType;
    }



    public ZkFishOperationLogDO add_dayFishNum(Long dayFishNum) {
        this.dayFishNum += dayFishNum;
        this.accumulateFishNum += dayFishNum;
        return this;
    }

    public ZkFishOperationLogDO(String productNo, String fishTypes, String fishSpec, String logType) {
        this.productNo = productNo;
        this.fishTypes = fishTypes;
        this.fishSpec = fishSpec;
        this.logType = logType;
    }
    public ZkFishOperationLogDO build_info(String productNo, String fishTypes, String fishSpec, String logType) {
        this.productNo = productNo;
        this.fishTypes = fishTypes;
        this.fishSpec = fishSpec;
        this.logType = logType;
        return this;
    }

    public ZkFishOperationLogDO build_pondDetailNum(String pondDetailNum) {
        this.pondDetailNum = pondDetailNum;
        return this;
    }

    public ZkFishOperationLogDO build_rate() {
        BigDecimal sum1 = EasyBigDecimalUtil.add(new BigDecimal(dayFishNum), new BigDecimal(currFishNum));
        BigDecimal sum2 = EasyBigDecimalUtil.add(new BigDecimal(accumulateFishNum), new BigDecimal(currFishNum));
        this.dayRate = EasyBigDecimalUtil.divide(new BigDecimal(dayFishNum), sum1);
        this.accumulateRate = EasyBigDecimalUtil.divide(new BigDecimal(accumulateFishNum), sum2);
        // this.survivalRate = EasyBigDecimalUtil.divide(new BigDecimal(currFishNum), new BigDecimal(pushFishNum));
        this.survivalRate = EasyBigDecimalUtil.subtract(new BigDecimal(1), this.accumulateRate);
        return this;
    }
    public ZkFishOperationLogDO clear_fishSpec() {
        if(StringUtils.isNotBlank(this.getFishSpec())) {
            this.fishSpec = this.getFishSpec().replace("g/尾", "");
        }
        return this;
    }
    public ZkFishOperationLogDO build_fishDensity(String pondScale, Long currFishNum, BigDecimal fishSpec) {
        if(pondScale == null) pondScale = "20";
        BigDecimal pondScaleReal = new BigDecimal(pondScale);
        BigDecimal currFishWeight = EasyBigDecimalUtil.multiply(new BigDecimal(currFishNum), fishSpec);
        currFishWeight = EasyBigDecimalUtil.divide(currFishWeight, new BigDecimal(1000));
        BigDecimal accumulateFishWeight = currFishWeight;
        BigDecimal fishDensity = EasyBigDecimalUtil.divide(accumulateFishWeight, pondScaleReal);

        this.currFishNum = currFishNum;
        this.currFishWeight = currFishWeight;
        this.accumulateFishWeight = accumulateFishWeight;
        this.pondScale = pondScaleReal;
        this.fishDensity = fishDensity;
        BigDecimal sumFishNum = EasyBigDecimalUtil.add(new BigDecimal(currFishNum), new BigDecimal(this.getAccumulateFishNum()));
        this.survivalRate = EasyBigDecimalUtil.divide(new BigDecimal(currFishNum), sumFishNum);

        return this;
    }

    public ZkFishOperationLogDO build_base(String fishSpec, String fishTypes, BigDecimal fishDensity, BigDecimal dayRate, Long currFishNum,
                                           BigDecimal accumulateRate, BigDecimal accumulateFishWeight, Long accumulateFishNum) {
        this.fishSpec = fishSpec;
        this.fishTypes = fishTypes;
        this.fishDensity = fishDensity;
        this.dayRate = dayRate;
        this.currFishNum = currFishNum;
        this.currFishWeight = EasyBigDecimalUtil.multiply(new BigDecimal(fishSpec), new BigDecimal(currFishNum));
        this.accumulateFishWeight = EasyBigDecimalUtil.multiply(new BigDecimal(fishSpec), new BigDecimal(currFishNum));
        if(EasyBigDecimalUtil.gt0(accumulateFishWeight)) this.accumulateFishWeight = accumulateFishWeight;
        this.accumulateRate = accumulateRate;
        if(accumulateFishNum != null) this.accumulateFishNum = accumulateFishNum;

        return this;
    }
    public ZkFishOperationLogDO build_other(BigDecimal currFeedCount, BigDecimal accumulateFeedCount, BigDecimal acturalRadio, BigDecimal currCost, BigDecimal biologyCost) {
        this.currFeedCount = currFeedCount;
        this.accumulateFeedCount = accumulateFeedCount;
        this.acturalRadio = acturalRadio;
        this.currCost = currCost;
        this.biologyCost = biologyCost;
        BigDecimal averageInputCost = EasyBigDecimalUtil.divide(currCost, new BigDecimal(this.getCurrFishNum()));
        BigDecimal averageBiologicalAssets = EasyBigDecimalUtil.divide(biologyCost, new BigDecimal(this.getCurrFishNum()));
        this.averageInputCost = averageInputCost;
        this.averageBiologicalAssets = averageBiologicalAssets;
        return this;
    }
    public ZkFishOperationLogDO build_other3(BigDecimal accumulateFeedCount, BigDecimal acturalRadio, BigDecimal biologyCost) {
        // this.currFeedCount = currFeedCount;
        this.accumulateFeedCount = accumulateFeedCount;
        this.acturalRadio = acturalRadio;
        // this.currCost = currCost;
        this.biologyCost = biologyCost;
        BigDecimal averageInputCost = EasyBigDecimalUtil.divide(currCost, new BigDecimal(this.getCurrFishNum()));
        BigDecimal averageBiologicalAssets = EasyBigDecimalUtil.divide(biologyCost, new BigDecimal(this.getCurrFishNum()));
        this.averageInputCost = averageInputCost;
        this.averageBiologicalAssets = averageBiologicalAssets;
        return this;
    }
    public ZkFishOperationLogDO build_currFeedCount(BigDecimal currFeedCount, BigDecimal currCost) {
        this.currFeedCount = EasyBigDecimalUtil.add(this.currFeedCount, currFeedCount);
        this.currCost = EasyBigDecimalUtil.add(this.currCost, currCost);
        return this;
    }

    public ZkFishOperationLogDO build_pondFish(ZkPondFishDO fish) {
        Long recentDayMortalityRate = fish.getRecentDayMortalityRate();
        BigDecimal recentDayMortalityRatePer = fish.getRecentDayMortalityRatePer();
        if(recentDayMortalityRate != null && recentDayMortalityRate <= 0 &&
                fish.getSplitDayMortalityRate() != null && fish.getSplitDayMortalityRate()>0) {
            recentDayMortalityRate = fish.getSplitDayMortalityRate();
            recentDayMortalityRatePer = fish.getSplitDayMortalityRatePer();
        }

        this.recentDayMortalityRate = recentDayMortalityRate;
        this.recentDayMortalityRatePer = recentDayMortalityRatePer;
        this.accumulatedWeightGain = fish.getAccumulatedWeightGain();
        this.saleFishNum = fish.getSaleFishNum();
        this.saleWeight = fish.getSaleWeight();
        this.avgFishSpec = fish.getAvgFishSpec();
        this.biologicalAssets = fish.getBiologicalAssets();
        if(EasyBigDecimalUtil.compare0(this.biologicalAssets, "EQ")) {
            this.biologicalAssets = EasyBigDecimalUtil.multiply(fish.getCurrFishSpec(), new BigDecimal(fish.getFishNum()));
            this.biologicalAssets = EasyBigDecimalUtil.divide(this.biologicalAssets, new BigDecimal(1000));
        }
        this.sumBiologicalAssets = fish.getSumBiologicalAssets();
        if(EasyBigDecimalUtil.compare0(this.biologicalAssets, "GT")) {
            this.sumBiologicalAssets = EasyBigDecimalUtil.add(this.biologicalAssets, this.saleWeight);
        }
        this.avgBaitCoefficient = fish.getAvgBaitCoefficient();
        if(EasyBigDecimalUtil.gt0(this.getSumBiologicalAssets())) return this;
        this.sumBiologicalAssets = EasyBigDecimalUtil.multiply(fish.getFishSpec(), new BigDecimal(fish.getFishNum()));
        this.sumBiologicalAssets = EasyBigDecimalUtil.divide(this.sumBiologicalAssets, new BigDecimal(1000));
        return this;
    }


    public ZkFishOperationLogDO build_map(Map<String, Object> map) {
        String fish_cost_str = map.getOrDefault("fish_cost", "0").toString();
        String fish_price_str = map.getOrDefault("fish_price", "0").toString();
        this.fishPrice = new BigDecimal(fish_price_str);
        this.biologyCost = new BigDecimal(fish_cost_str);

        return this;
    }
}
