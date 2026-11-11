package cn.iocoder.yudao.module.fish.controller.admin.zkgrowdailyrecord.to;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitingredients.ZkGrowBaitIngredientsDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowDailyRecordDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfarmingbatch.ZkGrowFarmingBatchDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfishspecies.ZkGrowFishSpeciesDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowmodelconfig.ZkGrowModelConfigDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrownutritionalrequirements.ZkGrowNutritionalRequirementsDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkgrowbaitingredients.ZkGrowBaitIngredientsMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkgrowdailyrecord.ZkGrowDailyRecordMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkgrowfarmingbatch.ZkGrowFarmingBatchMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkgrowfishspecies.ZkGrowFishSpeciesMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkgrowmodelconfig.ZkGrowModelConfigMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkgrownutritionalrequirements.ZkGrowNutritionalRequirementsMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class InitData {

    @Resource
    private ZkGrowFishSpeciesMapper zkGrowFishSpeciesMapper;
    @Resource
    private ZkGrowModelConfigMapper zkGrowModelConfigMapper;
    @Resource
    private ZkGrowFarmingBatchMapper zkGrowFarmingBatchMapper;
    @Resource
    private ZkGrowDailyRecordMapper zkGrowDailyRecordMapper;
    @Resource
    private ZkGrowNutritionalRequirementsMapper zkGrowNutritionalRequirementsMapper;
    @Resource
    private ZkGrowBaitIngredientsMapper zkGrowBaitIngredientsMapper;


    public void initData() {
        // 1. 鱼类品种
        ZkGrowFishSpeciesDO species = ZkGrowFishSpeciesDO.builder()
                .name("鲈鱼")
                .commonName("海鲈")
                .scientificName("Lateolabrax japonicus")
                .description("工厂化循环水养殖的主要品种之一，生长快，耐高密度。")
                .build();
        zkGrowFishSpeciesMapper.insert(species);

        // 2. 生长模型配置
        ZkGrowModelConfigDO config = ZkGrowModelConfigDO.builder()
                .speciesId(species.getId())
                .modelName("Von Bertalanffy")
                .lInf(new BigDecimal("45"))
                .wInf(new BigDecimal("1200"))
                .kLength(new BigDecimal("0.012"))
                .kWeight(new BigDecimal("0.015"))
                .initialWeight(new BigDecimal("5"))
                .initialLength(new BigDecimal("4"))
                .optimalTempMin(new BigDecimal("23"))
                .optimalTempMax(new BigDecimal("28"))
                .optimalFeedRate(new BigDecimal("3"))
                .build();
        zkGrowModelConfigMapper.insert(config);

        // 3. 养殖批次
        ZkGrowFarmingBatchDO batch = ZkGrowFarmingBatchDO.builder()
                .batchNo("BATCH-2026-001")
                .speciesId(species.getId())
                .startDate(LocalDateTime.now().minusDays(90)) // 已经养了90天
                .initialCount(5000)
                .currentCount(4850)
                .status(1)
                .build();
        zkGrowFarmingBatchMapper.insert(batch);

        // 4. 每日生长记录 (插入一条第90天的记录作为当前数据)
        ZkGrowDailyRecordDO record = ZkGrowDailyRecordDO.builder()
                .batchId(batch.getId())
                .dayIndex(90)
                .recordDate(LocalDateTime.now())
                .waterTemp(new BigDecimal("25"))
                .feedRate(new BigDecimal("3"))
                .predictedWeight(new BigDecimal("980.6"))
                .predictedLength(new BigDecimal("34.4"))
                .sgr(new BigDecimal("0.45"))
                .build();
        zkGrowDailyRecordMapper.insert(record);

        // 5. 营养需求
        zkGrowNutritionalRequirementsMapper.insertBatch(Arrays.asList(
                ZkGrowNutritionalRequirementsDO.builder().growthStage("鱼苗期").dayold("0-30天").weightRange("< 10g").crudeProtein("48-52%").crudeFat("12-15%").carbohydrate("15-20%").crudeFiber("2-3%").calcium("1.5-2.0%").phosphorus("1.0-1.2%").build(),
                ZkGrowNutritionalRequirementsDO.builder().growthStage("幼鱼期").dayold("31-90天").weightRange("10-100g").crudeProtein("45-48%").crudeFat("10-12%").carbohydrate("18-22%").crudeFiber("3-4%").calcium("1.2-1.5%").phosphorus("0.9-1.1%").build(),
                ZkGrowNutritionalRequirementsDO.builder().growthStage("成鱼前期").dayold("91-150天").weightRange("100-300g").crudeProtein("42-45%").crudeFat("8-10%").carbohydrate("20-25%").crudeFiber("3-5%").calcium("1.0-1.2%").phosphorus("0.8-1.0%").build(),
                ZkGrowNutritionalRequirementsDO.builder().growthStage("成鱼后期").dayold("151-180天").weightRange("> 300g").crudeProtein("40-42%").crudeFat("6-8%").carbohydrate("22-28%").crudeFiber("4-6%").calcium("0.8-1.0%").phosphorus("0.7-0.9%").build()
        ));

        // 6. 饵料成分
        zkGrowBaitIngredientsMapper.insertBatch(Arrays.asList(
                ZkGrowBaitIngredientsDO.builder().baitName("高档配合饲料").feedType("商品饲料").crudeProtein("48-50%").crudeFat("10-12%").carbohydrate("18-20%").crudeFiber("2-3%").moisture("8-10%").ashContent("10-12%").referencePrice("12-15元/kg").build(),
                ZkGrowBaitIngredientsDO.builder().baitName("中档配合饲料").feedType("商品饲料").crudeProtein("43-46%").crudeFat("8-10%").carbohydrate("20-25%").crudeFiber("3-4%").moisture("10-12%").ashContent("12-14%").referencePrice("8-10元/kg").build(),
                ZkGrowBaitIngredientsDO.builder().baitName("普通配合饲料").feedType("商品饲料").crudeProtein("38-42%").crudeFat("6-8%").carbohydrate("25-30%").crudeFiber("4-6%").moisture("10-12%").ashContent("13-15%").referencePrice("6-8元/kg").build(),
                ZkGrowBaitIngredientsDO.builder().baitName("冰鲜小杂鱼").feedType("鲜活饵料").crudeProtein("15-18%").crudeFat("3-5%").carbohydrate("0-1%").crudeFiber("0%").moisture("75-80%").ashContent("3-4%").referencePrice("4-6元/kg").build(),
                ZkGrowBaitIngredientsDO.builder().baitName("冰鲜虾类").feedType("鲜活饵料").crudeProtein("16-20%").crudeFat("1-2%").carbohydrate("0-1%").crudeFiber("0%").moisture("75-78%").ashContent("2-3%").referencePrice("15-20元/kg").build(),
                ZkGrowBaitIngredientsDO.builder().baitName("鲜活小鱼").feedType("鲜活饵料").crudeProtein("16-19%").crudeFat("4-6%").carbohydrate("0-1%").crudeFiber("0%").moisture("75-78%").ashContent("3-4%").referencePrice("8-12元/kg").build(),
                ZkGrowBaitIngredientsDO.builder().baitName("鱼粉（辅料）").feedType("原料").crudeProtein("60-65%").crudeFat("8-10%").carbohydrate("0-2%").crudeFiber("0-1%").moisture("8-10%").ashContent("15-18%").referencePrice("10-14元/kg").build(),
                ZkGrowBaitIngredientsDO.builder().baitName("豆粕（辅料）").feedType("原料").crudeProtein("43-46%").crudeFat("1-2%").carbohydrate("30-35%").crudeFiber("5-7%").moisture("10-12%").ashContent("6-7%").referencePrice("3-5元/kg").build()
        ));

        System.out.println("初始数据初始化完成！");
    }

}
