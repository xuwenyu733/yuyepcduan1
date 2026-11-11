package cn.iocoder.yudao.module.fish.utils;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologicalasset.ZkBiologicalAssetDO;

import java.math.BigDecimal;
import java.util.Objects;

public class refreshBioAssetUtile {
    public static ZkBiologicalAssetDO refreshBioAsset( ZkBiologicalAssetDO zkBiologicalAssetDO ) {
        // 检查必要属性是否为空
        if (zkBiologicalAssetDO != null 
                && zkBiologicalAssetDO.getTotalWeight() != null 
                && zkBiologicalAssetDO.getFishPrice() != null) {
            // 计算生物资产: 总重量 * 鱼价格 = 生物资产
            BigDecimal biologicalAsset = zkBiologicalAssetDO.getTotalWeight().multiply(zkBiologicalAssetDO.getFishPrice());
            // 更新生物资产值
            zkBiologicalAssetDO.setBiologicalAsset(biologicalAsset);
        }
        return zkBiologicalAssetDO;
    }
}