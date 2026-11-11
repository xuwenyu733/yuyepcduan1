package cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @创建人 lr
 * @创建时间 2025-05-15
 * @描述
 */
@Data
@NoArgsConstructor
public class AgentToolModel {

    private String modelNames;
    private List<ToolModelItem> itemList;

    public AgentToolModel build() {
        if(EasyListUtil.isEmpty(itemList)) return this;
        String modelNames = itemList.stream().map(obj -> obj.getModelName()).collect(Collectors.joining("、"));
        this.setModelNames(modelNames);
        return this;
    }

    public AgentToolModel(List<ToolModelItem> itemList) {
        this.itemList = itemList;
    }
}
