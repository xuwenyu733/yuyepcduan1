package cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch.po.OutPo;
import cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch.po.SalePo;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.PondFishDetailAddTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishproofing.ZkFishProofingDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.service.zkfishproofing.ZkFishProofingService;
import cn.iocoder.yudao.module.fish.service.zkpondfishdetail.ZkPondFishDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FishInfoTool {
    @Autowired
    @Lazy
    private ZkFishProofingService zkFishProofingService;
    @Autowired
    @Lazy
    private ZkPondFishDetailService zkPondFishDetailService;

    /**
     * 给tree1级节点添加detail信息
     * @param fishNode
     * @param collect
     */
    public void enhancerRoot(FishNode fishNode, Map<String, List<ZkPondFishDetailDO>> collect) {
        // 1. 获取 投放信息
        String originBatchNumber = fishNode.getOriginBatchNumber();
        List<ZkPondFishDetailDO> detailDOS = collect.get(originBatchNumber);
        if(EasyListUtil.isEmpty(detailDOS)) return;
        List<FishNodeDetail> fishNodeDetailList = fishNode.getFishNodeDetailList();
        FishNodeDetail first = fishNodeDetailList.get(0); // 目前只有一个
        // 2. 执行信息 填充
        List<OutPo> outPoList = getOutPos(detailDOS);
        first.setOutPoList(outPoList);
    }

    /**
     * 从分池或者投放记录 转换成 转出信息
     * @param detailDOS
     * @return
     */
    private static List<OutPo> getOutPos(List<ZkPondFishDetailDO> detailDOS) {
        List<OutPo> outPoList = new ArrayList<>();
        for(ZkPondFishDetailDO item : detailDOS) {
            OutPo out = new OutPo(EasyDateUtil.format(item.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                    item.getFishNum(), item.getPondWeight(), EasyBigDecimalUtil.toNumber(item.getTargetFishSpec()), item.getSplitRadio());
            out.setPondDetailNum(item.getTargetPondDetailNum());
            outPoList.add(out);
        }
        return outPoList;
    }
    private static List<SalePo> getSalePos(List<ZkPondFishDetailDO> detailDOS) {
        List<SalePo> salePoList = new ArrayList<>();
        for(ZkPondFishDetailDO item : detailDOS) {
            SalePo out = new SalePo(EasyDateUtil.format(item.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                    item.getFishNum(), item.getPondWeight(), EasyBigDecimalUtil.toNumber(item.getFishSpec()), item.getPurchaser());
            salePoList.add(out);
        }
        return salePoList;
    }

    /**
     * 给tree2级节点添加detail信息
     * @param fishNodes 父级节点
     * @param detailDOS 分池列表
     */
    public void enhancerTree2(List<FishNode> fishNodes, List<ZkPondFishDetailDO> detailDOS) {
        if(EasyListUtil.isEmpty(fishNodes) || EasyListUtil.isEmpty(detailDOS)) return;
        // 1. 填充outList
        Map<Long, List<ZkPondFishDetailDO>> collect = detailDOS.stream().collect(Collectors.groupingBy(ZkPondFishDetailDO::getPondId));
        Set<Long> pondIdSet = collect.keySet();
        for(FishNode fishNode : fishNodes) {
            Long pondId = fishNode.getPondId();
            List<ZkPondFishDetailDO> tempList = collect.get(pondId);
            if(EasyListUtil.isEmpty(tempList)) continue;
            List<OutPo> outPoList = getOutPos(tempList);
            fishNode.getFishNodeDetailList().get(0).setOutPoList(outPoList);
        }
        // 2. 填充 打样明细
        Map<Long, List<ZkFishProofingDO>> lastMapByPondIdSet = zkFishProofingService.getLastMapByPondIdSet(pondIdSet);
        for(FishNode fishNode : fishNodes) {
            List<ZkFishProofingDO> proofingList = lastMapByPondIdSet.get(fishNode.getPondId());
            if(EasyListUtil.isEmpty(proofingList)) continue;
            ZkFishProofingDO zkFishProofingDO = proofingList.get(0);
            fishNode.getFishNodeDetailList().get(0).build_proofing(zkFishProofingDO.getCreateTime(), zkFishProofingDO.getCurrNum(),
                    zkFishProofingDO.selectWeight());
        }
        // 3. 填充 销售明细
        List<ZkPondFishDetailDO> saleList = zkPondFishDetailService.getListByPondIds(pondIdSet, PondFishDetailAddTypeEnum.SALE.getAddType());
        if(EasyListUtil.isEmpty(saleList)) return;
        Map<Long, List<ZkPondFishDetailDO>> fishDetailMap = saleList.stream().collect(Collectors.groupingBy(ZkPondFishDetailDO::getPondId));
        for(FishNode fishNode : fishNodes) {
            List<ZkPondFishDetailDO> tempList = fishDetailMap.get(fishNode.getPondId());
            if(EasyListUtil.isEmpty(tempList)) return;
            List<SalePo> salePos = getSalePos(tempList);
            fishNode.getFishNodeDetailList().get(0).setSalePosList(salePos);
        }
    }



}
