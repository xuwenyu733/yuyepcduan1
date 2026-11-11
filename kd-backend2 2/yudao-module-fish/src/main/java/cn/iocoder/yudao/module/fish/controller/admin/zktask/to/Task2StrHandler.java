package cn.iocoder.yudao.module.fish.controller.admin.zktask.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyMap3;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.to.report.OneItem;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.to.report.TwoItem;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulingtype.ZkSchedulingTypeDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import cn.iocoder.yudao.module.fish.service.zkschedulingtype.ZkSchedulingTypeService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @创建人 lr
 * @创建时间 2025-09-13
 * @描述
 */
@Component
@Slf4j
public class Task2StrHandler {

    @Autowired
    private ZkSchedulingTypeService zkSchedulingTypeService;
    
    @Autowired
    private cn.iocoder.yudao.module.fish.dal.mysql.zktaskdetail.TaskDetailMapper taskDetailMapper;

    /**
     * 将巡检任务转换成文档，方便 智能体分析
     * @param taskDO
     * @return
     */
    public String parseTask(ZkTaskDO taskDO) {
        // 1. 判断参数
        if(taskDO == null) return "";
        List<TaskDetailDO> taskDetailDOS = taskDO.getTaskDetailDOS();
        if(EasyListUtil.isEmpty(taskDetailDOS)) return "";
        // filterTaskDetail(taskDetailDOS);
        // 2. 转换 字符串
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<taskDetailDOS.size();i++) {
            String taskDetailStr = parseTaskDetail(taskDetailDOS.get(i));
            if(StringUtils.isEmpty(taskDetailStr)) continue;
            builder.append(taskDetailStr).append("\n");
        }

        return builder.toString();
    }

    /**
     * 将巡检任务明细转换成文档，方便 智能体分析
     * @param taskDetailDO
     * @return
     */
    public String parseTaskDetail(TaskDetailDO taskDetailDO) {
        // 1. 判断参数
        if(taskDetailDO == null) return "";
        // if(taskDetailDO.getZkSchedulingTypeDO() == null) return "";
        if(StringUtils.isEmpty(taskDetailDO.getCheckContent())) return "";
        // 2. 转换 字符串
        String head = String.format("巡检内容：%s", taskDetailDO.getCheckName());
        StringBuilder builder = new StringBuilder(head).append("\n");
        // 第一级： checkContent
        JSONArray objects = JSON.parseArray(taskDetailDO.getCheckContent());
        for(int i=0;i<objects.size();i++) {
            // 第二级： checkContent_1[item_2]
            JSONObject temp = objects.getJSONObject(i);
            String typeValue = temp.getString("typeValue");
            String taskDetailStr = "";
            // 2.1 普通巡检任务
            if(Integer.parseInt(typeValue) <= Integer.parseInt(TaskDetailTypeValueEnum.NORMAL.getValue())) { // 普通巡检
                taskDetailStr = parseDetailItem(temp, new ArrayList<>());
            } else if(StringUtils.equals(typeValue, TaskDetailTypeValueEnum.FORM.getValue())) { // 表单巡检
                JSONObject checkSubject = temp;
                continue;
            } else if(StringUtils.equals(typeValue, TaskDetailTypeValueEnum.NOTICE.getValue())){ // 提示
                JSONObject checkSubject = temp;
                continue;
            }
            if(StringUtils.isEmpty(taskDetailStr)) continue;
            builder.append(taskDetailStr).append("\n");
        }
        
        // 补充 t1, t2, t3 信息
        Long pondId = taskDetailDO.getPondId();
        if (pondId != null && pondId > 0) {
            String waterQualityInfo = taskDetailMapper.getWaterQualityInfo(pondId);
            builder.append("水质：").append(StringUtils.isNotBlank(waterQualityInfo) ? waterQualityInfo : "暂未获取到对应信息").append("\n");
            
            Double avgDailyFeed = taskDetailMapper.getAvgDailyFeed(pondId);
            builder.append("鱼：").append(avgDailyFeed != null ? String.format("前三天平均饲料投喂量 %.2f", avgDailyFeed) : "暂未获取到对应信息").append("\n");
            
            String recentAlarms = taskDetailMapper.getRecentAlarms(pondId);
            builder.append("设备：").append(StringUtils.isNotBlank(recentAlarms) ? recentAlarms : "暂未获取到对应信息").append("\n");
        }
        
        return builder.toString();
    }
    public Map<String, Object> parseTaskDetail3(TaskDetailDO taskDetailDO) {
        // 1. 判断参数
        if(taskDetailDO == null) return null;
        // if(taskDetailDO.getZkSchedulingTypeDO() == null) return "";
        if(StringUtils.isEmpty(taskDetailDO.getCheckContent())) return null;
        EasyMap3<Object> easyMap = new EasyMap3<>();
        List<OneItem> listOne = new ArrayList<>();
        easyMap.put("listOne", listOne);
        // 2. 转换 字符串
        String head = String.format("巡检内容：%s", taskDetailDO.getCheckName());
        easyMap.put("checkName", taskDetailDO.getCheckName());
        StringBuilder builder = new StringBuilder(head).append("\n");
        // 第一级： checkContent
        JSONArray objects = JSON.parseArray(taskDetailDO.getCheckContent());
        for(int i=0;i<objects.size();i++) {
            // 第二级： checkContent_1[item_2]
            JSONObject temp = objects.getJSONObject(i);
            String typeValue = temp.getString("typeValue");
            String typeName = temp.getString("typeName");
            List<TwoItem> listTwo = new ArrayList<>();
            OneItem oneItem = new OneItem(typeName, listTwo);
            listOne.add(oneItem);

            String taskDetailStr = "";
            // 2.1 普通巡检任务
            if(Integer.parseInt(typeValue) <= Integer.parseInt(TaskDetailTypeValueEnum.NORMAL.getValue())) { // 普通巡检
                taskDetailStr = parseDetailItem(temp, listTwo);
            } else if(StringUtils.equals(typeValue, TaskDetailTypeValueEnum.FORM.getValue())) { // 表单巡检
                JSONObject checkSubject = temp;
                continue;
            } else if(StringUtils.equals(typeValue, TaskDetailTypeValueEnum.NOTICE.getValue())){ // 提示
                JSONObject checkSubject = temp;
                continue;
            }
            if(StringUtils.isEmpty(taskDetailStr)) continue;
            builder.append(taskDetailStr).append("\n");
        }
        return easyMap.value();
    }
    /**
     * 将巡检任务明细转换成文档，方便 智能体分析
     * @param checkSubject 第二级：  checkContent_1[item_2]
     * @return
     */
    public String parseDetailItem(JSONObject checkSubject, List<TwoItem> listTwo) {
        // 1. 判断参数
        if(checkSubject == null) return "";
        // 2. 转换 字符串
        String split = "    ";
        StringBuilder builder = new StringBuilder();

        JSONArray detailList = checkSubject.getJSONArray("detailList");
        for(int i=0;i<detailList.size();i++) {
            JSONObject tempDetailItem = detailList.getJSONObject(i);
            String itemStr = parseDetailList_item(tempDetailItem, listTwo);
            if(StringUtils.isEmpty(itemStr)) continue;
            builder.append(split).append(itemStr);
        }
        return builder.toString();
    }
    /**
     * 将巡检任务明细转换成文档，方便 智能体分析
     * @param tempDetailItem 第三级：  checkContent_1[item_2].detailList_3[i_4]
     * @return
     */
    public String parseDetailList_item(JSONObject tempDetailItem, List<TwoItem> listTwo) {
        // 1. 判断参数
        if(tempDetailItem == null) return "";
        // 2. 转换 字符串
        String split = "        ";
        StringBuilder builder = new StringBuilder();

        String detailTypeName = tempDetailItem.getString("detailTypeName");
        String head = String.format("巡检科目：%s", detailTypeName);

        builder.append(head).append("\n");
        List<String> keyList = new ArrayList<>(tempDetailItem.keySet());
        for(int i=0;i<keyList.size();i++) {
            String key_en = keyList.get(i);
            if(StringUtils.isEmpty(key_en)) continue;
            if(StringUtils.contains(key_en, "_")) continue;
            if(TaskDetailDict.PROPERTY_DICT_WHITE.contains(key_en)) continue;
            String key_value = parseDetailList_itemProperty(tempDetailItem, key_en, listTwo);
            if(StringUtils.isEmpty(key_value)) continue;
            builder.append(split).append(key_value).append("\n");
        }
        return builder.toString();
    }
    /**
     * 将巡检任务明细转换成文档，方便 智能体分析
     * @param key_en 第四级：  checkContent_1[item_2].detailList_3[i_4].key_en
     * @return
     */
    public String parseDetailList_itemProperty(JSONObject tempDetailItem, String key_en, List<TwoItem> listTwo) {
        // 1. 转换 字符串
        String key_ch = TaskDetailDict.PROPERTY_DICT.get(key_en);
        if(StringUtils.isEmpty(key_ch)) return "";
        StringBuilder builder = new StringBuilder();
        String score = tempDetailItem.getString(String.format("%s_score", key_en));
        String content = tempDetailItem.getString(String.format("%s_content", key_en));
        String fileUrl = tempDetailItem.getString(String.format("%s_fileUrl", key_en));
        String key_ch_info = String.format("巡检项：%s", key_ch);
        String content_info = String.format("评分：%s，描述：%s", score, content);
        builder.append(content_info);
        if(StringUtils.isNotBlank(fileUrl)) {
            String fileStr = String.format("，视频：%s", fileUrl);
            builder.append(fileStr);
        }
        TwoItem twoItem = new TwoItem(key_ch, builder.toString());
        listTwo.add(twoItem);

        return String.format("%s，%s", key_ch_info, builder);
    }

    /**
     * 任务明细 填充 巡检配置
     * @param taskDetailDOS
     */
    public void filterTaskDetail(List<TaskDetailDO> taskDetailDOS) {
        // 1. 获取巡检配置id zk_scheduling_type
        Set<Long> taskConfigIdSet = taskDetailDOS.stream().map(obj -> obj.getTaskConfigId()).collect(Collectors.toSet());
        List<ZkSchedulingTypeDO> zkSchedulingTypeList = zkSchedulingTypeService.getZkSchedulingTypeList(taskConfigIdSet);
        if(EasyListUtil.isEmpty(zkSchedulingTypeList)) return;
        // 2. 设置 检测配置
        for(TaskDetailDO taskDetail : taskDetailDOS) {
            ZkSchedulingTypeDO tempConfig = null;
            for(ZkSchedulingTypeDO schedulingType : zkSchedulingTypeList) {
                if(Objects.equals(taskDetail.getTaskConfigId(), schedulingType.getId())) {
                    tempConfig = schedulingType;
                    break;
                }
            }
            taskDetail.setZkSchedulingTypeDO(tempConfig);
        }
    }

}
