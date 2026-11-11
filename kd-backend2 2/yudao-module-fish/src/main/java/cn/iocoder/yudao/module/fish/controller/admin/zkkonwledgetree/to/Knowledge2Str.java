package cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.tool.WaterCheckParam;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.tool.WaterChecker;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkkonwledgetree.ZkKonwledgeTreeDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @创建人 lr
 * @创建时间 2025-08-17
 * @描述
 */
@Slf4j
@Component
public class Knowledge2Str {
    
    @Resource
    public WaterChecker waterChecker;

    public String select_str(ZkKonwledgeTreeDO temp, List<ZkKonwledgeTreeDO> sonList) {
        String res = "";
        int nodeType = Integer.parseInt(temp.getNodeType());
        if(nodeType < 50) { // 疾病智能体
            res = select_strIll(temp, sonList);
        } else if (nodeType > 50 && nodeType < 100) { // 常见智能体
            res = select_strUse(temp, sonList);
        }

        return res;
    }

    /**
     * 疾病智能体节点
     * @param temp
     * @param sonList
     * @return
     */
    public String select_strIll(ZkKonwledgeTreeDO temp, List<ZkKonwledgeTreeDO> sonList) {
        StringBuilder builder = new StringBuilder();
        if(StringUtils.equals(KnowledgeNodeTypeEnum.DESCRIBE.getTypeValue(), temp.getNodeType())) {
            String collect = sonList.stream().map(obj -> obj.getNodeTitle()).collect(Collectors.joining("、"));
            builder.append(String.format("请先确认鱼的部位，鱼部位有： <br>【%s】", collect));
        } else if(StringUtils.equals(KnowledgeNodeTypeEnum.BODY.getTypeValue(), temp.getNodeType())) {
            String collect = sonList.stream().map(obj -> obj.getNodeTitle()).collect(Collectors.joining("<br>"));
            builder.append(String.format("请先确认鱼【%s】 的发病症状，症状有： <br>【%s】", temp.getNodeTitle(), collect));
        } else if(StringUtils.equals(KnowledgeNodeTypeEnum.SYMPTOM.getTypeValue(), temp.getNodeType())) {
            StringBuilder bui2 = new StringBuilder();
            for(int i=0;i<sonList.size();i++) {
                bui2.append(String.format("原因%s:%s;", (i+1), sonList.get(i).getNodeTitle())).append("<br>");
            }
            String collect = bui2.toString();
            // 此时已经知道用户输入发病部位和症状，需要用户进一步确认发病原因。
            // 将该结果直接发送用户好进一步确认发病原因序号(eg:原因1)，不要做其他处理。
            builder.append(String.format("请先确认鱼【%s】 出现【%s】 的可能发病原因，原因有：<br>【%s】", temp.getBodyInfo(), temp.getNodeTitle(), collect));
        } else if(StringUtils.equals(KnowledgeNodeTypeEnum.REASON.getTypeValue(), temp.getNodeType())) {
            String collect = sonList.stream().map(obj -> obj.select_str()).collect(Collectors.joining("<br>"));
            builder.append(String.format("当鱼 在【%s】 出现【%s】 是因为【%s】 时，系统给出解决的方法有：<br> %s", temp.getBodyInfo(), temp.getSymptomInfo(), temp.getNodeTitle(), collect));
        }

        return builder.toString();
    }

    /**
     * 常见智能体选择
     * @param temp
     * @param sonList
     * @return
     */
    public String select_strUse(ZkKonwledgeTreeDO temp, List<ZkKonwledgeTreeDO> sonList) {
        StringBuilder builder = new StringBuilder();
        if(EasyListUtil.isEmpty(sonList)) {
            builder.append(String.format("针对出现%s，本地知识库暂无记录，请联网搜索对应解决办法，可以从鱼、水、设备、人为因素四个角度分析。", temp.getNodeTitle()));
            return builder.toString();
        }
        builder.append(String.format("针对出现%s，可能造成的原因有： \n", temp.getNodeTitle()));
        // 当前级别是鱼、水、设备、人为因素
        String split = "    ";
        for(ZkKonwledgeTreeDO item : sonList) {
            // 当前级别是可能原因
            List<ZkKonwledgeTreeDO> tempList = item.getSonList();
            if("水".equals(item.getNodeTitle()) && EasyListUtil.isEmpty(tempList)) {
                builder.append(String.format("%s从【水】因素方向分析： %s\n", split, waterChecker.selectWaterCheck(new WaterCheckParam())));
                continue;
            } else if(EasyListUtil.isEmpty(tempList)) continue;
            builder.append(String.format("%s从【%s】因素方向分析： \n", "", item.getNodeTitle())); // split
            for(ZkKonwledgeTreeDO maybeItem : tempList) {
                String andSure = selectMaybeAndSure(maybeItem);
                builder.append(String.format("%s%s\n", split, andSure));
            }
        }

        return builder.toString();
    }

    public String selectMaybeAndSure(ZkKonwledgeTreeDO maybeItem) {
        List<ZkKonwledgeTreeDO> sureList = maybeItem.getSonList();
        if(EasyListUtil.isEmpty(sureList)) {
            String prefix = String.format("可能是因为 %s 造成的。", maybeItem.getNodeTitle());
            return prefix;
        }
        StringBuilder builder = new StringBuilder();
        String prefix = String.format("如果是%s 造成的，可以：\n", maybeItem.getNodeTitle());
        builder.append(prefix);
        String split = "        ";
        List<String> temp = new ArrayList<>();
        for(ZkKonwledgeTreeDO item : sureList) {
            temp.add(String.format("%s 【%s】", split, item.getNodeTitle()));
        }
        builder.append(temp.stream().collect(Collectors.joining("\n")));
        return builder.toString();
    }

}
