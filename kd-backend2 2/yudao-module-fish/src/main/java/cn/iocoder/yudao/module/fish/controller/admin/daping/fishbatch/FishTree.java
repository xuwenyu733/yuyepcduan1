package cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @创建人 lr
 * @创建时间 2025-09-19
 * @描述
 */
@Data
@NoArgsConstructor
public class FishTree {

    private FishNode root; // 根节点
    private Integer maxLevel; // 最高级别, 注意这里不是从0开始，而是从1开始
    private Map<String, List<FishNode>> treeMap = new HashMap<>();
    private Set<Long> pondIdSet = new HashSet<>();

    public FishTree addRoot(FishNode root) {
        this.root = root;
        this.maxLevel = 1;
        root.setNodeType(1);
        treeMap.put(String.format("%s", this.maxLevel), Arrays.asList(root));
        return this;
    }

    public FishTree pushNode(Integer level, FishNode node) {
        List<FishNode> fishNodes = treeMap.get(level.toString());
        if(EasyListUtil.isEmpty(fishNodes)) {
            fishNodes = new ArrayList<>();
            if(level > this.maxLevel) this.maxLevel = level;
        }
        fishNodes.add(node);
        treeMap.put(level.toString(), fishNodes);
        return this;
    }
    public FishTree pushNodes(Integer level, List<FishNode> nodes) {
        List<FishNode> fishNodes = treeMap.get(level.toString());
        if(EasyListUtil.isEmpty(fishNodes)) {
            fishNodes = new ArrayList<>();
            if(level > this.maxLevel) this.maxLevel = level;
        }
        fishNodes.addAll(nodes);
        treeMap.put(level.toString(), fishNodes);
        return this;
    }
    public FishTree flushNodeIndex() {
        Set<String> levelSet = treeMap.keySet();
        for(String level : levelSet) {
            Integer levelInt = Integer.parseInt(level);
            List<FishNode> fishNodes = treeMap.get(level);
            if(EasyListUtil.isEmpty(fishNodes)) continue;
            for(int i=0;i<fishNodes.size();i++) {
                FishNode currNode = fishNodes.get(i);
                currNode.build_location(levelInt, i).build_fishDensity()
                        .build_sonInfo();
                if(currNode.getPondId() != null) pondIdSet.add(currNode.getPondId());
            }
        }
        return this;
    }

    public Boolean isAllJudge(Set<Long> needJudgeId) {
        Boolean flag = true;
        for(Long judgeId : needJudgeId) {
            if(!pondIdSet.contains(judgeId)) {
                flag = false;
                break;
            }
        }
        return flag;
    }
    public Set<Long> selectNoContain(Set<Long> needJudgeId) {
        Set<Long> needAddSet = new HashSet<>();
        for(Long judgeId : needJudgeId) {
            if(!pondIdSet.contains(judgeId)) {
                needAddSet.add(judgeId);
            }
        }
        return needAddSet;
    }
    public synchronized List<FishNode> selectSonNodes() {
        List<FishNode> list = new ArrayList<>();
        Set<Long> pondIdSet = new HashSet<>();
        for(String level : treeMap.keySet()) {
            // if("1".equals(level)) continue;
            List<FishNode> tempNodes = treeMap.get(level);
            if(EasyListUtil.isEmpty(tempNodes)) continue;
            for(FishNode item : tempNodes) {
                if(item.getPondId() != null && pondIdSet.add(item.getPondId()))
                    list.add(item);
            }
        }
        return list;
    }
    public synchronized FishTree emptyNodesRemove() {
        for(String level : treeMap.keySet()) {
            List<FishNode> tempNodes = treeMap.get(level);
            List<FishNode> temp = new ArrayList<>();
            if(EasyListUtil.isEmpty(tempNodes)) continue;
            for(FishNode item : tempNodes) {
                if(!EasyBigDecimalUtil.eq0(item.getRealTimeStoragePool()))
                    temp.add(item);
            }
            treeMap.put(level, temp);
        }
        return this;
    }

}
