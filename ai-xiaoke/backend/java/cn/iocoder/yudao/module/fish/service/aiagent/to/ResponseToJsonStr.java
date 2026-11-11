package cn.iocoder.yudao.module.fish.service.aiagent.to;

import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ChatRespVo;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondhealth.ZkPondHealthDO;
import com.alibaba.fastjson.JSON;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

/**
 * 将 check_summary 返回的结果转换成 json字符串
 */
public class ResponseToJsonStr {

    public static String strToJsonStr(ChatRespVo chatRespVo) {
        if(chatRespVo == null) return "";
        if(StringUtil.isEmptyWith0(chatRespVo.getMsg())) return "";
        String[] split = chatRespVo.getMsg().split("```json");
        String info = split.length>1?split[1]:split[0];
        info = info.replaceAll("<br>", "")
            .replaceAll("```json", "").replaceAll("```}", "").replaceAll("```", "");
        return info;
    }


    public static <T> T strToObj(ChatRespVo chatRespVo, Class<T> clazz) {
        String info = strToJsonStr(chatRespVo);
        if(StringUtil.isEmptyWith0(info)) return null;
        System.out.println("================>"+info);
        return JSON.parseObject(info, clazz);
    }

    public static void main(String[] args) {
        String str = "```json<br>{<br>    \"environmentalLayerInformation\": {<br>        \"doOxygen\": \"9.070000\",<br>        \"ammoniaNitrogen\": \"0.010000\",<br>        \"phPh\": \"8.580000\",<br>        \"doTemp\": \"16.810000\"<br>    },<br>    \"behavioralLayerInformation\": {<br>        \"foodSituation\": \"强\",<br>        \"swimmingState\": \"正常\",<br>        \"deadFishSituation\": \"无\"<br>    },<br>    \"bodyLayerInformation\": {<br>        \"gillFilaments\": \"腮丝肿胀\",<br>        \"intestine\": \"正常\",<br>        \"liverAndGallbladder\": \"充血\"<br>    }<br>}<br>```";
        ChatRespVo chatRespVo = new ChatRespVo().setMsg(str);
        String s = strToJsonStr(chatRespVo);
        System.out.println(s);
        ZkPondHealthDO zkPondHealthDO = JSON.parseObject(s, ZkPondHealthDO.class);
        System.out.println(JSON.toJSONString(zkPondHealthDO));
    }

}
