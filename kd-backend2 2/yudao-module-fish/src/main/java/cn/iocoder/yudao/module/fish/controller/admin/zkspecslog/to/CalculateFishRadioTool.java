package cn.iocoder.yudao.module.fish.controller.admin.zkspecslog.to;

import cn.iocoder.yudao.framework.common.enums.StatisticsTypeEnum;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishproofing.ZkFishProofingDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkspecslog.ZkSpecsLogDO;
import cn.iocoder.yudao.module.fish.service.zkfishproofing.ZkFishProofingService;
import cn.iocoder.yudao.module.fish.service.zkpondfish.ZkPondFishService;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @创建人 lr
 * @创建时间 2025-09-23
 * @描述
 */
@Component
public class CalculateFishRadioTool {

    public static BigDecimal predictRadio = new BigDecimal(1.1);
    @Resource
    private ZkFishProofingService zkFishProofingService;
    @Resource
    private ZkPondFishService zkPondFishService;

    /**
     * 根据 鱼塘 最新打样记录计算 预估规格和实际饵料系数
     */
    public List<ZkSpecsLogDO> calculateRadio(List<ZkSpecsLogDO> list) {
        if(EasyListUtil.isEmpty(list)) return null;
        Set<Long> pondIdSet = list.stream().map(ZkSpecsLogDO::getPondId).collect(Collectors.toSet());
        List<ZkPondFishDO> zkPondFishDOS = zkPondFishService.selectByPondIdSet(pondIdSet);
        // 1. 获取 鱼苗打样 记录
        List<ZkFishProofingDO> proofingList = zkFishProofingService.getCustomList(StatisticsTypeEnum.NOT_HANDLED.getValue());
        if(EasyListUtil.isNotEmpty(proofingList)) {
            return sync_acturalSpecs(proofingList, list, zkPondFishDOS);
        }
        // 2. 执行数据同步
        return calculateLog(list, zkPondFishDOS);
    }

    public List<ZkSpecsLogDO> sync_acturalSpecs(List<ZkFishProofingDO> proofingList, List<ZkSpecsLogDO> logDOS, List<ZkPondFishDO> zkPondFishDOS) {
        // 1. 同步 鱼苗实际打样信息
        for(ZkSpecsLogDO logItem : logDOS) {
            ZkFishProofingDO currProofing = null;
            for(ZkFishProofingDO proofingItem : proofingList) {
                if(Objects.equals(proofingItem.getPondId(), logItem.getPondId())) {
                    currProofing = proofingItem;
                    break;
                }
            }
            if(currProofing == null) continue;
            logItem.setActuralSpecs(currProofing.getAvgSpecs()).setTempActuralSpecs(currProofing.getAvgSpecs());
        }
        // 2. 更新当前打样信息已经统计
        proofingList.forEach(obj -> obj.setStatisticsType(StatisticsTypeEnum.HAD_HANDLED.getValue()));
        zkFishProofingService.customUpdateBatch(proofingList);
        // 3. 统计 饵料系数 信息
        return calculateLog(logDOS, zkPondFishDOS);
    }

    /**
     * 根据 预估饵料系数计算实际鱼重量
     * @param list
     * @return
     */
    public List<ZkSpecsLogDO> calculateLog(List<ZkSpecsLogDO> list, List<ZkPondFishDO> zkPondFishDOS) {
        if(EasyListUtil.isEmpty(list)) return null;
        // List<ZkSpecsLogDO> res = new ArrayList<>();
        // 1. 开始计算 预估规格
        for(ZkSpecsLogDO item : list) {
            if(!EasyBigDecimalUtil.gt0(item.getFeedCount())) continue;
            // 投喂总量/预估饵料系数
            BigDecimal temp1 = EasyBigDecimalUtil.divide(item.getFeedCount(), predictRadio);
            BigDecimal temp1_1 = EasyBigDecimalUtil.multiply(temp1, new BigDecimal(1000)); // Kg转换成g
            // 初始规格*初始数量
            BigDecimal temp2 = EasyBigDecimalUtil.multiply(new BigDecimal(item.getCurrNum()), item.getInitSpecs());
            // 总增长
            BigDecimal temp3 = EasyBigDecimalUtil.add(temp1_1, temp2);
            // 预估规格
            if(!EasyBigDecimalUtil.gt0(new BigDecimal(item.getCurrNum()))) {
                ZkPondFishDO byPondId = getByPondId(zkPondFishDOS, item.getPondId());
                if(byPondId == null) continue;
                item.build_predictInfo(predictRadio, byPondId.getCurrFishSpec()).setPredictAccumulatedWeightGain(temp1).setAvgBaitCoefficient(predictRadio)
                        .setActuralRadio(predictRadio).setActuralAccumulatedWeightGain(temp1);
                continue;
            }
            BigDecimal predictSpecs = EasyBigDecimalUtil.divide(temp3, new BigDecimal(item.getCurrNum()));
            // 计算总生物性资产
            updateCapital(zkPondFishDOS, item, predictSpecs);
            item.build_predictInfo(predictRadio, predictSpecs).setPredictAccumulatedWeightGain(temp1).setAvgBaitCoefficient(predictRadio)
                    .setActuralRadio(predictRadio).setActuralAccumulatedWeightGain(temp1);
        }
        // 2. 根据实际的来计算 实际 饵料系数
        for(ZkSpecsLogDO item : list) {
            if(item.getActuralSpecs() == null || !EasyBigDecimalUtil.gt0(item.getActuralSpecs()) || item.getTempActuralSpecs() == null
                    || !EasyBigDecimalUtil.gt0(item.getTempActuralSpecs())
                    || !EasyBigDecimalUtil.gt0(item.getFeedCount())) continue;
            // 打样规格*存塘数量
            BigDecimal temp1 = EasyBigDecimalUtil.multiply(new BigDecimal(item.getCurrNum()), item.getActuralSpecs());
            // 初始重量 = 初始规格 * 初始数量 目前死已经算里面
            BigDecimal temp2 = EasyBigDecimalUtil.multiply(new BigDecimal(item.getCurrNum()), item.getInitSpecs());
            // 打样规格*存塘数量 - 初始重量
            BigDecimal temp3 = EasyBigDecimalUtil.subtract(temp1, temp2);
            BigDecimal temp3_1 = EasyBigDecimalUtil.divide(temp3, new BigDecimal(1000)); // g => Kg
            if(!EasyBigDecimalUtil.gt0(temp3)) continue;
            // 实际饵料系数 = 投喂重量/（打样规格×当前存塘数量-初始重量）
            BigDecimal temp4 = EasyBigDecimalUtil.multiply(new BigDecimal(1000), item.getFeedCount()); // kg =》 g 1000
            BigDecimal temp5 = EasyBigDecimalUtil.divide(temp4, temp3);
            item.setActuralRadio(temp5).setActuralAccumulatedWeightGain(temp3_1).setAvgBaitCoefficient(temp5);
            // 计算总生物性资产
            updateCapital(zkPondFishDOS, item, item.getActuralSpecs());
            // res.add(item);
        }
        return list;
    }

    private void updateCapital(List<ZkPondFishDO> zkPondFishDOS, ZkSpecsLogDO item, BigDecimal predictSpecs) {
        ZkPondFishDO currPondFish = getByPondId(zkPondFishDOS, item.getPondId());
        if(currPondFish != null) {
            // 存塘生物资产
            BigDecimal currCapital = EasyBigDecimalUtil.multiply(predictSpecs, new BigDecimal(currPondFish.getFishNum()));
            currCapital = EasyBigDecimalUtil.divide(currCapital, new BigDecimal(1000));

            // 总生物资产= 存塘生物资产+卖出的
            BigDecimal sumCapital = EasyBigDecimalUtil.multiply(predictSpecs, new BigDecimal(currPondFish.getPushFishNum()+currPondFish.getSplitAddNum()
                    - currPondFish.getSplitReduceNum()
                    -currPondFish.getDeadFishNum()));
            sumCapital = EasyBigDecimalUtil.divide(sumCapital, new BigDecimal(1000));
            item.setBiologicalAssets(currCapital).setSumBiologicalAssets(sumCapital);
            System.out.println(String.format("pondId:%s==>pondFish:%s==>predictSpecs:%s", currPondFish.getPondId(),
                    JSON.toJSONString(currPondFish), predictSpecs));
        }
    }

    public ZkPondFishDO getByPondId(List<ZkPondFishDO> pondFishDOS, Long pondId) {
        if(EasyListUtil.isEmpty(pondFishDOS)) return null;
        for(ZkPondFishDO item : pondFishDOS) {
            if(Objects.equals(pondId, item.getPondId())) return item;
        }
        return null;
    }

}
