package cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkkonwledgetree.ZkKonwledgeTreeDO;
import cn.iocoder.yudao.module.fish.service.zkkonwledgetree.ZkKonwledgeTreeService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @创建人 lr
 * @创建时间 2025-08-13
 * @描述
 */
@Component
@Slf4j
public class TreeGetInfoByStr {

    @Resource
    private ZkKonwledgeTreeService zkKonwledgeTreeService;
    @Resource
    private Knowledge2Str knowledge2Str;

    public String exe_find(String str) {
        // 1. 获取列表
        List<ZkKonwledgeTreeDO> list = zkKonwledgeTreeService.selectList();
        // 2. 转换成树桩结构
        List<ZkKonwledgeTreeDO> tree = transformer2Tree(list);
        // System.out.println("============>"+ JSON.toJSONString(tree));
        // 3. 测试查找节点
        String info = findByTree(tree, str);
        return info;
    }
    public String findByTree(List<ZkKonwledgeTreeDO> tree, String str) {
        Stack<ZkKonwledgeTreeDO> stack = new Stack<>();
        for(int i=tree.size()-1;i>=0;i--) {
            stack.push(tree.get(i));
        }
        while (!stack.isEmpty()) {
            ZkKonwledgeTreeDO temp = stack.pop();
            if(temp == null) {

            } else {
                String content = temp.select_str();
                List<ZkKonwledgeTreeDO> sonList = temp.getSonList();
                // System.out.println(String.format("nodeStr:%s,isContain:%s", content, content.contains(str)));
                if(content.contains(str)) {
                    // return build_content(temp, sonList);
                    return knowledge2Str.select_str(temp, sonList);
                }
                if(EasyListUtil.isNotEmpty(sonList)) {
                    for(int i=sonList.size()-1;i>=0;i--) {
                        stack.push(sonList.get(i));
                    }
                }
            }
        }
        return "";
    }

    public String build_content(ZkKonwledgeTreeDO temp, List<ZkKonwledgeTreeDO> sonList) {
        StringBuilder builder = new StringBuilder(temp.select_str());
        if(EasyListUtil.isEmpty(sonList)) return builder.toString();
        builder.append("\\n可以考虑的解决方案有：");
        List<String> others = new ArrayList<>();
        for (ZkKonwledgeTreeDO item : sonList) {
            others.add(item.select_str());
        }
        builder.append(others.stream().collect(Collectors.joining("\\n")));
        return builder.toString();
    }

    public String build_content_for_tree(ZkKonwledgeTreeDO temp, List<ZkKonwledgeTreeDO> sonList) {
        StringBuilder builder = new StringBuilder(temp.select_str());
        if(EasyListUtil.isEmpty(sonList)) return builder.toString();
        builder.append("可以考虑的解决方案有：");
        List<String> others = new ArrayList<>();
        for (ZkKonwledgeTreeDO item : sonList) {
            others.add(String.format("%s(%s)", item.getNodeTitle(), item.getHandleContent()));
        }
        builder.append(others.stream().collect(Collectors.joining("、")));
        return builder.toString();
    }

    public void writeTree(List<ZkKonwledgeTreeDO> tree) {
        Stack<ZkKonwledgeTreeDO> stack = new Stack<>();
        for(int i=tree.size()-1;i>=0;i--) {
            stack.push(tree.get(i));
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("E:\\Use\\rag_2.txt"))) {
            while (!stack.isEmpty()) {
                ZkKonwledgeTreeDO temp = stack.pop();
                if(temp == null) {

                } else {
                    bw.write(temp.select_str());
                    bw.newLine();  // 换行
                    List<ZkKonwledgeTreeDO> sonList = temp.getSonList();
                    if(EasyListUtil.isNotEmpty(sonList)) {
                        for(int i=sonList.size()-1;i>=0;i--) {
                            stack.push(sonList.get(i));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("写入文件时发生错误：" + e.getMessage());
        }
        System.out.println("Rag文件 生成成功！");
    }

    public List<ZkKonwledgeTreeDO> transformer2Tree(List<ZkKonwledgeTreeDO> list) {
        // 1. 获取 父菜单
        Queue<ZkKonwledgeTreeDO> queue = new LinkedBlockingQueue<>();
        List<ZkKonwledgeTreeDO> parentList = list.stream().filter(obj -> Objects.equals(obj.getParentId(), 0l)).collect(Collectors.toList());
        List<Long> hasHandle = new ArrayList<>();
        List<ZkKonwledgeTreeDO> tree = new ArrayList<>();
        Integer level = 0;
        parentList.forEach(obj -> {
            queue.offer(obj.build_level(level).build_info(obj.getNodeTitle(), "待用户输入", "待用户输入"));
            tree.add(obj);
            hasHandle.add(obj.getId());
        });
        list = removeHasHandle(hasHandle, list);
        // 2. 遍历将子节点纳入父节点中
        while (!queue.isEmpty()) {
            ZkKonwledgeTreeDO v1 = queue.remove();
            List<ZkKonwledgeTreeDO> sonList = getSonList(v1, list);
            list = removeHasHandle(hasHandle, list);
            if(EasyListUtil.isNotEmpty(sonList)) sonList.forEach(obj -> {
                queue.offer(obj);
                hasHandle.add(obj.getId());
            });
        }
        return tree;
    }

    public List<ZkKonwledgeTreeDO> getSonList(ZkKonwledgeTreeDO curr, List<ZkKonwledgeTreeDO> list) {
        Integer level = curr.getLevel()+1;
        List<ZkKonwledgeTreeDO> sonList = list.stream().filter(obj -> Objects.equals(obj.getParentId(), curr.getId())).collect(Collectors.toList());
        if(EasyListUtil.isNotEmpty(sonList)) {
            sonList.stream().forEach(obj -> {
                obj.build_level(level).build_parentNode(curr);
                if(StringUtils.equals(KnowledgeNodeTypeEnum.DESCRIBE.getTypeValue(), curr.getNodeType())) {
                    // 不用管
                } else if(StringUtils.equals(KnowledgeNodeTypeEnum.BODY.getTypeValue(), curr.getNodeType())) {
                    obj.build_info(curr.getNodeTitle(), "待用户输入", "待用户输入");
                } else if(StringUtils.equals(KnowledgeNodeTypeEnum.SYMPTOM.getTypeValue(), curr.getNodeType())) {
                    obj.build_info(curr.getBodyInfo(), curr.getNodeTitle(), "待用户输入");
                } else if(StringUtils.equals(KnowledgeNodeTypeEnum.REASON.getTypeValue(), curr.getNodeType())) {
                    obj.build_info(curr.getBodyInfo(), curr.getSymptomInfo(), curr.getNodeTitle());
                }
            });
        }
        curr.setSonList(sonList);
        curr.setWasVisited(true);
        return sonList;
    }

    public List<ZkKonwledgeTreeDO> removeHasHandle(List<Long> hasHandle, List<ZkKonwledgeTreeDO> list) {
        List<ZkKonwledgeTreeDO> collect = list.stream().filter(obj -> !hasHandle.contains(obj.getId())).collect(Collectors.toList());
        list = collect;
        return list;
    }

}
