package cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class ParentNodeItem {

    private Long parentPondId; // 父池子，分池涉及
    private Integer parentNodeIndex = 0; // 父节点 序号

    public ParentNodeItem(Long parentPondId, Integer parentNodeIndex) {
        this.parentPondId = parentPondId;
        this.parentNodeIndex = parentNodeIndex;
    }

    public static List<ParentNodeItem> selectByTreeAndSplitList(List<FishNode> parentNodes, List<ZkPondFishDetailDO> tempList) {
        List<ParentNodeItem> list = new ArrayList<>();
        Set<Long> parentNodePondIdSet = CollectionUtils.convertSet(tempList, ZkPondFishDetailDO::getPondId);
        for(int i = 0; i < parentNodes.size(); i++) {
            FishNode fishNode = parentNodes.get(i);
            Long nodePondId = fishNode.getPondId();
            if(parentNodePondIdSet.contains(nodePondId)) {
                ParentNodeItem nodeItem = new ParentNodeItem(nodePondId, fishNode.getNodeIndex());
                list.add(nodeItem);
            }
        }
        return list;
    }

}
