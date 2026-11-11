package cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch.po.TransferPo;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.PondFishDetailAddTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.service.zkpondfishdetail.ZkPondFishDetailService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 多层不允许 重复 存塘，则 分池的存塘需要 在 存塘那里作为 分池转入 记录
 */
@Component
@Slf4j
public class SplitForIn {

    @Resource
    private ZkPondFishDetailService zkPondFishDetailService;

    public void splitForInExe(List<FishTree> fishTreeList) {
        if(EasyListUtil.isEmpty(fishTreeList)) return;
        // 1. 一棵树 一棵树的来进行 获取 分池转入信息
        for(FishTree fishTree : fishTreeList) {
            // 1.1. 获取 分池转入信息
            Set<Long> pondIdSet = fishTree.getPondIdSet();
            if(CollectionUtils.isAnyEmpty(pondIdSet)) continue;
            Map<Long, List<ZkPondFishDetailDO>> map = zkPondFishDetailService.selectMapByTypeAndTargetPondId(PondFishDetailAddTypeEnum.SPLIT.getAddType(), pondIdSet);
            Set<Long> mapPondSet = map.keySet();
            if(CollectionUtils.isAnyEmpty(mapPondSet)) continue;
            // 1.2. 每个节点遍历， 看是否有 分池的转入信息
            Map<String, List<FishNode>> treeMap = fishTree.getTreeMap();
            Set<String> levelSet = treeMap.keySet();
            for(String level : levelSet) {
                Integer levelInt = Integer.parseInt(level);
                List<FishNode> fishNodes = treeMap.get(level);
                if(EasyListUtil.isEmpty(fishNodes)) continue;
                for(int i=0;i<fishNodes.size();i++) {
                    FishNode currNode = fishNodes.get(i);
                    // 1.3. 分池信息转 fishNodeDetailList
                    List<TransferPo> transferPoList = splitList2TransferList(map.get(currNode.getPondId()));
                    // 1.4 将列表加入 转入明细
                    currNode.getFishNodeDetailList().get(0).build_transferPoList(transferPoList);
                }
            }
        }

    }

    /**
     * 将 分池列表 转成 转入列表
     * @param zkPondFishDetailDOS
     * @return
     */
    private List<TransferPo> splitList2TransferList(List<ZkPondFishDetailDO> zkPondFishDetailDOS) {
        if(EasyListUtil.isEmpty(zkPondFishDetailDOS)) return new ArrayList<>();
        return zkPondFishDetailDOS.stream().map(obj -> {
            BigDecimal transferSevenDayMortalityRatePer = EasyBigDecimalUtil.divide(new BigDecimal(obj.getSplitAfterDead()), new BigDecimal(obj.getFishNum()));
            return new TransferPo(EasyDateUtil.format(obj.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                    obj.getFishNum(), obj.getTargetWeight(), EasyBigDecimalUtil.toNumber(obj.getTargetFishSpec()), obj.getSplitRadio(), obj.getSplitAfterDead(), transferSevenDayMortalityRatePer,
                    PondFishDetailAddTypeEnum.SPLIT.getAddType(), obj.getPondId())
                    .setSplitPondDetailNum(obj.getPondDetailNum());
        }).toList();
    }

}
