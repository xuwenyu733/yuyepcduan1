package cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch.po.TransferPo;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.to.FishOperationLogTypeEnum;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.PondFishDetailAddTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog.ZkFishOperationLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.service.zkfishoperationlog.ZkFishOperationLogService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.service.zkpondfishdetail.ZkPondFishDetailService;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @创建人 lr
 * @创建时间 2025-09-19
 * @描述
 */
@Component
public class FishDetail2Node {
    @Resource
    private ZkPondFishDetailService zkPondFishDetailService;
    @Resource
    private ZkFishOperationLogService zkFishOperationLogService;
    @Resource
    private ZkPondService zkPondService;
    @Resource
    private FishInfoTool fishInfoTool;
    @Resource
    private SplitForIn splitForIn;

    /**
     * 投放记录 转 批次树
     * @param detailDOS
     * @param fishTree
     * @return
     */
    public List<FishNode> selectByFishDetail(List<ZkPondFishDetailDO> detailDOS, FishTree fishTree) {
        if(EasyListUtil.isEmpty(detailDOS)) return null;
        List<FishNode> list = new ArrayList<>();
        // 1. 根据tree1级节点生成tree2级节点
        Map<Long, List<ZkPondFishDetailDO>> collect = detailDOS.stream().collect(Collectors.groupingBy(ZkPondFishDetailDO::getPondId));
        for(Long key : collect.keySet()) {
            List<ZkPondFishDetailDO> tempList = collect.get(key);
            // 填充节点明细，展示附属框
            FishNode fishNode = detailList2Node(tempList, "push");
            if(fishNode != null) fishTree.pushNode(2, fishNode);
        }
        // 2. 给 tree1节点和tree2级节点 填充 其他统计 数据 通过 zk_fish_operation_log
        normalEnhancer(fishTree);
        // 3. tree非root节点的指标值往 tree的root节点汇总 统计
        sonSum4Root(fishTree);
        fishTree.flushNodeIndex();

        return list;
    }

    /**
     * tree非root节点的指标值往 tree的root节点汇总 统计
     * @param fishTree
     */
    public void sonSum4Root(FishTree fishTree) {
        FishNode root = fishTree.getRoot();
        List<FishNode> fishNodes = fishTree.selectSonNodes();
        System.out.println(String.format("sonSum4Root===============>fishNodes:%s", JSON.toJSONString(fishNodes)));
        if(EasyListUtil.isEmpty(fishNodes)) return;
        root.build_clear();
        BigDecimal feedCount = new BigDecimal(0);
        BigDecimal deadCount = new BigDecimal(0);
        BigDecimal realTimeStoragePool = new BigDecimal(0);
        BigDecimal survivalRate = new BigDecimal(0);
        BigDecimal mortalityRate = new BigDecimal(0);

        BigDecimal dailySpecifications = new BigDecimal(0);
        BigDecimal accumulatedFeedingAmount = new BigDecimal(0);
        BigDecimal accumulativeDeathToll = new BigDecimal(0);
        BigDecimal feedCoefficient = new BigDecimal(0);
        BigDecimal inputCost = new BigDecimal(0);
        BigDecimal biologicalAssets = new BigDecimal(0);
        BigDecimal sumBiologicalAssets = new BigDecimal(0);
        // 后面新增的 信息
        for(FishNode item : fishNodes) {
            feedCount = EasyBigDecimalUtil.add(feedCount, item.getFeedCount());
            deadCount = EasyBigDecimalUtil.add(deadCount, item.getDeadCount());
            realTimeStoragePool = EasyBigDecimalUtil.add(realTimeStoragePool, item.getRealTimeStoragePool());
            // survivalRate = EasyBigDecimalUtil.add(survivalRate, item.getSurvivalRate());
            mortalityRate = EasyBigDecimalUtil.add(mortalityRate, item.getMortalityRate());
            FishNodeDetail fishNodeDetail = item.getFishNodeDetailList().get(0);

            dailySpecifications = EasyBigDecimalUtil.add(dailySpecifications,  fishNodeDetail.getDailySpecifications());
            accumulatedFeedingAmount = EasyBigDecimalUtil.add(accumulatedFeedingAmount,  EasyBigDecimalUtil.toNumber(fishNodeDetail.getAccumulatedFeedingAmount()+""));
            accumulativeDeathToll = EasyBigDecimalUtil.add(accumulativeDeathToll,  EasyBigDecimalUtil.toNumber(fishNodeDetail.getAccumulativeDeathToll()+""));
            feedCoefficient = EasyBigDecimalUtil.add(feedCoefficient, fishNodeDetail.getFeedCoefficient());
            inputCost = EasyBigDecimalUtil.add(inputCost, item.getInputCost());
            biologicalAssets = EasyBigDecimalUtil.add(biologicalAssets, item.getBiologicalAssets());
            sumBiologicalAssets = EasyBigDecimalUtil.add(sumBiologicalAssets, fishNodeDetail.getSumBiologicalAssets());
            root.build_sevenDayMortalityRate3(item.getSevenDayMortalityRate(), realTimeStoragePool)
                    .build_inputCost(item.getInputCost()).build_realTimeStoragePool(item.getRealTimeStoragePool())
                    .build_accumulativeDeathToll(item.getAccumulativeDeathToll(), item.getMortalityRate())
                    .build_accumulatedFeedingAmount(item.getAccumulatedFeedingAmount(), item.getAccumulatedWeightGain())
                    .build_saleFishNum(item.getSaleFishNum(), item.getSaleWeight(), item.getAvgFishSpec())
                    .build_biologicalAssets(item.getBiologicalAssets(), item.getSumBiologicalAssets(), item.getAvgBaitCoefficient())
                    ;
        }
        mortalityRate = EasyBigDecimalUtil.divide(accumulativeDeathToll, EasyBigDecimalUtil.add(realTimeStoragePool, accumulativeDeathToll));
        survivalRate = EasyBigDecimalUtil.subtract(new BigDecimal(1), mortalityRate);
        dailySpecifications = EasyBigDecimalUtil.divide(dailySpecifications, new BigDecimal(fishNodes.size()));
        feedCoefficient = EasyBigDecimalUtil.divide(feedCoefficient,
                new BigDecimal(fishNodes.stream().filter(obj -> EasyBigDecimalUtil.gt0(obj.getFishNodeDetailList().get(0).getFeedCoefficient())).toList().size()));
        // inputCost = EasyBigDecimalUtil.divide(inputCost, new BigDecimal(fishNodes.size()));
        // biologicalAssets = EasyBigDecimalUtil.divide(biologicalAssets, new BigDecimal(fishNodes.size()));

        root.build_rate_feed_dead(survivalRate, feedCount, deadCount).setMortalityRate(mortalityRate).build_avgRes(fishNodes.size());
        root.getFishNodeDetailList().get(0).build_accumulate(dailySpecifications+"", accumulatedFeedingAmount+"", accumulativeDeathToll+"",
                feedCoefficient+"", inputCost+"", biologicalAssets+"")
                .setSumBiologicalAssets(sumBiologicalAssets);
        System.out.println("sonSum4Root===>"+JSON.toJSONString(root));
    }

    public void normalEnhancer(FishTree fishTree) {
        // 0. 鱼 根目录 数据补充
        FishNode root = fishTree.getRoot();
        List<ZkFishOperationLogDO> rootLogs = zkFishOperationLogService.getByBatchNumbers(new HashSet<>(Collections.singletonList(root.getOriginBatchNumber())), FishOperationLogTypeEnum.POND.getLogType());
        if(EasyListUtil.isNotEmpty(rootLogs)) {
            for(ZkFishOperationLogDO item : rootLogs) {
                root.build_operationLog(item);
            }
        }
        // 1. 鱼树 2级 统计指标 补充
        List<FishNode> fishNodes = fishTree.selectSonNodes();
        normalEnhancer4Nodes(fishNodes);
    }
    /**
     * 1.节点信息汇总，son node向root汇总
     * 2. 所有节点的 转入信息补充
     * @param fishTreeList
     */
    public void normalEnhancer4TreeNodes(List<FishTree> fishTreeList) {
        if(EasyListUtil.isEmpty(fishTreeList)) return;
        // 0. 所有节点需要添加 转入：分池类型
        splitForIn.splitForInExe(fishTreeList);
        for(FishTree fishTree : fishTreeList) {
            // 1. 计算 分池节点 统计数据
            normalEnhancer4Other(fishTree);
            // System.out.println(String.format("normalEnhancer4TreeNodes 10==================>fishTree:%s", JSON.toJSONString(fishTree)));
            // 2. 子节点数据汇总到父节点
            sonSum4Root(fishTree);
            // System.out.println(String.format("normalEnhancer4TreeNodes 11==================>fishTree:%s", JSON.toJSONString(fishTree)));
            fishTree.flushNodeIndex();
        }
    }
    /**
     * 计算 分池节点 统计数据
     * @param fishTree
     */
    public void normalEnhancer4Other(FishTree fishTree) {
        // 1. 鱼树 2级 统计指标 补充
        List<String> whiteSplit = Arrays.asList("1", "2");
        Map<String, List<FishNode>> treeMap = fishTree.getTreeMap();
        for(String level : treeMap.keySet()) {
            if(whiteSplit.contains(level)) continue;
            List<FishNode> fishNodes = treeMap.get(level);
            normalEnhancer4Nodes(fishNodes);
        }
    }
    /**
     * 鱼树 2级 统计指标 补充
     * @param fishNodes
     */
    public void normalEnhancer4Nodes(List<FishNode> fishNodes) {
        if(EasyListUtil.isEmpty(fishNodes)) return;
        // 1. 部分节点 存塘信息不完善，需要补充完整
        normalEnhancer4Pond(fishNodes);
        Set<Long> pondIdSet = fishNodes.stream().map(FishNode::getPondId).collect(Collectors.toSet());
        List<ZkFishOperationLogDO> operationLog = zkFishOperationLogService.getByPondIds(pondIdSet);
        if(EasyListUtil.isEmpty(operationLog)) return;
        for(FishNode fishNode : fishNodes) {
            // 2. 找到 当前节点
            ZkFishOperationLogDO curr = null;
            for(ZkFishOperationLogDO item : operationLog) {
                if(Objects.equals(fishNode.getPondId(), item.getPondId())) {
                    curr = item;
                    break;
                }
            }
            if(curr == null) continue;
            // 3. 书节点 添加统计数据
            fishNode.build_operationLog(curr);
        }
    }

    /**
     * 部分节点 存塘信息不完善，需要补充完整
     * @param fishNodes
     */
    public void normalEnhancer4Pond(List<FishNode> fishNodes) {
        if(EasyListUtil.isEmpty(fishNodes)) return;
        // 1. 如果有 节点存塘信息为空，则填充存塘信息
        Set<Long> pondIdNeedFillSet = fishNodes.stream().filter(obj -> StringUtils.isEmpty(obj.getWorkshopName()) || obj.getPondScale()==null)
                .map(FishNode::getPondId).collect(Collectors.toSet());
        List<ZkPondDO> zkPondList = zkPondService.getZkPondList(pondIdNeedFillSet);
        if(EasyListUtil.isEmpty(zkPondList)) return;
        for(FishNode nodeItem : fishNodes) {
            ZkPondDO currPond = null;
            for(ZkPondDO pondDO : zkPondList) {
                if(Objects.equals(pondDO.getId(), nodeItem.getPondId())) {
                    currPond = pondDO;
                    break;
                }
            }
            if(currPond == null) continue;
            nodeItem.build_pondBase(currPond);
        }
    }

    /**
     * 当前值和历史值进行比对
     * @param historyRoots
     * @param currRoots
     */
    public void handleUpOrDown(List<FishTree> historyRoots, List<FishTree> currRoots) {
        for(FishTree currTree : currRoots) {
            // 1. 找到历史 树
            FishTree historyTree = null;
            for(FishTree historyItem : historyRoots) {
                if(StringUtils.equals(currTree.getRoot().getOriginBatchNumber(), historyItem.getRoot().getOriginBatchNumber())) {
                    historyTree = historyItem;
                    break;
                }
            }
            if(historyTree == null) continue;
            // 2. 指标 对比
            exe_handleUpOrDown(historyTree, currTree);
        }
    }
    public FishTree exe_handleUpOrDown(FishTree historyTree, FishTree currTree) {
        Map<String, List<FishNode>> history_treeMap = historyTree.getTreeMap();
        Map<String, List<FishNode>> curr_treeMap = currTree.getTreeMap();
        for(String level : curr_treeMap.keySet()) {
            List<FishNode> currNodes = curr_treeMap.get(level);
            List<FishNode> historyNodes = history_treeMap.get(level);

            exe_handleUpOrDown4Nodes(historyNodes, currNodes);
        }
        return currTree;
    }
    public List<FishNode> exe_handleUpOrDown4Nodes(List<FishNode> historyNodes, List<FishNode> currNodes) {
        if(EasyListUtil.isEmpty(historyNodes)) return currNodes;
        if(EasyListUtil.isEmpty(currNodes)) return currNodes;
        for(FishNode curr : currNodes) {
            // 1. 找到历史
            FishNode history = null;
            for(FishNode historyItem : historyNodes) {
                if(historyItem != null && curr != null && Objects.equals(1, curr.getNodeType()) && curr.getOriginBatchNumber() != null
                        && historyItem.getOriginBatchNumber() != null
                        && StringUtils.equals(curr.getOriginBatchNumber(), historyItem.getOriginBatchNumber())) {
                    history = historyItem;
                    break;
                }
                if(historyItem != null && curr != null && Objects.equals(2, curr.getNodeType())  && curr.getPondId() != null
                        && historyItem.getPondId() != null
                        && Objects.equals(curr.getPondId(), historyItem.getPondId())) {
                    history = historyItem;
                    break;
                }
            }
            if(history == null) continue;
            // 2. 处理历史
            BigDecimal subtractFeedCount = EasyBigDecimalUtil.subtract(curr.getFeedCount(), history.getFeedCount());
            subtractFeedCount = EasyBigDecimalUtil.toScale(subtractFeedCount, 2);
            BigDecimal subtractDeadCount = EasyBigDecimalUtil.subtract(curr.getDeadCount(), history.getDeadCount());
            subtractDeadCount = EasyBigDecimalUtil.toScale(subtractDeadCount, 2);
            String feedCountTrend = "down";
            String deadCountTrend = "down";
            if(EasyBigDecimalUtil.gt0(subtractFeedCount)) {
                feedCountTrend = "up";
            } else if(EasyBigDecimalUtil.ge0(subtractFeedCount)) {
                feedCountTrend = "flat";
            }
            if(EasyBigDecimalUtil.gt0(subtractDeadCount)) {
                deadCountTrend = "up";
            } else if(EasyBigDecimalUtil.ge0(subtractDeadCount)) {
                deadCountTrend = "flat";
            }
            curr.build_upOrDown(subtractFeedCount+"", feedCountTrend, subtractDeadCount+"", deadCountTrend);
            // System.out.println("curr=====================>"+ JSON.toJSONString(curr));
        }
        return currNodes;
    }

    public void handleSplitByTrees(Integer level, List<FishTree> roots) {
        if(EasyListUtil.isEmpty(roots)) return;
        for(FishTree fishTree : roots) {
            handleSplit(level, fishTree);
        }
        // System.out.println(String.format("handleSplitByTrees 7==================>fishNode:%s", JSON.toJSONString(roots)));
    }
    public void handleSplit(Integer level, FishTree fishTree) {
        // 1. 当前节点 信息填充
        Set<Long> nextParentPondIdSet = exe_handleSplit(level, fishTree, null);
        Integer count = 0;
        // 2. 下级 分池明细 填充 ==> 注意，这里因为是树，如果出现分池死循环需要注意  && !fishTree.isAllJudge(nextParentPondIdSet)
        while (!CollectionUtils.isAnyEmpty(nextParentPondIdSet) && count<5) {
            // nextParentPondIdSet = fishTree.selectNoContain(nextParentPondIdSet);
            level += 1;
            nextParentPondIdSet = exe_handleSplit(level, fishTree, nextParentPondIdSet);
            count++;
        }
    }
    public Set<Long> exe_handleSplit(Integer level, FishTree fishTree, Set<Long> parentPondIdSetP) {
        // 1. 获取父节点的分池记录
        Integer parentLevel = level - 1;
        // List<FishNode> fishNodes = fishTree.getTreeMap().get(parentLevel.toString());
        List<FishNode> fishNodes = fishTree.selectSonNodes();
        if(EasyListUtil.isEmpty(fishNodes)) return null;
        Set<Long> parentPondIdSet = fishNodes.stream().map(FishNode::getPondId).collect(Collectors.toSet());
        if(parentPondIdSetP != null && !CollectionUtils.isAnyEmpty(parentPondIdSetP)) { // 说明是>3级进来的
            // parentPondIdSet = parentPondIdSetP;
        } else if(parentPondIdSetP != null && CollectionUtils.isAnyEmpty(parentPondIdSetP)) { // 说明是3级进来的
            return null;
        }
        List<ZkPondFishDetailDO> fishDetailList = zkPondFishDetailService.byPondIdsExcludeTargetPondIds(parentPondIdSet, PondFishDetailAddTypeEnum.SPLIT.getAddType(), parentPondIdSet);
        if(EasyListUtil.isEmpty(fishDetailList)) return null;
        // 2. 分池级别 统计
        List<FishNode> list = new ArrayList<>();
        // 按照 分塘 进行分组
        Map<Long, List<ZkPondFishDetailDO>> originPondId2Details = fishDetailList.stream().collect(Collectors.groupingBy(ZkPondFishDetailDO::getTargetPondId));
        for(Long targetPondId : originPondId2Details.keySet()) {
            List<ZkPondFishDetailDO> tempList = originPondId2Details.get(targetPondId);
            if(EasyListUtil.isEmpty(tempList)) continue;

            FishNode fishNode = detailList2Node(tempList, "split");
            if(fishNode != null) list.add(fishNode.build_parentNode(ParentNodeItem.selectByTreeAndSplitList(fishNodes, tempList)));
        }
        Set<Long> nextParentPondIdSet = list.stream().map(FishNode::getPondId).collect(Collectors.toSet());
        fishTree.pushNodes(level, list).flushNodeIndex();
        // 3. tree父级节点填充 detail信息
        fishInfoTool.enhancerTree2(fishNodes, fishDetailList);
        return nextParentPondIdSet;
    }
    public FishNode getNodeByPondId(List<FishNode> fishNodes, Long currPondId) {
        for(FishNode item : fishNodes) {
            if(Objects.equals(currPondId, item.getPondId())) return item;
        }
        return null;
    }

    /**
     * 鱼操作明细列表 2 树node列表
     * @param tempList
     * @param type split:分池;push:投放;
     * @return
     */
    public FishNode detailList2Node(List<ZkPondFishDetailDO> tempList, String type) {
        if(EasyListUtil.isEmpty(tempList)) return null;
        // 2. 分池级别 统计
        FishNode fishNode = null;
        FishNodeDetail fishNodeDetail = null;
        for(ZkPondFishDetailDO item : tempList) {
            // 1. 创建 节点
            if(fishNode == null) {
                if(StringUtils.equals("push", type)) {
                    fishNode = new FishNode(item.getBatchNumber(), item.getWorkshopId(), item.getWorkshopName(), null, item.getPondSystemName(),
                            item.getPondNum(), item.getPondId(), item.getPondName(), item.getPondDetailNum(), new BigDecimal(item.getFishNum()), item.getFishSpec());
                } else if(StringUtils.equals("split", type)) {
                    fishNode = new FishNode(item.getBatchNumber(), null, null, null, item.getTargetPondSystemName(),
                            item.getTargetPondNum(), item.getTargetPondId(), item.getTargetPondName(), item.getTargetPondDetailNum(), new BigDecimal(item.getFishNum()), item.getTargetFishSpec());
                }
            } else {
                if(StringUtils.equals("push", type)) {
                    fishNode.build_fishBase(new BigDecimal(item.getFishNum()), item.getFishSpec());
                } else if(StringUtils.equals("split", type)) {
                    fishNode.build_fishBase(new BigDecimal(item.getFishNum()), item.getTargetFishSpec());
                }
            }
            // 2. 创建 节点明细
            if(fishNodeDetail == null) {
                fishNodeDetail = new FishNodeDetail(EasyDateUtil.format(item.getCreateTime(), "yyyy.MM.dd"), item.getFishNum()+"", fishNode.getFishSpec(), item.getSplitRadio()+"");
            } else {
                fishNodeDetail.build_fishBase(EasyDateUtil.format(item.getCreateTime(), "yyyy.MM.dd"), fishNode.getRealTimeStoragePool()+"", fishNode.getFishSpec(), item.getSplitRadio()+"");
            }
            // 3. 创建 节点明细 转入push，split不需要，会在后面汇总 统计
            if(StringUtils.equals("push", type)) {
                BigDecimal transferSevenDayMortalityRatePer = EasyBigDecimalUtil.divide(new BigDecimal(item.getSevenDayMortalityRate()), new BigDecimal(item.getFishNum()));
                TransferPo transferPo = new TransferPo(EasyDateUtil.format(item.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                        item.getFishNum(), item.getTargetWeight(), EasyBigDecimalUtil.toNumber(item.getFishSpec()), item.getSplitRadio(), item.getSevenDayMortalityRate(), transferSevenDayMortalityRatePer,
                        PondFishDetailAddTypeEnum.ADD.getAddType(), null);
                fishNodeDetail.build_transferPoList(List.of(transferPo));
            }
        }
        // System.out.println(String.format("detailList2Node 8==================>fishNode:%s", JSON.toJSONString(fishNode)));
        return fishNode.build_fishNodeDetailList(fishNodeDetail);
    }


    /**
     * 书节点里面的空数据 realTimeStoragePool 删除
     * @param fishTreeList
     */
    public List<FishTree> treeNodesRemoveEmpty(List<FishTree> fishTreeList) {
        if(EasyListUtil.isEmpty(fishTreeList)) return fishTreeList;
        // 0. 所有树需要遍历
        for(FishTree fishTree : fishTreeList) {
            // 1. 去掉树的空node
            fishTree.emptyNodesRemove();
        }
        return fishTreeList;
    }


}
