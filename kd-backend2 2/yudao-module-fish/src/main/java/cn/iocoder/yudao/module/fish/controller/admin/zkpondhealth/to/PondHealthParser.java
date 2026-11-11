package cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyMap3;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ChatRespVo;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ChatVo;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.to.Task2StrHandler;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicerecord.ZkDeviceRecordDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondhealth.ZkPondHealthDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import cn.iocoder.yudao.module.fish.service.aiagent.AiAgentService;
import cn.iocoder.yudao.module.fish.service.aiagent.to.ResponseToJsonStr;
import cn.iocoder.yudao.module.fish.service.zkpondhealth.ZkPondHealthService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 将 设备统计、任务信息 ==》中文描述==》请求大模型==》获取json结果==》填充数据库
 */
@Slf4j
@Component
public class PondHealthParser {

    @Resource
    private Task2StrHandler task2StrHandler;
    @Resource
    private AiAgentService aiAgentService;
    @Autowired
    private AdminUserApi adminUserApi;
    @Autowired
    private ZkPondHealthService zkPondHealthService;

    /**
     * 执行 设备记录、任务信息 ==》中文描述==》请求大模型==》获取json结果==》填充数据库   的 方法入口
     * @param pondDO 存塘信息
     * @param recordDOS 存塘设备记录
     * @param taskDOS 任务明细信息
     * @return 转换的结果信息
     */
    public ZkPondHealthDO exeParser(ZkPondDO pondDO, List<ZkDeviceRecordDO> recordDOS, List<ZkTaskDO> taskDOS) {
        // 1. 将 内容转成 中文描述
        String text = parserForText(recordDOS, taskDOS);
        // 2. 填写 大模型的 提示词，方便整体功能实现
        String modelText = parserForModelText(text);
        // 3. 请求大模型 得到对应结果
        return parserForModelRes(pondDO, modelText);
    }

    public ZkPondHealthDO parserForModelRes(ZkPondDO pondDO, String modelText) {
        String otherParams = new EasyMap3<Long>().put("taskDetailId", pondDO.getId()).toJson();
        // 1. 生成uid
        AdminUserRespDTO adminUserRespDTO = adminUserApi.getUser(SecurityFrameworkUtils.getLoginUserId());
        String uid = ChatVo.selectUid(adminUserRespDTO.getUsername(), adminUserRespDTO.getNickname(), adminUserRespDTO.getId());
        // 2. 请求大模型
        String time = new Date().getTime()+"";
        ChatVo chatVo = new ChatVo(uid, adminUserRespDTO.getId()+"", modelText, "token",
                "pc", "deepseek-v3", "check_summary", "false", time,
                String.format("%sAAA%s", adminUserRespDTO.getId(), time)).build_otherParams(otherParams);
        ChatRespVo chatRespVo = aiAgentService.chat(chatVo);
        ZkPondHealthDO res = ResponseToJsonStr.strToObj(chatRespVo, ZkPondHealthDO.class);
        if(res == null) return null;
        res.setPondId(pondDO.getId()).setDeptId(pondDO.getDeptId());
        zkPondHealthService.customSave(res);
        log.warn("【Java请求智能体服务生成健康评分-{}】middle,操作类型为 巡检明细内容：{}, 对应存塘:{}, 智能体返回内容:{},最终结果：{}",
                TokenUtil.userInfo(), modelText, JSON.toJSONString(pondDO), JSON.toJSONString(chatRespVo), JSON.toJSONString(res));

        return res;
    }

    /**
     * 将 设备记录、任务信息 转换成 中文描述后， 需要填充 大模型的描述语，方便json返回
     * @param text 中文描述
     * @return 能够交给大模型转换的提示词
     */
    private String parserForModelText(String text) {
        String standard = "评分口径：外部环境层满分20 + 外观行为层满分60 + 内部肌体层满分20 = 综合100。\n" +
                "请只输出可解析的 JSON 字段值；具体分值可由后端按规则重算，但 information 必须尽量完整。\n" +
                "\n" +
                "--- 外部环境层信息 environmentalLayerInformation\n" +
                "溶氧 doOxygen / 亚硝酸盐 nitrite / 氨氮 ammoniaNitrogen / ph phPh / 水温 doTemp / 硝酸盐 nitrate\n" +
                "示例：{\"doOxygen\":5.7,\"nitrite\":0.05,\"ammoniaNitrogen\":0.2,\"phPh\":7.8,\"doTemp\":25.8,\"nitrate\":20.3}\n" +
                "\n" +
                "--- 外观行为层信息 behavioralLayerInformation\n" +
                "摄食量 foodIntakeAmount（15分）\n" +
                "摄食程度 foodIntakeDegree（10分）\n" +
                "体型 bodyShape（5分）\n" +
                "体色 bodyColor（5分）\n" +
                "损伤 injury（5分）\n" +
                "游泳情况 swimming（10分）\n" +
                "死亡情况 death（10分，参照死亡预警阈值，可用“无/正常/今日死亡N尾/异常”描述）\n" +
                "示例：{\"foodIntakeAmount\":\"正常\",\"foodIntakeDegree\":\"强\",\"bodyShape\":\"正常\",\"bodyColor\":\"正常\",\"injury\":\"无\",\"swimming\":\"正常\",\"death\":\"无\"}\n" +
                "\n" +
                "--- 内部肌体层信息 bodyLayerInformation\n" +
                "肝脏与胆囊 liverAndGallbladder / 鳃丝 gillFilaments / 肠道 intestine / 内脏结节 visceralNodule / 规格与饵料系数 specificationAndBaitCoefficient\n" +
                "示例：{\"liverAndGallbladder\":\"正常\",\"gillFilaments\":\"正常\",\"intestine\":\"正常\",\"visceralNodule\":\"正常\",\"specificationAndBaitCoefficient\":\"正常\"}";
        String suffix = "请返回字段：\n" +
                "environmentalLayerRating（环境层，满分20）\n" +
                "behavioralLayerRating（行为层，满分60）\n" +
                "bodyLayerRating（机体层，满分20）\n" +
                "overallRating = 三层相加（满分100）\n" +
                "environmentalLayerInformation / behavioralLayerInformation / bodyLayerInformation（JSON 字符串）\n" +
                "exceptionMessage（异常汇总文本）\n" +
                "numberOfExceptions（异常个数）\n" +
                "没有提及的一律按正常。最终示例：\n" +
                "{\n" +
                "  \"environmentalLayerRating\": 18.00,\n" +
                "  \"behavioralLayerRating\": 55.00,\n" +
                "  \"bodyLayerRating\": 16.00,\n" +
                "  \"overallRating\": 89.00,\n" +
                "  \"environmentalLayerInformation\": \"{\\\"doOxygen\\\":5.7,\\\"nitrite\\\":0.05,\\\"ammoniaNitrogen\\\":0.2,\\\"phPh\\\":7.8,\\\"doTemp\\\":25.8,\\\"nitrate\\\":20.3}\",\n" +
                "  \"behavioralLayerInformation\": \"{\\\"foodIntakeAmount\\\":\\\"正常\\\",\\\"foodIntakeDegree\\\":\\\"强\\\",\\\"bodyShape\\\":\\\"正常\\\",\\\"bodyColor\\\":\\\"正常\\\",\\\"injury\\\":\\\"无\\\",\\\"swimming\\\":\\\"正常\\\",\\\"death\\\":\\\"无\\\"}\",\n" +
                "  \"bodyLayerInformation\": \"{\\\"liverAndGallbladder\\\":\\\"正常\\\",\\\"gillFilaments\\\":\\\"正常\\\",\\\"intestine\\\":\\\"正常\\\",\\\"visceralNodule\\\":\\\"正常\\\",\\\"specificationAndBaitCoefficient\\\":\\\"正常\\\"}\",\n" +
                "  \"exceptionMessage\": \"\",\n" +
                "  \"numberOfExceptions\": 0\n" +
                "}";
        StringBuilder builder = new StringBuilder();
        builder.append(text).append("转换内容中英文对应关系如下：\n");
        builder.append(standard).append("\n").append(suffix);

        return builder.toString();
    }

    /**
     * 将 巡检信息、设备信息 转成 中文描述 的  方法入口
     * @param recordDOS 传感器信息
     * @param taskDOS 巡检内容
     * @return 中文描述
     */
    private String parserForText(List<ZkDeviceRecordDO> recordDOS, List<ZkTaskDO> taskDOS) {
        StringBuilder builder = new StringBuilder("当前存塘的巡检及传感器信息如下：");
        // 1. 将 设备记录 信息 给转成指标信息
        String deviceInfo = forTextByDevice(recordDOS);
        String taskInfo = forTextByTask(taskDOS);
        builder.append(deviceInfo).append("\n").append(taskInfo);
        return builder.toString();
    }

    /**
     * 将巡检信息转换成 中文描述
     * @param taskDOS 巡检内容
     * @return 返回巡检中文描述
     */
    private String forTextByTask(List<ZkTaskDO> taskDOS) {
        StringBuilder builder = new StringBuilder("巡检信息如下：\n");
        for(ZkTaskDO taskDO : taskDOS) {
            String taskInfo = task2StrHandler.parseTask(taskDO);
            builder.append(taskInfo).append("\n");
        }
        return builder.toString();
    }

    /**
     * 将设备信息 转换成 中文描述
     * @param recordDOS 设备记录
     * @return 中文描述
     */
    private String forTextByDevice(List<ZkDeviceRecordDO> recordDOS) {
        StringBuilder builder = new StringBuilder("存塘传感器指标信息：\n");
        for(ZkDeviceRecordDO recordDO : recordDOS) {
            if(recordDO.getMetricId().contains("do_nh4")) builder.append("NH4").append(":").append(recordDO.getMetricValue()).append(";");
            if(recordDO.getMetricId().contains("do_oxygen")) builder.append("溶解氧").append(":").append(recordDO.getMetricValue()).append(";");
            if(recordDO.getMetricId().contains("do_temp")) builder.append("温度").append(":").append(recordDO.getMetricValue()).append(";");
            if(recordDO.getMetricId().contains("do_oxyper")) builder.append("氧百分比").append(":").append(recordDO.getMetricValue()).append(";");
            if(recordDO.getMetricId().contains("ph_ph")) builder.append("ph").append(":").append(recordDO.getMetricValue()).append(";");
            if(recordDO.getMetricId().contains("orp_orp")) builder.append("氧化还原电位").append(":").append(recordDO.getMetricValue()).append(";");
            if(recordDO.getMetricId().contains("ntu_ntu")) builder.append("浊度").append(":").append(recordDO.getMetricValue()).append(";");
            if(recordDO.getMetricId().contains("TSs_TSs")) builder.append("总悬浮固体").append(":").append(recordDO.getMetricValue()).append(";");
            if(recordDO.getMetricId().contains("lel_level")) builder.append("水位").append(":").append(recordDO.getMetricValue()).append(";");
        }
        return builder.append("\n").toString();
    }


}
